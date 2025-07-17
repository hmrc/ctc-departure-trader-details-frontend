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

package viewModels

import base.SpecBase
import generators.Generators
import pages.consignment._
import models.reference.{Country, CountryCode}
import models.{DynamicAddress, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.traderDetails.ConsignmentViewModel.ConsignmentViewModelProvider

class ConsignmentViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[ConsignmentViewModelProvider]
        val sections          = viewModelProvider.apply(emptyUserAnswers, mode).sections

        sections.size mustEqual 3

        sections.head.sectionTitle.get mustEqual "Consignor"
        sections.head.rows must be(empty)

        sections(1).sectionTitle.get mustEqual "Consignor contact"
        sections(1).rows must be(empty)

        sections(2).sectionTitle.get mustEqual "Consignee"
        sections(2).rows must be(empty)
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .setValue(consignor.EoriYesNoPage, true)
          .setValue(consignor.EoriPage, "eori")
          .setValue(consignor.NamePage, "name")
          .setValue(consignor.CountryPage, Country(CountryCode("GB"), "Great Britain"))
          .setValue(consignor.AddressPage, DynamicAddress("line1", "line2", Some("postal code")))
          .setValue(consignor.AddContactPage, true)
          .setValue(consignor.contact.NamePage, "contact name")
          .setValue(consignor.contact.TelephoneNumberPage, "phone number")
          .setValue(consignee.EoriYesNoPage, true)
          .setValue(consignee.EoriNumberPage, "eori2")
          .setValue(consignee.NamePage, "name2")
          .setValue(consignee.CountryPage, Country(CountryCode("GB"), "Great Britain"))
          .setValue(consignee.AddressPage, DynamicAddress("line11", "line12", Some("postal code2")))

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[ConsignmentViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode).sections

        sections.size mustEqual 3

        sections.head.sectionTitle.get mustEqual "Consignor"
        sections.head.rows.size mustEqual 5
        sections.head.rows.head.value.content.asHtml.toString() mustEqual "Yes"
        sections.head.rows(1).value.content.asHtml.toString() mustEqual "eori"
        sections.head.rows(2).value.content.asHtml.toString() mustEqual "name"
        sections.head.rows(3).value.content.asHtml.toString() mustEqual "Great Britain - GB"
        sections.head.rows(4).value.content.asHtml.toString() mustEqual "line1<br>line2<br>postal code"

        sections(1).sectionTitle.get mustEqual "Consignor contact"
        sections(1).rows.size mustEqual 3
        sections(1).rows.head.value.content.asHtml.toString() mustEqual "Yes"
        sections(1).rows(1).value.content.asHtml.toString() mustEqual "contact name"
        sections(1).rows(2).value.content.asHtml.toString() mustEqual "phone number"

        sections(2).sectionTitle.get mustEqual "Consignee"
        sections(2).rows.size mustEqual 5
        sections(2).rows.head.value.content.asHtml.toString() mustEqual "Yes"
        sections(2).rows(1).value.content.asHtml.toString() mustEqual "eori2"
        sections(2).rows(2).value.content.asHtml.toString() mustEqual "name2"
        sections(2).rows(3).value.content.asHtml.toString() mustEqual "Great Britain - GB"
        sections(2).rows(4).value.content.asHtml.toString() mustEqual "line11<br>line12<br>postal code2"
      }
    }
  }
}
