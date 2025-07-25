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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.UserAnswersResponse.{Answers, NoAnswers}
import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{LocalReferenceNumber, SubmissionState, UserAnswers, UserAnswersResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import play.api.mvc.{AnyContent, Results}

import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  def harness(lrn: LocalReferenceNumber)(f: OptionalDataRequest[AnyContent] => Unit): Unit = {

    lazy val actionProvider = app.injector.instanceOf[DataRetrievalActionProviderImpl]

    actionProvider(lrn)
      .invokeBlock(
        IdentifierRequest(fakeRequest, eoriNumber),
        {
          (request: OptionalDataRequest[AnyContent]) =>
            f(request)
            Future.successful(Results.Ok)
        }
      )
      .futureValue
  }

  "a data retrieval action" - {

    "must return an OptionalDataRequest with an empty UserAnswers" - {

      "where there are no existing answers for this LRN" in {

        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(NoAnswers))

        harness(lrn) {
          _.userAnswers mustEqual NoAnswers
        }
      }
    }

    "must return an OptionalDataRequest with some defined UserAnswers" - {

      "when there are existing answers for this LRN" in {

        val expectedAnswers = Answers(UserAnswers(lrn, eoriNumber, status = SubmissionState.NotSubmitted))
        when(mockSessionRepository.get(any())(any()))
          .thenReturn(Future.successful(expectedAnswers))

        harness(lrn) {
          _.userAnswers mustEqual expectedAnswers
        }
      }
    }
  }
}
