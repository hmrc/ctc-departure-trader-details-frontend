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

package views.consignment

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.consignment.ApprovedOperatorView

class ApprovedOperatorViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[ApprovedOperatorView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.consignment.approvedOperator"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This reduces the number of mandatory questions in this form, enabling you to complete your declaration with less data.")

  behave like pageWithHint("You can only use this if you are an approved airline, shipping or rail freight operator and hold a reduced data set authorisation.")

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")

}
