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

import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxTuple4Semigroupal, toFunctorOps}
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import pages.consignment.consignor._

sealed trait ConsignmentConsignorDomain extends JourneyDomainModel {
  val contact: Option[ConsignmentConsignorContactDomain]
}

object ConsignmentConsignorDomain {

  implicit val userAnswersReader: UserAnswersReader[ConsignmentConsignorDomain] =
    EoriYesNoPage.reader.flatMap {
      case true  => UserAnswersReader[ConsignorWithEori].widen[ConsignmentConsignorDomain]
      case false => UserAnswersReader[ConsignorWithoutEori].widen[ConsignmentConsignorDomain]
    }

  case class ConsignorWithEori(
    eori: EoriNumber,
    contact: Option[ConsignmentConsignorContactDomain]
  ) extends ConsignmentConsignorDomain

  object ConsignorWithEori {

    implicit val userAnswersReader: UserAnswersReader[ConsignorWithEori] =
      (
        EoriPage.reader.map(EoriNumber(_)),
        AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[ConsignmentConsignorContactDomain])
      ).tupled.map((ConsignorWithEori.apply _).tupled)

  }

  case class ConsignorWithoutEori(
    name: String,
    country: Country,
    address: DynamicAddress,
    contact: Option[ConsignmentConsignorContactDomain]
  ) extends ConsignmentConsignorDomain

  object ConsignorWithoutEori {

    implicit val userAnswersReader: UserAnswersReader[ConsignorWithoutEori] =
      (
        NamePage.reader,
        CountryPage.reader,
        AddressPage.reader,
        AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[ConsignmentConsignorContactDomain])
      ).tupled.map((ConsignorWithoutEori.apply _).tupled)
  }
}
