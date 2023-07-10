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

package pages.consignment.consignee

import models.DynamicAddress
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class EoriYesNoPageSpec extends PageBehaviours {

  "EoriYesNoPage" - {

    beRetrievable[Boolean](EoriYesNoPage)

    beSettable[Boolean](EoriYesNoPage)

    beRemovable[Boolean](EoriYesNoPage)

    "cleanup" - {
      "when false" - {
        "must clean up EoriPage" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange  = emptyUserAnswers.setValue(EoriNumberPage, eori)
              val postChange = preChange.setValue(EoriYesNoPage, false)

              postChange.get(EoriNumberPage) mustNot be(defined)
          }
        }
      }

      "when true selected" - {
        "must clean up name, country and address page" in {
          forAll(arbitrary[String], arbitrary[Country], arbitrary[DynamicAddress]) {
            (name, country, address) =>
              val preChange = emptyUserAnswers
                .setValue(NamePage, name)
                .setValue(CountryPage, country)
                .setValue(AddressPage, address)

              val postChange = preChange.setValue(EoriYesNoPage, true)

              postChange.get(NamePage) mustNot be(defined)
              postChange.get(CountryPage) mustNot be(defined)
              postChange.get(AddressPage) mustNot be(defined)
          }
        }
      }
    }

  }
}
