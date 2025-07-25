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
import config.Constants.DeclarationType.TIR
import config.Constants.SecurityType.NoSecurityDetails
import generators.Generators
import models.journeyDomain.consignment.ConsignmentConsigneeDomain.ConsigneeWithEori
import models.journeyDomain.consignment.ConsignmentConsignorDomain.ConsignorWithoutEori
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.consignment._
import pages.external._

class ConsignmentDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ConsignmentDomain" - {

    val name                      = Gen.alphaNumStr.sample.value
    val eoriNumber                = Gen.alphaNumStr.sample.value
    val address                   = arbitrary[DynamicAddress].sample.value
    val nonOption4DeclarationType = arbitrary[String](arbitraryNonTIRDeclarationType).sample.value
    val securityDetailsType       = arbitrary[String](arbitrarySecurityDetailsType).sample.value
    val someSecurityType          = arbitrary[String](arbitrarySomeSecurityDetailsType).sample.value
    val country                   = arbitrary[Country].sample.value

    "can be parsed from UserAnswers" - {

      "when has the minimum consignment fields complete" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonOption4DeclarationType)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

        val consigneeWithEori = ConsigneeWithEori(EoriNumber(eoriNumber))

        val expectedResult = ConsignmentDomain(
          consignor = None,
          consignee = Some(consigneeWithEori)
        )

        val result = ConsignmentDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          ApprovedOperatorPage,
          consignee.EoriYesNoPage,
          consignee.EoriNumberPage
        )
      }

      "when the consignor fields are complete" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonOption4DeclarationType)
          .setValue(SecurityDetailsTypePage, someSecurityType)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

        val consigneeWithEori = ConsigneeWithEori(EoriNumber(eoriNumber))

        val consignorDomain = ConsignorWithoutEori(
          name = name,
          country = country,
          address = address,
          contact = None
        )

        val expectedResult = ConsignmentDomain(
          consignor = Some(consignorDomain),
          consignee = Some(consigneeWithEori)
        )

        val result = ConsignmentDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          ApprovedOperatorPage,
          consignor.EoriYesNoPage,
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage,
          consignee.EoriYesNoPage,
          consignee.EoriNumberPage
        )
      }

      "when the consignor fields are populated but we don't want security details but have the ApprovedOperatorPage as Yes" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonOption4DeclarationType)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(true)
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

        val consigneeWithEori = ConsigneeWithEori(EoriNumber(eoriNumber))

        val expectedResult = ConsignmentDomain(
          consignor = None,
          consignee = Some(consigneeWithEori)
        )

        val result = ConsignmentDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          ApprovedOperatorPage,
          consignee.EoriYesNoPage,
          consignee.EoriNumberPage
        )
      }

      "when the consignor fields are populated we do not want security details and have the ApprovedOperatorPage as No" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonOption4DeclarationType)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(ApprovedOperatorPage)(false)
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

        val consignorDomain = ConsignorWithoutEori(
          name = name,
          country = country,
          address = address,
          contact = None
        )

        val consigneeWithEori = ConsigneeWithEori(EoriNumber(eoriNumber))

        val expectedResult = ConsignmentDomain(
          consignor = Some(consignorDomain),
          consignee = Some(consigneeWithEori)
        )

        val result = ConsignmentDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          ApprovedOperatorPage,
          consignor.EoriYesNoPage,
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage,
          consignee.EoriYesNoPage,
          consignee.EoriNumberPage
        )
      }

      "when the consignor fields are populated but we don't want security details but have the ApprovedOperatorPage as Yes, but we have an option4 declarationType" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, TIR)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.CountryPage)(country)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(eoriNumber)

        val consignorDomain = ConsignorWithoutEori(
          name = name,
          country = country,
          address = address,
          contact = None
        )

        val consigneeWithEori = ConsigneeWithEori(EoriNumber(eoriNumber))

        val expectedResult = ConsignmentDomain(
          consignor = Some(consignorDomain),
          consignee = Some(consigneeWithEori)
        )

        val result = ConsignmentDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          consignor.EoriYesNoPage,
          consignor.NamePage,
          consignor.CountryPage,
          consignor.AddressPage,
          consignor.AddContactPage,
          consignee.EoriYesNoPage,
          consignee.EoriNumberPage
        )
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when Reduced data set page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, nonOption4DeclarationType)
          .setValue(SecurityDetailsTypePage, securityDetailsType)

        val result = ConsignmentDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustEqual ApprovedOperatorPage
        result.left.value.pages mustEqual Seq(
          ApprovedOperatorPage
        )
      }
    }
  }
}
