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

import cats.implicits.{catsSyntaxTuple3Semigroupal, toFunctorOps}
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import pages.consignment.consignee._

sealed trait ConsignmentConsigneeDomain extends JourneyDomainModel

object ConsignmentConsigneeDomain {

  implicit val userAnswersReader: UserAnswersReader[ConsignmentConsigneeDomain] =
    EoriYesNoPage.reader.flatMap {
      case true  => UserAnswersReader[ConsigneeWithEori].widen[ConsignmentConsigneeDomain]
      case false => UserAnswersReader[ConsigneeWithoutEori].widen[ConsignmentConsigneeDomain]
    }

  case class ConsigneeWithEori(eori: EoriNumber) extends ConsignmentConsigneeDomain

  object ConsigneeWithEori {

    implicit val userAnswersReader: UserAnswersReader[ConsigneeWithEori] =
      EoriNumberPage.reader.map(
        eori => ConsigneeWithEori(EoriNumber(eori))
      )

  }

  case class ConsigneeWithoutEori(
    name: String,
    country: Country,
    address: DynamicAddress
  ) extends ConsignmentConsigneeDomain

  object ConsigneeWithoutEori {

    implicit val userAnswersReader: UserAnswersReader[ConsigneeWithoutEori] =
      (
        NamePage.reader,
        CountryPage.reader,
        AddressPage.reader
      ).tupled.map((ConsigneeWithoutEori.apply _).tupled)
  }
}
