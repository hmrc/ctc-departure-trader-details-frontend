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
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.HolderOfTransitCheckYourAnswersHelper
import viewModels.Section

import javax.inject.Inject

case class HolderOfTransitViewModel(sections: Seq[Section])

object HolderOfTransitViewModel {

  class HolderOfTransitViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages, config: FrontendAppConfig): HolderOfTransitViewModel = {
      val helper = new HolderOfTransitCheckYourAnswersHelper(userAnswers, mode)

      val holderOfTransitSection = Section(
        sectionTitle = messages("traderDetails.checkYourAnswers.transitHolder"),
        rows = Seq(
          helper.eoriYesNo,
          helper.eori,
          helper.name,
          helper.country,
          helper.address,
          helper.tirIdentification
        ).flatten
      )

      val additionalContactSection = Section(
        sectionTitle = messages("traderDetails.checkYourAnswers.additionalContact"),
        rows = Seq(
          helper.addContact,
          helper.contactName,
          helper.contactTelephoneNumber
        ).flatten
      )

      new HolderOfTransitViewModel(Seq(holderOfTransitSection, additionalContactSection))
    }
  }
}
