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

import cats.implicits.catsSyntaxTuple5Semigroupal
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import pages.consignment.consignor.{AddContactPage, AddressPage, CountryPage, EoriPage, EoriYesNoPage, NamePage}

case class ConsignmentConsignorDomain(
  eori: Option[EoriNumber],
  name: String,
  country: Country,
  address: DynamicAddress,
  contact: Option[ConsignmentConsignorContactDomain]
) extends JourneyDomainModel

object ConsignmentConsignorDomain {

  implicit val userAnswersReader: UserAnswersReader[ConsignmentConsignorDomain] =
    (
      EoriYesNoPage.filterOptionalDependent(identity)(EoriPage.reader.map(EoriNumber(_))),
      NamePage.reader,
      CountryPage.reader,
      AddressPage.reader,
      AddContactPage.filterOptionalDependent(identity)(UserAnswersReader[ConsignmentConsignorContactDomain])
    ).tupled.map((ConsignmentConsignorDomain.apply _).tupled)
}
