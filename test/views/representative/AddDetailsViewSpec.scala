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

package views.representative

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.representative.AddDetailsView

class AddDetailsViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddDetailsView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.representative.addDetails"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Trader details - Representative")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This includes your name and phone number for if Customs have any queries.")

  behave like pageWithHint("Adding your details is optional.")

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
