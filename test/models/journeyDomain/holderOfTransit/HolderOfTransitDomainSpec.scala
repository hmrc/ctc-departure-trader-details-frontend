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

package models.journeyDomain.holderOfTransit

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import config.Constants.DeclarationType.TIR
import generators.Generators
import models.journeyDomain.holderOfTransit.HolderOfTransitDomain.{HolderOfTransitWithEori, HolderOfTransitWithoutEori}
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.external.DeclarationTypePage
import pages.holderOfTransit._

class HolderOfTransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val eori                  = arbitrary[EoriNumber].sample.value
  private val name                  = Gen.alphaNumStr.sample.value
  private val country               = arbitrary[Country].sample.value
  private val address               = arbitrary[DynamicAddress].sample.value
  private val contactName           = Gen.alphaNumStr.sample.value
  private val contactPhone          = Gen.alphaNumStr.sample.value
  private val tirNumber             = Gen.alphaNumStr.sample.value
  private val nonTIRDeclarationType = arbitrary[String](arbitraryNonTIRDeclarationType).sample.value

  "HolderOfTransitDomain" - {

    "can be parsed from UserAnswers" - {

      "when EORI is defined" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonTIRDeclarationType)
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.value mustBe an[HolderOfTransitWithEori]
      }

      "when EORI is not defined" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonTIRDeclarationType)
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)
          .unsafeSetVal(TirIdentificationYesNoPage)(false)

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.value mustBe an[HolderOfTransitWithoutEori]
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when EoriYesNoPage is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.left.value.page mustBe EoriYesNoPage
      }
    }

  }

  "HolderOfTransitWithEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered for non TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonTIRDeclarationType)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithEori(eori, None, None)

        result.value mustBe expectedResult

      }

      "when all mandatory pages are answered for TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(false)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithEori(eori, None, Some(tirNumber))

        result.value mustBe expectedResult

      }

      "when all optional pages are answered for non TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonTIRDeclarationType)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithEori(
          eori,
          Some(AdditionalContactDomain(contactName, contactPhone)),
          None
        )

        result.value mustBe expectedResult
      }

      "when all optional pages are answered for TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithEori(
          eori,
          Some(AdditionalContactDomain(contactName, contactPhone)),
          Some(tirNumber)
        )

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          EoriPage,
          AddContactPage,
          TirIdentificationPage
        )

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when contact details are missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(TirIdentificationYesNoPage)(true)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        result.left.value.page mustBe contact.NamePage
      }

      "when DeclarationType is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        result.left.value.page mustBe DeclarationTypePage
      }

      "when TIR identification is missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)

        val result: EitherType[HolderOfTransitWithEori] = UserAnswersReader[HolderOfTransitWithEori].run(userAnswers)

        result.left.value.page mustBe TirIdentificationPage
      }
    }
  }

  "HolderOfTransitWithoutEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered for TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithoutEori(name, country, address, None, Some(tirNumber))

        result.value mustBe expectedResult

      }

      "when all mandatory pages are answered for non TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonTIRDeclarationType)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithoutEori(name, country, address, None, None)

        result.value mustBe expectedResult

      }

      "when all optional pages are answered for TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithoutEori(
          name,
          country,
          address,
          Some(AdditionalContactDomain(contactName, contactPhone)),
          Some(tirNumber)
        )

        result.value mustBe expectedResult

      }

      "when all optional pages are answered for non TIR" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonTIRDeclarationType)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        val expectedResult = HolderOfTransitWithoutEori(
          name,
          country,
          address,
          Some(AdditionalContactDomain(contactName, contactPhone)),
          None
        )

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          NamePage,
          CountryPage,
          AddressPage,
          AddContactPage,
          TirIdentificationPage
        )

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when tir identification number is missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        result.left.value.page mustBe TirIdentificationPage
      }

      "when contact details are missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(TirIdentificationPage)(tirNumber)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        result.left.value.page mustBe contact.NamePage
      }

      "when DeclarationType is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitWithoutEori] = UserAnswersReader[HolderOfTransitWithoutEori].run(userAnswers)

        result.left.value.page mustBe DeclarationTypePage
      }
    }
  }

}
