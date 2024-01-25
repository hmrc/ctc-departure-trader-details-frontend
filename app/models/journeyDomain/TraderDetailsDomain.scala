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

package models.journeyDomain

import config.PhaseConfig
import models.journeyDomain.consignment.ConsignmentDomain
import models.journeyDomain.holderOfTransit.HolderOfTransitDomain
import models.journeyDomain.representative.RepresentativeDomain
import models.{Mode, SubmissionState, UserAnswers}
import pages.ActingAsRepresentativePage
import play.api.mvc.Call

trait TraderDetailsDomain extends JourneyDomainModel {
  val consignment: ConsignmentDomain

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn))
}

object TraderDetailsDomain {

  implicit def userAnswersReader(implicit phaseConfig: PhaseConfig): UserAnswersReader[TraderDetailsDomain] =
    UserAnswersReader
      .success(
        (userAnswers: UserAnswers) => userAnswers.status
      )
      .to {
        case SubmissionState.Amendment => TraderDetailsDomainAmending.userAnswersParser
        case _                         => TraderDetailsDomainDefault.userAnswersParser
      }
      .apply(Nil)
}

case class TraderDetailsDomainDefault(
  holderOfTransit: HolderOfTransitDomain,
  representative: Option[RepresentativeDomain],
  consignment: ConsignmentDomain
) extends TraderDetailsDomain

object TraderDetailsDomainDefault {

  implicit def userAnswersParser(implicit phaseConfig: PhaseConfig): Read[TraderDetailsDomain] =
    (
      HolderOfTransitDomain.userAnswersReader,
      ActingAsRepresentativePage.filterOptionalDependent(identity)(RepresentativeDomain.userAnswersReader),
      ConsignmentDomain.userAnswersReader
    ).map(TraderDetailsDomainDefault.apply)
}

case class TraderDetailsDomainAmending(
  consignment: ConsignmentDomain
) extends TraderDetailsDomain

object TraderDetailsDomainAmending {

  implicit def userAnswersParser(implicit phaseConfig: PhaseConfig): Read[TraderDetailsDomain] =
    ConsignmentDomain.userAnswersReader.map(TraderDetailsDomainAmending.apply)
}
