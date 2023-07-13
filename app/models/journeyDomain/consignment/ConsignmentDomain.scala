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
import config.PhaseConfig
import models.DeclarationType.Option4
import models.SecurityDetailsType.NoSecurityDetails
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import pages.consignment.ApprovedOperatorPage
import pages.external.{DeclarationTypePage, SecurityDetailsTypePage}

case class ConsignmentDomain(
  consignor: Option[ConsignmentConsignorDomain],
  consignee: ConsignmentConsigneeDomain
) extends JourneyDomainModel

object ConsignmentDomain {

  implicit def userAnswersReader(implicit phaseConfig: PhaseConfig): UserAnswersReader[ConsignmentDomain] =
    for {
      consignor <- readConsignorDomain
      consignee <- UserAnswersReader[ConsignmentConsigneeDomain]
    } yield ConsignmentDomain(consignor, consignee)

  private def readConsignorDomain(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[ConsignmentConsignorDomain]] = {
    lazy val consignorReader: UserAnswersReader[Option[ConsignmentConsignorDomain]] =
      UserAnswersReader[ConsignmentConsignorDomain].map(Some(_))

    DeclarationTypePage.reader.flatMap {
      case Option4 => consignorReader
      case _ =>
        for {
          securityDetailsType <- SecurityDetailsTypePage.reader
          reducedDataSet      <- ApprovedOperatorPage.reader
          reader <- (securityDetailsType, reducedDataSet) match {
            case (NoSecurityDetails, true) => none[ConsignmentConsignorDomain].pure[UserAnswersReader]
            case _                         => consignorReader
          }
        } yield reader
    }
  }
}
