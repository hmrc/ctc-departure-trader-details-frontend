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
import generators.Generators
import models.DeclarationType.Option4
import models.journeyDomain.holderOfTransit.HolderOfTransitDomain.{HolderOfTransitEori, HolderOfTransitTIR}
import models.reference.Country
import models.{DeclarationType, DynamicAddress, EoriNumber}
import pages.holderOfTransit._
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.external.DeclarationTypePage

class HolderOfTransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val name         = Gen.alphaNumStr.sample.value
  private val country      = arbitrary[Country].sample.value
  private val address      = arbitrary[DynamicAddress].sample.value
  private val contactName  = Gen.alphaNumStr.sample.value
  private val contactPhone = Gen.alphaNumStr.sample.value

  "HolderOfTransitDomain" - {

    "can be parsed from UserAnswers" - {

      "when Declaration Type is TIR (Option 4)" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, Option4)
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.value mustBe an[HolderOfTransitTIR]
      }

      "when Declaration Type is anything else" in {

        DeclarationType.values.filterNot(_ == Option4).foreach {
          declarationType =>
            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .unsafeSetVal(EoriYesNoPage)(false)
              .unsafeSetVal(NamePage)(name)
              .unsafeSetVal(CountryPage)(country)
              .unsafeSetVal(AddressPage)(address)
              .unsafeSetVal(AddContactPage)(false)

            val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

            result.value mustBe an[HolderOfTransitEori]
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when Declaration type is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.left.value.page mustBe DeclarationTypePage
      }
    }

  }

  "HolderOfTransitEori" - {

    val eori = arbitrary[EoriNumber].sample.value

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        val expectedResult = HolderOfTransitEori(None, name, country, address, None)

        result.value mustBe expectedResult

      }

      "when all optional pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        val expectedResult = HolderOfTransitEori(
          Some(eori),
          name,
          country,
          address,
          Some(AdditionalContactDomain(contactName, contactPhone))
        )

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          EoriYesNoPage,
          NamePage,
          CountryPage,
          AddressPage,
          AddContactPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when eori number is missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        result.left.value.page mustBe EoriPage
      }

      "when contact details are missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        result.left.value.page mustBe contact.NamePage
      }
    }
  }

  "HolderOfTransitTIR" - {

    val tirId = Gen.alphaNumStr.sample.value

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        val expectedResult = HolderOfTransitTIR(None, name, country, address, None)

        result.value mustBe expectedResult

      }

      "when all optional pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(true)
          .unsafeSetVal(TirIdentificationPage)(tirId)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(contact.NamePage)(contactName)
          .unsafeSetVal(contact.TelephoneNumberPage)(contactPhone)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        val expectedResult = HolderOfTransitTIR(
          Some(tirId),
          name,
          country,
          address,
          Some(AdditionalContactDomain(contactName, contactPhone))
        )

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          TirIdentificationYesNoPage,
          NamePage,
          CountryPage,
          AddressPage,
          AddContactPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when tir identification number is missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(true)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        result.left.value.page mustBe TirIdentificationPage
      }

      "when contact details are missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        result.left.value.page mustBe contact.NamePage
      }

    }
  }

}
