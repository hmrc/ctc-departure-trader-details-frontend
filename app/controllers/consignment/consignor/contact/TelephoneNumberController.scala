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

package controllers.consignment.consignor.contact

import controllers.actions.{Actions, SpecificDataRequiredActionProvider}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.TelephoneNumberFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{LocalReferenceNumber, Mode}
import navigation.{TraderDetailsNavigatorProvider, UserAnswersNavigator}
import pages.consignment.consignor.contact.{NamePage, TelephoneNumberPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.consignment.consignor.contact.TelephoneNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TelephoneNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: TraderDetailsNavigatorProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: TelephoneNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: TelephoneNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(implicit request: Request): Form[String] =
    formProvider("traderDetails.consignment.consignor.contact.telephoneNumber", contactName)

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[?]

  private def contactName(implicit request: Request): String = request.arg

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(NamePage)) {
      implicit request =>
        val preparedForm = request.userAnswers.get(TelephoneNumberPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, lrn, mode, contactName))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(NamePage))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, contactName))),
            value => {
              val navigator: UserAnswersNavigator = navigatorProvider(mode)
              TelephoneNumberPage.writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
            }
          )
    }
}
