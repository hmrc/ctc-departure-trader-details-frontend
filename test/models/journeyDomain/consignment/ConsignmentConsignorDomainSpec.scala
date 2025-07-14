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

package models.journeyDomain.consignment

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.journeyDomain.consignment.ConsignmentConsignorDomain.{ConsignorWithEori, ConsignorWithoutEori}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.consignment._

class ConsignmentConsignorDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val eori         = arbitrary[EoriNumber].sample.value
  private val country      = arbitrary[Country].sample.value
  private val name         = Gen.alphaNumStr.sample.value
  private val address      = arbitrary[DynamicAddress].sample.value
  private val contactName  = Gen.alphaNumStr.sample.value
  private val contactPhone = Gen.alphaNumStr.sample.value

  "ConsignmentConsignorDomain" - {

    "can be parsed from UserAnswers" - {

      "when EORI is defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(true)
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.AddContactPage)(false)

        val expectedResult = ConsignorWithEori(
          eori = eori,
          contact = None
        )

        val result = ConsignmentConsignorDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.EoriYesNoPage,
          consignor.EoriPage,
          consignor.AddContactPage
        )
      }

      "when EORI is not defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)

        val expectedResult = ConsignorWithoutEori(
          name = name,
          country = country,
          address = address,
          contact = None
        )

        val result = ConsignmentConsignorDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.EoriYesNoPage,
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when EoriYesNoPage is missing" in {

        val userAnswers = emptyUserAnswers

        val result = ConsignmentConsignorDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustEqual consignor.EoriYesNoPage
        result.left.value.pages mustEqual Seq(
          consignor.EoriYesNoPage
        )
      }
    }
  }

  "ConsignorWithEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.AddContactPage)(false)

        val expectedResult = ConsignorWithEori(
          eori = eori,
          contact = None
        )

        val result = ConsignorWithEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.EoriPage,
          consignor.AddContactPage
        )
      }

      "when all optional pages are defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)
          .unsafeSetVal(consignor.contact.TelephoneNumberPage)(contactPhone)

        val expectedResult = ConsignorWithEori(
          eori = eori,
          contact = Some(
            ConsignmentConsignorContactDomain(
              name = contactName,
              telephoneNumber = contactPhone
            )
          )
        )

        val result = ConsignorWithEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.EoriPage,
          consignor.AddContactPage,
          consignor.contact.NamePage,
          consignor.contact.TelephoneNumberPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[?]] = Gen.oneOf(
          consignor.EoriPage,
          consignor.AddContactPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.AddContactPage)(false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result = ConsignorWithEori.userAnswersReader.apply(Nil).run(invalidUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }

      "when contact is defined and mandatory contact information is missing" in {

        val mandatoryPages: Gen[QuestionPage[?]] = Gen.oneOf(
          consignor.contact.NamePage,
          consignor.contact.TelephoneNumberPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)
          .unsafeSetVal(consignor.contact.TelephoneNumberPage)(contactPhone)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result = ConsignorWithEori.userAnswersReader.apply(Nil).run(invalidUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }
    }
  }

  "ConsignorWithoutEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)

        val expectedResult = ConsignorWithoutEori(
          name = name,
          country = country,
          address = address,
          None
        )

        val result = ConsignorWithoutEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage
        )
      }

      "when all optional pages are defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)
          .unsafeSetVal(consignor.contact.TelephoneNumberPage)(contactPhone)

        val expectedResult = ConsignorWithoutEori(
          name = name,
          country = country,
          address = address,
          contact = Some(
            ConsignmentConsignorContactDomain(
              name = contactName,
              telephoneNumber = contactPhone
            )
          )
        )

        val result = ConsignorWithoutEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage,
          consignor.contact.NamePage,
          consignor.contact.TelephoneNumberPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[?]] = Gen.oneOf(
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result = ConsignorWithoutEori.userAnswersReader.apply(Nil).run(invalidUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }

      "when contact is defined and mandatory contact information is missing" in {

        val mandatoryPages: Gen[QuestionPage[?]] = Gen.oneOf(
          consignor.contact.NamePage,
          consignor.contact.TelephoneNumberPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)
          .unsafeSetVal(consignor.contact.TelephoneNumberPage)(contactPhone)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result = ConsignorWithoutEori.userAnswersReader.apply(Nil).run(invalidUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }
    }
  }
}
