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

import forms.TirIdNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.holderOfTransit.TirIdentificationView

class TirIdentificationViewSpec extends InputTextViewBehaviours[String] {

  override def form: Form[String] = new TirIdNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[TirIdentificationView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.holderOfTransit.tirIdentification"

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaNumStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Trader details - Transit holder")

  behave like pageWithHeading()

  behave like pageWithHint("For example, AAA/999/99999.")

  behave like pageWithContent(
    "p",
    "The TIR holder is the person responsible for the transit procedure. They can submit the declaration themselves or a third party can do it on their behalf."
  )

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
