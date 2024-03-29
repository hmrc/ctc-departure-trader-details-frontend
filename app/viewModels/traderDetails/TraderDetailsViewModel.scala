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

package viewModels.traderDetails

import config.FrontendAppConfig
import models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import viewModels.Section
import viewModels.traderDetails.ConsignmentViewModel.ConsignmentViewModelProvider
import viewModels.traderDetails.HeaderViewModel.HeaderViewModelProvider
import viewModels.traderDetails.HolderOfTransitViewModel.HolderOfTransitViewModelProvider
import viewModels.traderDetails.RepresentativeViewModel.RepresentativeViewModelProvider

import javax.inject.Inject

case class TraderDetailsViewModel(sections: Seq[Section])

object TraderDetailsViewModel {

  class TraderDetailsViewModelProvider @Inject() (
    headerViewModel: HeaderViewModelProvider,
    holderOfTransitViewModelProvider: HolderOfTransitViewModelProvider,
    representativeViewModelProvider: RepresentativeViewModelProvider,
    consignmentViewModelProvider: ConsignmentViewModelProvider
  ) {

    def apply(userAnswers: UserAnswers)(implicit messages: Messages, config: FrontendAppConfig): TraderDetailsViewModel = {
      val mode = CheckMode
      new TraderDetailsViewModel(
        headerViewModel.apply(userAnswers, mode).sections ++
          holderOfTransitViewModelProvider.apply(userAnswers, mode).sections ++
          representativeViewModelProvider.apply(userAnswers, mode).sections ++
          consignmentViewModelProvider.apply(userAnswers, mode).sections
      )
    }
  }
}
