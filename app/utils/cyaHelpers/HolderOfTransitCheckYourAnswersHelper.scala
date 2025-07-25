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
import models.reference.Country
import models.{DynamicAddress, Mode, SubmissionState, UserAnswers}
import pages.holderOfTransit.*
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class HolderOfTransitCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  override val showChangeLink: Boolean = userAnswers.status != SubmissionState.Amendment

  def tirIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TirIdentificationPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.tirIdentification",
    id = Some("change-transit-holder-tir-id-number")
  )

  def eoriYesNo: Option[SummaryListRow] =
    getAnswerAndBuildRow[Boolean](
      page = EoriYesNoPage,
      formatAnswer = formatAsYesOrNo,
      prefix = "traderDetails.holderOfTransit.eoriYesNo",
      id = Some("change-has-transit-holder-eori")
    )

  def eori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.eori",
    id = Some("change-transit-holder-eori-number")
  )

  def name: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.name",
    id = Some("change-transit-holder-name")
  )

  def country: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryPage,
    formatAnswer = formatAsCountry,
    prefix = "traderDetails.holderOfTransit.country",
    id = Some("change-transit-holder-country")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "traderDetails.holderOfTransit.address",
    id = Some("change-transit-holder-address")
  )

  def addContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.addContact",
    id = Some("change-has-transit-holder-contact")
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = contact.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.contact.name",
    id = Some("change-transit-holder-contact-name")
  )

  def contactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = contact.TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.contact.telephoneNumber",
    id = Some("change-transit-holder-contact-phone-number")
  )
}
