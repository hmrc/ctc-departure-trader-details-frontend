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

import config.Constants.DeclarationType.TIR
import config.Constants.SecurityType.NoSecurityDetails
import config.PhaseConfig
import models.Phase
import models.journeyDomain.*
import pages.consignment.{ApprovedOperatorPage, MoreThanOneConsigneePage}
import pages.external.{DeclarationTypePage, SecurityDetailsTypePage}

case class ConsignmentDomain(
  consignor: Option[ConsignmentConsignorDomain],
  consignee: Option[ConsignmentConsigneeDomain]
) extends JourneyDomainModel

object ConsignmentDomain {

  implicit def userAnswersReader(implicit phaseConfig: PhaseConfig): Read[ConsignmentDomain] =
    (
      consignorReader,
      consigneeReader
    ).map(ConsignmentDomain.apply)

  private def consignorReader: Read[Option[ConsignmentConsignorDomain]] = {
    lazy val mandatoryConsignorReader: Read[Option[ConsignmentConsignorDomain]] =
      ConsignmentConsignorDomain.userAnswersReader.toOption

    DeclarationTypePage.reader.to {
      case TIR => mandatoryConsignorReader
      case _ =>
        (
          SecurityDetailsTypePage.reader,
          ApprovedOperatorPage.reader
        ).to {
          case (NoSecurityDetails, true) => UserAnswersReader.none
          case _                         => mandatoryConsignorReader
        }
    }
  }

  private def consigneeReader(implicit phaseConfig: PhaseConfig): Read[Option[ConsignmentConsigneeDomain]] =
    phaseConfig.phase match {
      case Phase.Transition =>
        MoreThanOneConsigneePage.filterOptionalDependent(!_)(ConsignmentConsigneeDomain.userAnswersReader)
      case Phase.PostTransition =>
        ConsignmentConsigneeDomain.userAnswersReader.toOption
    }
}
