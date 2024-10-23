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

import config.Constants.SecurityType.NoSecurityDetails
import models.DynamicAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.consignment.consignor.*
import pages.external.*
import play.api.libs.json.{JsArray, Json}

class ApprovedOperatorPageSpec extends PageBehaviours {

  private val address = arbitrary[DynamicAddress].sample.value
  private val array   = JsArray(Seq(Json.obj("foo" -> "bar")))

  "ApprovedOperatorPage" - {

    beRetrievable[Boolean](ApprovedOperatorPage)

    beSettable[Boolean](ApprovedOperatorPage)

    beRemovable[Boolean](ApprovedOperatorPage)

    "cleanup" - {

      "when Yes selected and we have No Security Details" - {
        "must clean up Consignor pages, authorisations and items" in {
          forAll(arbitrary[String]) {
            eori =>
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(ApprovedOperatorPage, false)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, address)
                .setValue(AuthorisationsAndLimitSection, array)
                .setValue(ItemsSection, array)

              val result = userAnswers.setValue(ApprovedOperatorPage, true)

              result.get(EoriPage).isDefined mustBe false
              result.get(EoriYesNoPage).isDefined mustBe false
              result.get(NamePage).isDefined mustBe false
              result.get(AddressPage).isDefined mustBe false
              result.get(AuthorisationsAndLimitSection).isDefined mustBe false
              result.get(ItemsSection).isDefined mustBe false
          }
        }
      }

      "when No selected and we have No Security Details" - {
        "must clean up authorisations and items" in {
          forAll(arbitrary[String]) {
            eori =>
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(ApprovedOperatorPage, true)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, address)
                .setValue(AuthorisationsAndLimitSection, array)
                .setValue(ItemsSection, array)

              val result = userAnswers.setValue(ApprovedOperatorPage, false)

              result.get(EoriPage).isDefined mustBe true
              result.get(EoriYesNoPage).isDefined mustBe true
              result.get(NamePage).isDefined mustBe true
              result.get(AddressPage).isDefined mustBe true
              result.get(AuthorisationsAndLimitSection).isDefined mustBe false
              result.get(ItemsSection).isDefined mustBe false
          }
        }
      }

      "when Yes selected and we have Security Details" - {
        "must clean up authorisations and items" in {
          forAll(arbitrary[String](arbitrarySomeSecurityDetailsType), arbitrary[String]) {
            (securityType, eori) =>
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, securityType)
                .setValue(ApprovedOperatorPage, false)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, address)
                .setValue(AuthorisationsAndLimitSection, array)
                .setValue(ItemsSection, array)

              val result = userAnswers.setValue(ApprovedOperatorPage, true)

              result.get(EoriPage).isDefined mustBe true
              result.get(EoriYesNoPage).isDefined mustBe true
              result.get(NamePage).isDefined mustBe true
              result.get(AddressPage).isDefined mustBe true
              result.get(AuthorisationsAndLimitSection).isDefined mustBe false
              result.get(ItemsSection).isDefined mustBe false
          }
        }
      }

      "when Yes selected and haven't populated the security details type" - {
        "must clean up authorisations and items" in {
          forAll(arbitrary[String]) {
            eori =>
              val userAnswers = emptyUserAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, address)
                .setValue(AuthorisationsAndLimitSection, array)
                .setValue(ItemsSection, array)

              val result = userAnswers.setValue(ApprovedOperatorPage, true)

              result.get(EoriPage).isDefined mustBe true
              result.get(EoriYesNoPage).isDefined mustBe true
              result.get(NamePage).isDefined mustBe true
              result.get(AddressPage).isDefined mustBe true
              result.get(AuthorisationsAndLimitSection).isDefined mustBe false
              result.get(ItemsSection).isDefined mustBe false
          }
        }

        "when the value changes" - {
          "must clean up authorisations and items" in {
            forAll(arbitrary[Boolean]) {
              bool =>
                val userAnswers = emptyUserAnswers
                  .setValue(ApprovedOperatorPage, bool)
                  .setValue(AuthorisationsAndLimitSection, array)
                  .setValue(ItemsSection, array)

                val result = userAnswers.setValue(ApprovedOperatorPage, !bool)

                result.get(AuthorisationsAndLimitSection).isDefined mustBe false
                result.get(ItemsSection).isDefined mustBe false
            }
          }
        }

        "when the value doesn't change" - {
          "must do nothing" in {
            forAll(arbitrary[Boolean]) {
              bool =>
                val userAnswers = emptyUserAnswers
                  .setValue(ApprovedOperatorPage, bool)
                  .setValue(AuthorisationsAndLimitSection, array)
                  .setValue(ItemsSection, array)

                val result = userAnswers.setValue(ApprovedOperatorPage, bool)

                result.get(AuthorisationsAndLimitSection).isDefined mustBe true
                result.get(ItemsSection).isDefined mustBe true
            }
          }
        }
      }
    }
  }
}
