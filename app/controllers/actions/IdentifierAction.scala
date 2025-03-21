/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.EnrolmentStoreConnector
import models.EoriNumber
import models.requests.IdentifierRequest
import play.api.mvc.Results.*
import play.api.mvc.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class IdentifierActionImpl @Inject() (
  override val authConnector: AuthConnector,
  config: FrontendAppConfig,
  val parser: BodyParsers.Default,
  enrolmentStoreConnector: EnrolmentStoreConnector
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions {

  // scalastyle:off cyclomatic.complexity
  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter
      .fromRequestAndSession(request, request.session)

    authorised(EmptyPredicate)
      .retrieve(Retrievals.allEnrolments and Retrievals.groupIdentifier) {
        case enrolments ~ maybeGroupId =>
          enrolments.enrolments.filter(_.isActivated).find(_.key.equals(config.enrolmentKey)) match {
            case Some(enrolment) =>
              enrolment.getIdentifier(config.enrolmentIdentifierKey) match {
                case Some(enrolmentIdentifier) =>
                  block(IdentifierRequest(request, EoriNumber(enrolmentIdentifier.value)))
                case None =>
                  Future.successful(Redirect(config.unauthorisedUrl))
              }
            case None =>
              maybeGroupId match {
                case Some(groupId) =>
                  enrolmentStoreConnector.checkGroupEnrolments(groupId, config.enrolmentKey).map {
                    case true  => Redirect(config.unauthorisedWithGroupAccessUrl)
                    case false => Redirect(config.eccEnrolmentSplashPage)
                  }
                case _ => Future.successful(Redirect(config.eccEnrolmentSplashPage))
              }
          }
      }
  } recover {
    case _: NoActiveSession =>
      Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
    case _: AuthorisationException =>
      Redirect(config.unauthorisedUrl)
  }
  // scalastyle:on cyclomatic.complexity

}
