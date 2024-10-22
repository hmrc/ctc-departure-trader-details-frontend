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
import models.journeyDomain.consignment.ConsignmentConsigneeDomain.{ConsigneeWithEori, ConsigneeWithoutEori}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.consignment.consignee.*

class ConsignmentConsigneeDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val eori    = arbitrary[EoriNumber].sample.value
  private val country = arbitrary[Country].sample.value
  private val name    = Gen.alphaNumStr.sample.value
  private val address = arbitrary[DynamicAddress].sample.value

  "ConsignmentConsigneeDomain" - {

    "can be parsed from UserAnswers" - {

      "when EORI is defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(EoriNumberPage)(eori.value)

        val expectedResult = ConsigneeWithEori(eori)

        val result = ConsignmentConsigneeDomain.userAnswersReader.apply(Nil).run(userAnswers)
        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          EoriYesNoPage,
          EoriNumberPage
        )
      }

      "when EORI is not defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)

        val expectedResult = ConsigneeWithoutEori(
          name = name,
          country = country,
          address = address
        )

        val result = ConsignmentConsigneeDomain.userAnswersReader.apply(Nil).run(userAnswers)
        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          EoriYesNoPage,
          NamePage,
          CountryPage,
          AddressPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when EoriYesNoPage is missing" in {

        val userAnswers = emptyUserAnswers

        val result = ConsignmentConsigneeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustBe EoriYesNoPage
        result.left.value.pages mustBe Seq(
          EoriYesNoPage
        )
      }
    }
  }

  "ConsigneeWithEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriNumberPage)(eori.value)

        val expectedResult = ConsigneeWithEori(eori = eori)

        val result = ConsigneeWithEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          EoriNumberPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when mandatory page is missing" in {

        val userAnswers = emptyUserAnswers

        val result = ConsigneeWithEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustBe EoriNumberPage
        result.left.value.pages mustBe Seq(
          EoriNumberPage
        )
      }
    }
  }

  "ConsigneeWithoutEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are defined" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)

        val expectedResult = ConsigneeWithoutEori(
          name = name,
          country = country,
          address = address
        )

        val result = ConsigneeWithoutEori.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          NamePage,
          CountryPage,
          AddressPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[?]] = Gen.oneOf(
          NamePage,
          CountryPage,
          AddressPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(AddressPage)(address)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result = ConsigneeWithoutEori.userAnswersReader.apply(Nil).run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
