/*
 * Copyright 2024 HM Revenue & Customs
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

package handlers

import base.{AppWithDefaultMockFixtures, SpecBase}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.mvc.{Result, Results}
import play.api.test.Helpers.*
import uk.gov.hmrc.play.bootstrap.frontend.http.ApplicationException

import scala.concurrent.Future

// scalastyle:off magic.number
class ErrorHandlerSpec extends SpecBase with AppWithDefaultMockFixtures with Results {

  private lazy val handler: ErrorHandler = app.injector.instanceOf[ErrorHandler]

  "onClientError" - {
    "must redirect to NotFound page when given a 404" in {

      val result: Future[Result] = handler.onClientError(fakeRequest, 404)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.notFoundUrl
    }

    "must redirect to BadRequest page when given a client error (400-499)" in {

      forAll(Gen.choose(400, 499).suchThat(_ != 404)) {
        clientErrorCode =>
          beforeEach()

          val result: Future[Result] = handler.onClientError(fakeRequest, clientErrorCode)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual s"${frontendAppConfig.departureHubUrl}/bad-request"
      }
    }

    "must redirect to TechnicalDifficulties page when given any other error" in {

      forAll(Gen.choose(500, 599)) {
        serverErrorCode =>
          beforeEach()

          val result: Future[Result] = handler.onClientError(fakeRequest, serverErrorCode)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual frontendAppConfig.technicalDifficultiesUrl
      }
    }
  }

  "onServerError" - {
    "when an application exception" - {
      "must return the underlying result" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (message, url) =>
            val redirect  = Redirect(url)
            val exception = ApplicationException(redirect, message)
            val result    = handler.onServerError(fakeRequest, exception)
            redirectLocation(result).value mustEqual url
        }
      }
    }

    "when any other exception" - {
      "must redirect to internal server error page" in {
        forAll(Gen.alphaNumStr) {
          message =>
            val exception = Exception(message)
            val result    = handler.onServerError(fakeRequest, exception)
            redirectLocation(result).value mustEqual s"${frontendAppConfig.departureHubUrl}/internal-server-error"
        }
      }
    }
  }

}
// scalastyle:on magic.number
