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

package utils.cyaHelpers

import config.FrontendAppConfig
import models.{Mode, UserAnswers}
import pages.ActingAsRepresentativePage
import play.api.i18n.Messages
import pages.representative._
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class RepresentativeCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages, config: FrontendAppConfig) extends AnswersHelper(userAnswers, mode) {

  def actingAsRepresentative: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ActingAsRepresentativePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.actingRepresentative",
    id = Some("change-has-acting-representative")
  )

  def eori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.representative.eori",
    id = Some("change-representative-eori-number")
  )

  def addDetails: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddDetailsPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.representative.addDetails",
    id = Some("change-add-details")
  )

  def name: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.representative.name",
    id = Some("change-representative-name")
  )

  def phoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.representative.telephoneNumber",
    id = Some("change-representative-phone-number")
  )
}
