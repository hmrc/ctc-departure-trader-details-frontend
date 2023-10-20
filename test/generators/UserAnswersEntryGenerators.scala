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

package generators

import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json._
import queries.Gettable

// scalastyle:off number.of.methods
trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateExternalAnswer orElse
      generateTraderDetailsAnswer

  private def generateExternalAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.external._
    {
      case DeclarationTypePage     => arbitrary[String](arbitraryDeclarationType).map(Json.toJson(_))
      case SecurityDetailsTypePage => arbitrary[String](arbitrarySecurityDetailsType).map(Json.toJson(_))
    }
  }

  private def generateTraderDetailsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages._
    {
      generateHolderOfTransitAnswer orElse
        generateRepresentativeAnswer orElse
        generateConsignmentAnswer orElse {
          case ActingAsRepresentativePage => arbitrary[Boolean].map(JsBoolean)
        }
    }
  }

  private def generateHolderOfTransitAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.holderOfTransit._
    {
      case EoriYesNoPage               => arbitrary[Boolean].map(JsBoolean)
      case EoriPage                    => Gen.alphaNumStr.map(JsString)
      case TirIdentificationPage       => Gen.alphaNumStr.map(JsString)
      case NamePage                    => Gen.alphaNumStr.map(JsString)
      case CountryPage                 => arbitrary[Country].map(Json.toJson(_))
      case AddressPage                 => arbitrary[DynamicAddress].map(Json.toJson(_))
      case AddContactPage              => arbitrary[Boolean].map(JsBoolean)
      case contact.NamePage            => Gen.alphaNumStr.map(JsString)
      case contact.TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateRepresentativeAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.representative._
    {
      case AddDetailsPage      => arbitrary[Boolean].map(JsBoolean)
      case EoriPage            => Gen.alphaNumStr.map(JsString)
      case NamePage            => Gen.alphaNumStr.map(JsString)
      case TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateConsignmentAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.consignment._
    {
      generateConsignorAnswer orElse
        generateConsigneeAnswer orElse {
          case ApprovedOperatorPage     => arbitrary[Boolean].map(JsBoolean)
          case MoreThanOneConsigneePage => arbitrary[Boolean].map(JsBoolean)
        }
    }
  }

  private def generateConsignorAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.consignment.consignor._
    {
      case EoriYesNoPage               => arbitrary[Boolean].map(JsBoolean)
      case EoriPage                    => Gen.alphaNumStr.map(JsString)
      case NamePage                    => Gen.alphaNumStr.map(JsString)
      case CountryPage                 => arbitrary[Country].map(Json.toJson(_))
      case AddressPage                 => arbitrary[DynamicAddress].map(Json.toJson(_))
      case AddContactPage              => arbitrary[Boolean].map(JsBoolean)
      case contact.NamePage            => Gen.alphaNumStr.map(JsString)
      case contact.TelephoneNumberPage => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateConsigneeAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.consignment.consignee._
    {
      case EoriYesNoPage  => arbitrary[Boolean].map(JsBoolean)
      case EoriNumberPage => Gen.alphaNumStr.map(JsString)
      case NamePage       => Gen.alphaNumStr.map(JsString)
      case CountryPage    => arbitrary[Country].map(Json.toJson(_))
      case AddressPage    => arbitrary[DynamicAddress].map(Json.toJson(_))
    }
  }

}
// scalastyle:on number.of.methods
