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

import models.{Mode, RichJsArray, UserAnswers}
import pages.QuestionPage
import pages.external.{ConsignmentCountryOfDestinationInCL009Page, ItemConsigneeSection, ItemsSection}
import pages.sections.{TraderDetailsConsigneeSection, TraderDetailsConsignmentSection}
import play.api.libs.json.{JsArray, JsPath}
import play.api.mvc.Call

import scala.util.{Success, Try}

case object MoreThanOneConsigneePage extends QuestionPage[Boolean] {

  override def path: JsPath = TraderDetailsConsignmentSection.path \ toString

  override def toString: String = "moreThanOneConsignee"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true)  => userAnswers.remove(TraderDetailsConsigneeSection)
      case Some(false) => removeItemLevelConsignees(userAnswers)
      case _           => super.cleanup(value, userAnswers)
    }

  private def removeItemLevelConsignees(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(ConsignmentCountryOfDestinationInCL009Page) match {
      case Some(true) =>
        userAnswers
          .get(ItemsSection)
          .getOrElse(JsArray())
          .zipWithIndex
          .foldLeft[Try[UserAnswers]](Success(userAnswers)) {
            case (acc, (_, index)) =>
              acc.map(_.remove(ItemConsigneeSection(index)))
          }
      case _ =>
        Success(userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.consignment.routes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode))
}
