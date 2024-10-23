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

import models.{DynamicAddress, EoriNumber, Index}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.consignment.consignee.*
import pages.external.{ConsignmentCountryOfDestinationInCL009Page, ItemConsigneeSection}
import play.api.libs.json.Json

class MoreThanOneConsigneePageSpec extends PageBehaviours {

  "MoreThanOneConsigneePage" - {

    beRetrievable[Boolean](MoreThanOneConsigneePage)

    beSettable[Boolean](MoreThanOneConsigneePage)

    beRemovable[Boolean](MoreThanOneConsigneePage)

    "cleanup" - {
      val consigneeEori    = arbitrary[EoriNumber].sample.value
      val consigneeName    = Gen.alphaNumStr.sample.value
      val consigneeAddress = arbitrary[DynamicAddress].sample.value

      "when Yes selected" - {
        "must clean up Consignee pages" in {
          val preChange = emptyUserAnswers
            .setValue(EoriYesNoPage, true)
            .setValue(EoriNumberPage, consigneeEori.value)
            .setValue(NamePage, consigneeName)
            .setValue(AddressPage, consigneeAddress)
          val postChange = preChange.setValue(MoreThanOneConsigneePage, true)

          postChange.get(EoriNumberPage) mustNot be(defined)
          postChange.get(EoriYesNoPage) mustNot be(defined)
          postChange.get(NamePage) mustNot be(defined)
          postChange.get(AddressPage) mustNot be(defined)
        }
      }

      "when NO selected" - {
        "and country of destination is in CL009" - {
          "must clean up consignee at each item" in {
            val userAnswers = emptyUserAnswers
              .setValue(ConsignmentCountryOfDestinationInCL009Page, true)
              .setValue(ItemConsigneeSection(Index(0)), Json.obj("foo" -> "bar"))
              .setValue(ItemConsigneeSection(Index(1)), Json.obj("foo" -> "bar"))

            val result = userAnswers.setValue(MoreThanOneConsigneePage, false)

            result.get(ItemConsigneeSection(Index(0))) must not be defined
            result.get(ItemConsigneeSection(Index(1))) must not be defined
          }
        }

        "and country of destination is not in CL009" - {
          "must not clean up consignee at each item" in {
            val userAnswers = emptyUserAnswers
              .setValue(ConsignmentCountryOfDestinationInCL009Page, false)
              .setValue(ItemConsigneeSection(Index(0)), Json.obj("foo" -> "bar"))
              .setValue(ItemConsigneeSection(Index(1)), Json.obj("foo" -> "bar"))

            val result = userAnswers.setValue(MoreThanOneConsigneePage, false)

            result.get(ItemConsigneeSection(Index(0))) must be(defined)
            result.get(ItemConsigneeSection(Index(1))) must be(defined)
          }
        }

        "and country of destination is undefined" - {
          "must not clean up consignee at each item" in {
            val userAnswers = emptyUserAnswers
              .setValue(ItemConsigneeSection(Index(0)), Json.obj("foo" -> "bar"))
              .setValue(ItemConsigneeSection(Index(1)), Json.obj("foo" -> "bar"))

            val result = userAnswers.setValue(MoreThanOneConsigneePage, false)

            result.get(ItemConsigneeSection(Index(0))) must be(defined)
            result.get(ItemConsigneeSection(Index(1))) must be(defined)
          }
        }
      }
    }
  }
}
