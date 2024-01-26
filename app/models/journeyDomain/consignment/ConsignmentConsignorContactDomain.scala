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

import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Read}
import pages.consignment.consignor.contact.{NamePage, TelephoneNumberPage}

case class ConsignmentConsignorContactDomain(
  name: String,
  telephoneNumber: String
) extends JourneyDomainModel

object ConsignmentConsignorContactDomain {

  implicit val userAnswersReader: Read[ConsignmentConsignorContactDomain] =
    (
      NamePage.reader,
      TelephoneNumberPage.reader
    ).map(ConsignmentConsignorContactDomain.apply)
}
