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
import views.html.consignment.MoreThanOneConsigneeView

class MoreThanOneConsigneeViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[MoreThanOneConsigneeView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.consignment.moreThanOneConsignee"

  behave like pageWithTitle()

  behave like pageWithSectionCaption("Trader details - Consignee")

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithInsetText("If you have more than one consignee, we will ask you for their details in your Items section.")

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
