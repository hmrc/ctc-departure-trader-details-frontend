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
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import pages.consignment.ApprovedOperatorPage
import viewModels.traderDetails.HeaderViewModel.HeaderViewModelProvider

class HeaderViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[HeaderViewModelProvider]
        val sections          = viewModelProvider.apply(emptyUserAnswers, mode).sections

        sections.size mustEqual 1

        sections.head.sectionTitle must not be defined
        sections.head.rows must be(empty)
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, false)

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[HeaderViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode).sections

        sections.size mustEqual 1

        sections.head.sectionTitle must not be defined
        sections.head.rows.size mustEqual 1
        sections.head.rows.head.value.content.asHtml.toString() mustEqual "No"
      }
    }
  }
}
