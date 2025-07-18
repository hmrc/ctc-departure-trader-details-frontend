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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.traderDetails.TraderDetailsViewModel.TraderDetailsViewModelProvider

class TraderDetailsViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {
    "must return all sections" in {
      forAll(arbitraryTraderDetailsAnswers(emptyUserAnswers)) {
        answers =>
          val viewModelProvider = injector.instanceOf[TraderDetailsViewModelProvider]
          val sections          = viewModelProvider.apply(answers).sections

          sections.size mustEqual 7
          sections.head.sectionTitle must not be defined
          sections(1).sectionTitle.get mustEqual "Transit holder"
          sections(2).sectionTitle.get mustEqual "Additional contact"
          sections(3).sectionTitle.get mustEqual "Representative"
          sections(4).sectionTitle.get mustEqual "Consignor"
          sections(5).sectionTitle.get mustEqual "Consignor contact"
          sections(6).sectionTitle.get mustEqual "Consignee"
      }
    }
  }
}
