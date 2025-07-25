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

package models.journeyDomain.representative

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.EoriNumber
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.representative.*

class RepresentativeDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val eori  = arbitrary[EoriNumber].sample.value
  private val name  = Gen.alphaNumStr.sample.value
  private val phone = Gen.alphaNumStr.sample.value

  "Representative" - {

    "can be parsed from UserAnswers" - {

      "when mandatory eori field complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddDetailsPage)(false)

        val expectedResult = RepresentativeDomain(
          eori = eori,
          None
        )

        val result = RepresentativeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          EoriPage,
          AddDetailsPage
        )
      }

      "when all optional fields complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddDetailsPage)(true)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(TelephoneNumberPage)(phone)

        val expectedResult = RepresentativeDomain(
          eori = eori,
          Some(RepresentativeDetailsDomain(name, phone))
        )

        val result = RepresentativeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          EoriPage,
          AddDetailsPage,
          NamePage,
          TelephoneNumberPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when representative has no eori" in {

        val userAnswers = emptyUserAnswers

        val result = RepresentativeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustEqual EoriPage
        result.left.value.pages mustEqual Seq(
          EoriPage
        )
      }

      "when additional representative details are not optional and has no name" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddDetailsPage)(true)

        val result = RepresentativeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustEqual NamePage
        result.left.value.pages mustEqual Seq(
          EoriPage,
          AddDetailsPage,
          NamePage
        )
      }

      "when additional representative details are not optional and has no telephone number" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(AddDetailsPage)(true)
          .unsafeSetVal(NamePage)(name)

        val result = RepresentativeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustEqual TelephoneNumberPage
        result.left.value.pages mustEqual Seq(
          EoriPage,
          AddDetailsPage,
          NamePage,
          TelephoneNumberPage
        )
      }
    }
  }
}
