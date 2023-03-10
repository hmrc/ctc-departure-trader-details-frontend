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

import cats.implicits.catsSyntaxTuple2Semigroupal
import models.EoriNumber
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import pages.representative.{AddDetailsPage, EoriPage}

case class RepresentativeDomain(
  eori: EoriNumber,
  representativeDetails: Option[RepresentativeDetailsDomain]
) extends JourneyDomainModel

object RepresentativeDomain {

  implicit val userAnswersReader: UserAnswersReader[RepresentativeDomain] =
    (
      EoriPage.reader.map(EoriNumber(_)),
      AddDetailsPage.filterOptionalDependent(identity)(UserAnswersReader[RepresentativeDetailsDomain])
    ).tupled.map((RepresentativeDomain.apply _).tupled)
}
