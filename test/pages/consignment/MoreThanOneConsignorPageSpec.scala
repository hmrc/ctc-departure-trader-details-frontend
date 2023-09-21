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

import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.consignment.consignor._

class MoreThanOneConsignorPageSpec extends PageBehaviours {

  "MoreThanOneConsignorPage" - {

    beRetrievable[Boolean](MoreThanOneConsignorPage)

    beSettable[Boolean](MoreThanOneConsignorPage)

    beRemovable[Boolean](MoreThanOneConsignorPage)

    "cleanup" - {
      val eori    = arbitrary[EoriNumber].sample.value
      val name    = Gen.alphaNumStr.sample.value
      val country = arbitrary[Country].sample.value
      val address = arbitrary[DynamicAddress].sample.value

      "when Yes selected" - {
        "must clean up Consignor pages" in {
          val preChange = emptyUserAnswers
            .setValue(EoriYesNoPage, true)
            .setValue(EoriPage, eori.value)
            .setValue(NamePage, name)
            .setValue(CountryPage, country)
            .setValue(AddressPage, address)

          val postChange = preChange.setValue(MoreThanOneConsignorPage, true)

          postChange.get(EoriPage) mustNot be(defined)
          postChange.get(EoriYesNoPage) mustNot be(defined)
          postChange.get(NamePage) mustNot be(defined)
          postChange.get(CountryPage) mustNot be(defined)
          postChange.get(AddressPage) mustNot be(defined)
        }
      }

      "when NO selected" - {
        "must do nothing" in {
          val preChange = emptyUserAnswers
            .setValue(EoriYesNoPage, true)
            .setValue(EoriPage, eori.value)
            .setValue(NamePage, name)
            .setValue(CountryPage, country)
            .setValue(AddressPage, address)

          val postChange = preChange.setValue(MoreThanOneConsignorPage, false)

          postChange.get(EoriPage) must be(defined)
          postChange.get(EoriYesNoPage) must be(defined)
          postChange.get(NamePage) must be(defined)
          postChange.get(CountryPage) must be(defined)
          postChange.get(AddressPage) must be(defined)
        }
      }
    }
  }
}
