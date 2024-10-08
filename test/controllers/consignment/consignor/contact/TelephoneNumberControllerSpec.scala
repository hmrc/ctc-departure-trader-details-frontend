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

package controllers.consignment.consignor.contact

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.TelephoneNumberFormProvider
import generators.Generators
import pages.consignment.consignor.contact._
import navigation.TraderDetailsNavigatorProvider
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.consignment.consignor.contact.TelephoneNumberView

import scala.concurrent.Future

class TelephoneNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                     = new TelephoneNumberFormProvider()
  private val contactName                      = "Test Contact Name"
  private val form                             = formProvider("traderDetails.consignment.consignor.contact.telephoneNumber", contactName)
  private val mode                             = NormalMode
  private lazy val contactTelephoneNumberRoute = routes.TelephoneNumberController.onPageLoad(lrn, mode).url

  private val validAnswer: String = "+123123"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TraderDetailsNavigatorProvider]).toInstance(fakeTraderDetailsNavigatorProvider))

  "traderDetails.consignment.consignor.contactTelephoneNumber Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.setValue(NamePage, contactName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, contactTelephoneNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, contactName)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers
        .setValue(NamePage, contactName)
        .setValue(TelephoneNumberPage, validAnswer)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, contactTelephoneNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> validAnswer))

      val view = injector.instanceOf[TelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, contactName)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.setValue(NamePage, contactName)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      val request =
        FakeRequest(POST, contactTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.setValue(NamePage, contactName)
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, contactTelephoneNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TelephoneNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, contactName)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, contactTelephoneNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, contactTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)

    }
  }
}
