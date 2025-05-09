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
import models.{DynamicAddress, Mode, UserAnswers}
import pages.consignment.*
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class ConsignmentCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def consignorEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.eoriYesNo",
    id = Some("change-has-consignor-eori")
  )

  def consignorEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.EoriPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.eori",
    id = Some("change-consignor-eori-number")
  )

  def consignorName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.name",
    id = Some("change-consignor-name")
  )

  def consignorCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = consignor.CountryPage,
    formatAnswer = formatAsCountry,
    prefix = "traderDetails.consignment.consignor.country",
    id = Some("change-consignor-country")
  )

  def consignorAddress: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = consignor.AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "traderDetails.consignment.consignor.address",
    id = Some("change-consignor-address")
  )

  def addConsignorContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.addContact",
    id = Some("change-has-consignor-contact")
  )

  def consignorContactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.contact.name",
    id = Some("change-consignor-contact-name")
  )

  def consignorContactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.contact.telephoneNumber",
    id = Some("change-consignor-contact-phone-number")
  )

  def consigneeEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignee.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignee.eoriYesNo",
    id = Some("change-has-consignee-eori")
  )

  def consigneeEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignee.EoriNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignee.eoriNumber",
    id = Some("change-consignee-eori-number")
  )

  def consigneeName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignee.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignee.name",
    id = Some("change-consignee-name")
  )

  def consigneeCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = consignee.CountryPage,
    formatAnswer = formatAsCountry,
    prefix = "traderDetails.consignment.consignee.country",
    id = Some("change-consignee-country")
  )

  def consigneeAddress: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = consignee.AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "traderDetails.consignment.consignee.address",
    id = Some("change-consignee-address")
  )
}
