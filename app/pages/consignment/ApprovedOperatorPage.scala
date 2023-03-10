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

package pages.consignment

import models.DeclarationType.Option4
import models.SecurityDetailsType.NoSecurityDetails
import models.journeyDomain.{GettableAsReaderOps, UserAnswersReader}
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.external.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.sections.{TraderDetailsConsignmentSection, TraderDetailsConsignorSection}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ApprovedOperatorPage extends QuestionPage[Boolean] {

  override def path: JsPath = TraderDetailsConsignmentSection.path \ toString

  override def toString: String = "approvedOperator"

  // TODO - this answer affects transport details authorisation nav.
  //  what cleanup logic do we need?
  //  if we split out into separate FEs we'll need endpoints to do this cleanup in the backend cache
  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    (userAnswers.get(SecurityDetailsTypePage), value) match {
      case (Some(NoSecurityDetails), Some(true)) => userAnswers.remove(TraderDetailsConsignorSection)
      case _                                     => super.cleanup(value, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.consignment.routes.ApprovedOperatorController.onPageLoad(userAnswers.lrn, mode))

  def inferredReader: UserAnswersReader[Boolean] = DeclarationTypePage.reader.flatMap {
    case Option4 => UserAnswersReader(false)
    case _       => ApprovedOperatorPage.reader
  }
}
