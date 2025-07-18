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
import config.Constants.DeclarationType.TIR
import config.Constants.SecurityType.NoSecurityDetails
import generators.Generators
import models.journeyDomain.consignment.ConsignmentConsigneeDomain.ConsigneeWithEori
import models.journeyDomain.consignment.ConsignmentDomain
import models.journeyDomain.holderOfTransit.HolderOfTransitDomain.HolderOfTransitWithoutEori
import models.reference.Country
import models.{DynamicAddress, EoriNumber, SubmissionState}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.consignment.*
import pages.external.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.sections.TraderDetailsSection
import pages.{holderOfTransit => hot, ActingAsRepresentativePage}

class TraderDetailsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TraderDetailsDomain" - {

    "must redirect to the correct domain" - {
      "when status is Amended" in {
        val userAnswers = emptyUserAnswers
          .copy(status = SubmissionState.Amendment)
          .setValue(DeclarationTypePage, TIR)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)

        val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

        result.left.value.page mustEqual consignor.EoriYesNoPage
        result.left.value.pages mustEqual Seq(
          consignor.EoriYesNoPage
        )
      }

      "when status is not Amended" in {
        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)

        val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

        result.left.value.page mustEqual hot.EoriYesNoPage
        result.left.value.pages mustEqual Seq(
          hot.EoriYesNoPage
        )
      }
    }

    val eoriNumber       = Gen.alphaNumStr.sample.value
    val someSecurityType = arbitrary[String](arbitrarySomeSecurityDetailsType).sample.value

    "TraderDetailsDomainDefault" - {

      val holderOfTransitName       = Gen.alphaNumStr.sample.value
      val holderOfTransitCountry    = arbitrary[Country].sample.value
      val holderOfTransitAddress    = arbitrary[DynamicAddress].sample.value
      val someSecurityType          = arbitrary[String](arbitrarySomeSecurityDetailsType).sample.value
      val nonOption4DeclarationType = arbitrary[String](arbitraryNonTIRDeclarationType).sample.value
      val tirNumber                 = Gen.alphaNumStr.sample.value

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
            .unsafeSetVal(consignee.EoriYesNoPage)(true)
            .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

          val expectedResult = TraderDetailsDomainDefault(
            holderOfTransit = HolderOfTransitWithoutEori(
              name = holderOfTransitName,
              country = holderOfTransitCountry,
              address = holderOfTransitAddress,
              additionalContact = None,
              tir = None
            ),
            representative = None,
            consignment = ConsignmentDomain(
              consignor = None,
              consignee = Some(ConsigneeWithEori(EoriNumber(eoriNumber)))
            )
          )

          val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

          result.value.value mustEqual expectedResult
          result.value.pages mustEqual Seq(
            hot.EoriYesNoPage,
            hot.NamePage,
            hot.CountryPage,
            hot.AddressPage,
            hot.AddContactPage,
            ActingAsRepresentativePage,
            ApprovedOperatorPage,
            consignee.EoriYesNoPage,
            consignee.EoriNumberPage,
            TraderDetailsSection
          )
        }
      }

      "cannot be parsed from UserAnswers" - {

        "when ActingAsRepresentativePage is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, TIR)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .unsafeSetVal(hot.EoriYesNoPage)(false)
            .unsafeSetVal(hot.NamePage)(holderOfTransitName)
            .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
            .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
            .unsafeSetVal(hot.TirIdentificationPage)(tirNumber)
            .unsafeSetVal(hot.AddContactPage)(false)

          val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

          result.left.value.page mustEqual ActingAsRepresentativePage
          result.left.value.pages mustEqual Seq(
            hot.EoriYesNoPage,
            hot.NamePage,
            hot.CountryPage,
            hot.AddressPage,
            hot.AddContactPage,
            hot.TirIdentificationPage,
            ActingAsRepresentativePage
          )
        }

        "when TIR declaration type" - {
          "and consignor EORI yes/no is missing" in {

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, TIR)
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .unsafeSetVal(hot.EoriYesNoPage)(false)
              .unsafeSetVal(hot.TirIdentificationPage)(tirNumber)
              .unsafeSetVal(hot.NamePage)(holderOfTransitName)
              .unsafeSetVal(hot.CountryPage)(holderOfTransitCountry)
              .unsafeSetVal(hot.AddressPage)(holderOfTransitAddress)
              .unsafeSetVal(hot.AddContactPage)(false)
              .unsafeSetVal(ActingAsRepresentativePage)(false)

            val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

            result.left.value.page mustEqual consignor.EoriYesNoPage
            result.left.value.pages mustEqual Seq(
              hot.EoriYesNoPage,
              hot.NamePage,
              hot.CountryPage,
              hot.AddressPage,
              hot.AddContactPage,
              hot.TirIdentificationPage,
              ActingAsRepresentativePage,
              consignor.EoriYesNoPage
            )
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

            val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

            result.left.value.page mustEqual ApprovedOperatorPage
            result.left.value.pages mustEqual Seq(
              hot.EoriYesNoPage,
              hot.NamePage,
              hot.CountryPage,
              hot.AddressPage,
              hot.AddContactPage,
              ActingAsRepresentativePage,
              ApprovedOperatorPage
            )
          }

          "and there is no security" - {

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

                val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

                result.left.value.page mustEqual consignor.EoriYesNoPage
                result.left.value.pages mustEqual Seq(
                  hot.EoriYesNoPage,
                  hot.NamePage,
                  hot.CountryPage,
                  hot.AddressPage,
                  hot.AddContactPage,
                  ActingAsRepresentativePage,
                  ApprovedOperatorPage,
                  consignor.EoriYesNoPage
                )
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

              val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

              result.left.value.page mustEqual consignor.EoriYesNoPage
              result.left.value.pages mustEqual Seq(
                hot.EoriYesNoPage,
                hot.NamePage,
                hot.CountryPage,
                hot.AddressPage,
                hot.AddContactPage,
                ActingAsRepresentativePage,
                ApprovedOperatorPage,
                consignor.EoriYesNoPage
              )
            }
          }
        }
      }
    }

    "TraderDetailsDomainAmending" - {

      "can be parsed from UserAnswers" in {

        val userAnswers = emptyUserAnswers
          .copy(status = SubmissionState.Amendment)
          .setValue(DeclarationTypePage, someSecurityType)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

        val expectedResult = TraderDetailsDomainAmending(
          consignment = ConsignmentDomain(
            consignor = None,
            consignee = Some(ConsigneeWithEori(EoriNumber(eoriNumber)))
          )
        )

        val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          ApprovedOperatorPage,
          consignee.EoriYesNoPage,
          consignee.EoriNumberPage,
          TraderDetailsSection
        )
      }

      "cannot be parsed from UserAnswers" - {
        "when add consignee EORI yes/no is unanswered" in {
          val userAnswers = emptyUserAnswers
            .copy(status = SubmissionState.Amendment)
            .setValue(DeclarationTypePage, someSecurityType)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .unsafeSetVal(ApprovedOperatorPage)(true)
            .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)
            .unsafeSetVal(consignor.EoriYesNoPage)(true)
            .unsafeSetVal(consignor.EoriPage)(eoriNumber)
            .unsafeSetVal(consignor.AddContactPage)(false)

          val result = TraderDetailsDomain.userAnswersReader.run(userAnswers)

          result.left.value.page mustEqual consignee.EoriYesNoPage
          result.left.value.pages mustEqual Seq(
            ApprovedOperatorPage,
            consignee.EoriYesNoPage
          )
        }
      }
    }
  }
}
