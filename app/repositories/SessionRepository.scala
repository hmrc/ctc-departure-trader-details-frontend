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

package repositories

import connectors.CacheConnector
import models.{LocalReferenceNumber, UserAnswers, UserAnswersResponse}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SessionRepository @Inject() (
  cacheConnector: CacheConnector
) {

  def get(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[UserAnswersResponse] =
    cacheConnector.get(lrn)

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.post(userAnswers)
}
