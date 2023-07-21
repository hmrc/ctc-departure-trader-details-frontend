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

import cats.implicits._
import config.Constants.TIR
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import pages.external.DeclarationTypePage
import pages.holderOfTransit._

sealed trait HolderOfTransitDomain extends JourneyDomainModel {
  val tir: Option[String]
  val additionalContact: Option[AdditionalContactDomain]
}

object HolderOfTransitDomain {

  implicit val userAnswersReader: UserAnswersReader[HolderOfTransitDomain] =
    EoriYesNoPage.reader.flatMap {
      case true  => UserAnswersReader[HolderOfTransitWithEori].widen[HolderOfTransitDomain]
      case false => UserAnswersReader[HolderOfTransitWithoutEori].widen[HolderOfTransitDomain]
    }

  case class HolderOfTransitWithEori(
    eori: EoriNumber,
    additionalContact: Option[AdditionalContactDomain],
    tir: Option[String]
  ) extends HolderOfTransitDomain

  object HolderOfTransitWithEori {

    implicit val userAnswersReader: UserAnswersReader[HolderOfTransitWithEori] =
      (
        EoriPage.reader.map(EoriNumber(_)),
        AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain]),
        DeclarationTypePage.filterOptionalDependent(_ == TIR)(TirIdentificationPage.reader)
      ).tupled.map((HolderOfTransitWithEori.apply _).tupled)

  }

  case class HolderOfTransitWithoutEori(
    name: String,
    country: Country,
    address: DynamicAddress,
    additionalContact: Option[AdditionalContactDomain],
    tir: Option[String]
  ) extends HolderOfTransitDomain

  object HolderOfTransitWithoutEori {

    implicit val userAnswersReader: UserAnswersReader[HolderOfTransitWithoutEori] =
      (
        NamePage.reader,
        CountryPage.reader,
        AddressPage.reader,
        AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalContactDomain]),
        DeclarationTypePage.filterOptionalDependent(_ == TIR)(TirIdentificationPage.reader)
      ).tupled.map((HolderOfTransitWithoutEori.apply _).tupled)
  }
}
