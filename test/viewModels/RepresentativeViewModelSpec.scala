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
import pages.representative.*
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import pages.ActingAsRepresentativePage
import viewModels.traderDetails.RepresentativeViewModel.RepresentativeViewModelProvider

class RepresentativeViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[RepresentativeViewModelProvider]
        val sections          = viewModelProvider.apply(emptyUserAnswers, mode).sections

        sections.size mustEqual 1

        sections.head.sectionTitle.get mustEqual "Representative"
        sections.head.rows must be(empty)
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .setValue(ActingAsRepresentativePage, true)
          .setValue(EoriPage, "eori")
          .setValue(AddDetailsPage, true)
          .setValue(NamePage, "name")
          .setValue(TelephoneNumberPage, "phone")

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[RepresentativeViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode).sections

        sections.size mustEqual 1

        sections.head.sectionTitle.get mustEqual "Representative"
        sections.head.rows.size mustEqual 5
        sections.head.rows.head.value.content.asHtml.toString() mustEqual "Yes"
        sections.head.rows(1).value.content.asHtml.toString() mustEqual "eori"
        sections.head.rows(2).value.content.asHtml.toString() mustEqual "Yes"
        sections.head.rows(3).value.content.asHtml.toString() mustEqual "name"
        sections.head.rows(4).value.content.asHtml.toString() mustEqual "phone"
      }
    }
  }
}
