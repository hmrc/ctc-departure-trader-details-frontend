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

package views.holderOfTransit

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.holderOfTransit.EoriYesNoView

class EoriYesNoViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[EoriYesNoView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.holderOfTransit.eoriYesNo"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Trader details - Transit holder")

  behave like pageWithHeading()

  behave like pageWithContent(
    "p",
    "EORI number stands for Economic Operators Registration and Identification number. It is a unique reference used to identify a business or individual that trades internationally in the EU."
  )

  behave like pageWithContent(
    "p",
    "The transit holder is the person responsible for the transit procedure. They can submit the declaration themselves or a third party can do it on their behalf."
  )

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
