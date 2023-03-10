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

package models.journeyDomain

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.SecurityDetailsType.NoSecurityDetails
import models.journeyDomain.consignment.ConsignmentDomain
import models.journeyDomain.holderOfTransit.HolderOfTransitDomain.HolderOfTransitEori
import models.reference.Country
import models.{DeclarationType, DynamicAddress, SecurityDetailsType}
import pages.consignment._
import pages.{ActingAsRepresentativePage, holderOfTransit => hot}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.external.{DeclarationTypePage, SecurityDetailsTypePage}

class TraderDetailsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TraderDetailsDomain" - {

    val nonOption4DeclarationType = arbitrary[DeclarationType](arbitraryNonTIRDeclarationType).sample.value
    val holderOfTransitName       = Gen.alphaNumStr.sample.value
    val holderOfTransitCountry    = arbitrary[Country].sample.value
    val holderOfTransitAddress    = arbitrary[DynamicAddress].sample.value
    val someSecurityType          = arbitrary[SecurityDetailsType](arbitrarySecurityDetailsType).sample.value

    "can be parsed from UserAnswers" - {

      "when has the minimum fields complete" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonOption4DeclarationType)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(hot.EoriYesNoPage)(false)
          .unsafeSetVal(hot.NamePage)(holderOfTransitName)
          .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
          .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
          .unsafeSetVal(hot.AddContactPage)(false)
          .unsafeSetVal(ActingAsRepresentativePage)(false)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(MoreThanOneConsigneePage)(true)

        val expectedResult = TraderDetailsDomain(
          holderOfTransit = HolderOfTransitEori(
            eori = None,
            name = holderOfTransitName,
            country = holderOfTransitCountry,
            address = holderOfTransitAddress,
            additionalContact = None
          ),
          representative = None,
          consignment = ConsignmentDomain(
            consignor = None,
            consignee = None
          )
        )

        val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when ActingAsRepresentativePage is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, DeclarationType.Option4)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(hot.TirIdentificationYesNoPage)(false)
          .unsafeSetVal(hot.NamePage)(holderOfTransitName)
          .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
          .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
          .unsafeSetVal(hot.AddContactPage)(false)

        val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

        result.left.value.page mustBe ActingAsRepresentativePage
      }

      "when TIR declaration type" - {
        "and consignor EORI yes/no is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, DeclarationType.Option4)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .unsafeSetVal(hot.TirIdentificationYesNoPage)(false)
            .unsafeSetVal(hot.NamePage)(holderOfTransitName)
            .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
            .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
            .unsafeSetVal(hot.AddContactPage)(false)
            .unsafeSetVal(ActingAsRepresentativePage)(false)

          val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

          result.left.value.page mustBe consignor.EoriYesNoPage
        }
      }

      "when non-TIR declaration type" - {
        "and reduced data set is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, nonOption4DeclarationType)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .unsafeSetVal(hot.EoriYesNoPage)(false)
            .unsafeSetVal(hot.NamePage)(holderOfTransitName)
            .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
            .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
            .unsafeSetVal(hot.AddContactPage)(false)
            .unsafeSetVal(ActingAsRepresentativePage)(false)

          val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

          result.left.value.page mustBe ApprovedOperatorPage
        }

        "and there is no security" - {
          "and using a reduced data set" - {
            "and more than one consignee is missing" in {

              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage, nonOption4DeclarationType)
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .unsafeSetVal(hot.EoriYesNoPage)(false)
                .unsafeSetVal(hot.NamePage)(holderOfTransitName)
                .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
                .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
                .unsafeSetVal(hot.AddContactPage)(false)
                .unsafeSetVal(ActingAsRepresentativePage)(false)
                .unsafeSetVal(ApprovedOperatorPage)(true)

              val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

              result.left.value.page mustBe MoreThanOneConsigneePage
            }
          }

          "and not using a reduced data set" - {
            "and consignor EORI yes/no is missing" in {

              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage, nonOption4DeclarationType)
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .unsafeSetVal(hot.EoriYesNoPage)(false)
                .unsafeSetVal(hot.NamePage)(holderOfTransitName)
                .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
                .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
                .unsafeSetVal(hot.AddContactPage)(false)
                .unsafeSetVal(ActingAsRepresentativePage)(false)
                .unsafeSetVal(ApprovedOperatorPage)(false)

              val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

              result.left.value.page mustBe consignor.EoriYesNoPage
            }
          }
        }

        "and there is security" - {
          "and consignor EORI yes/no is missing" in {

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, nonOption4DeclarationType)
              .setValue(SecurityDetailsTypePage, someSecurityType)
              .unsafeSetVal(hot.EoriYesNoPage)(false)
              .unsafeSetVal(hot.NamePage)(holderOfTransitName)
              .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
              .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
              .unsafeSetVal(hot.AddContactPage)(false)
              .unsafeSetVal(ActingAsRepresentativePage)(false)
              .unsafeSetVal(ApprovedOperatorPage)(arbitrary[Boolean].sample.value)

            val result: EitherType[TraderDetailsDomain] = UserAnswersReader[TraderDetailsDomain].run(userAnswers)

            result.left.value.page mustBe consignor.EoriYesNoPage
          }
        }
      }
    }
  }
}
