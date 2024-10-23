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

package utils.cyaHelpers

import base.SpecBase
import controllers.consignment.{routes => consignmentRoutes}
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.consignment.*
import uk.gov.hmrc.govukfrontend.views.html.components.implicits.*
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

class HeaderCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "HeaderCheckYourAnswersHelper" - {

    "approvedOperator" - {
      "must return None" - {
        s"when $ApprovedOperatorPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HeaderCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.approvedOperator
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $ApprovedOperatorPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ApprovedOperatorPage, true)

              val helper = new HeaderCheckYourAnswersHelper(answers, mode)
              val result = helper.approvedOperator

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to use a reduced data set?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignmentRoutes.ApprovedOperatorController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to use a reduced data set"),
                          attributes = Map("id" -> "change-has-reduced-data-set")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }
  }
}
