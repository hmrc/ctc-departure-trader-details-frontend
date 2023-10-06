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

import cats.implicits._
import config.Constants.DeclarationType.TIR
import config.Constants.SecurityType.NoSecurityDetails
import config.PhaseConfig
import models.Phase
import models.journeyDomain._
import pages.consignment.{ApprovedOperatorPage, MoreThanOneConsigneePage}
import pages.external.{DeclarationTypePage, SecurityDetailsTypePage}

case class ConsignmentDomain(
  consignor: Option[ConsignmentConsignorDomain],
  consignee: Option[ConsignmentConsigneeDomain]
) extends JourneyDomainModel

object ConsignmentDomain {

  implicit def userAnswersReader(implicit phaseConfig: PhaseConfig): UserAnswersReader[ConsignmentDomain] =
    for {
      consignor <- consignorReader
      consignee <- consigneeReader
    } yield ConsignmentDomain(consignor, consignee)

  private def consignorReader: UserAnswersReader[Option[ConsignmentConsignorDomain]] = {
    lazy val mandatoryConsignorReader: UserAnswersReader[Option[ConsignmentConsignorDomain]] =
      UserAnswersReader[ConsignmentConsignorDomain].map(Some(_))

    DeclarationTypePage.reader.flatMap {
      case TIR => mandatoryConsignorReader
      case _ =>
        for {
          securityDetailsType <- SecurityDetailsTypePage.reader
          reducedDataSet      <- ApprovedOperatorPage.reader
          reader <- (securityDetailsType, reducedDataSet) match {
            case (NoSecurityDetails, true) => none[ConsignmentConsignorDomain].pure[UserAnswersReader]
            case _                         => mandatoryConsignorReader
          }
        } yield reader
    }
  }

  private def consigneeReader(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[ConsignmentConsigneeDomain]] =
    phaseConfig.phase match {
      case Phase.Transition     => MoreThanOneConsigneePage.filterOptionalDependent(!_)(UserAnswersReader[ConsignmentConsigneeDomain])
      case Phase.PostTransition => UserAnswersReader[ConsignmentConsigneeDomain].map(Some(_))
    }
}
