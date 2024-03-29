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

package pages.holderOfTransit

import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.HolderOfTransitSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object EoriYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = HolderOfTransitSection.path \ toString

  override def toString: String = "eoriYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => userAnswers.remove(EoriPage)
      case Some(true) =>
        for {
          ua1 <- userAnswers.remove(NamePage)
          ua2 <- ua1.remove(CountryPage)
          ua3 <- ua2.remove(AddressPage)
        } yield ua3
      case _ => super.cleanup(value, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.holderOfTransit.routes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode))
}
