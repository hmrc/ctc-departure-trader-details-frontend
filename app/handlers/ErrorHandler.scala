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

package handlers

import config.FrontendAppConfig
import play.api.http.HttpErrorHandler
import play.api.http.Status.*
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.*
import play.api.mvc.{RequestHeader, Result}
import play.api.{Logging, PlayException}
import uk.gov.hmrc.play.bootstrap.frontend.http.ApplicationException

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

// NOTE: There should be changes to bootstrap to make this easier, the API in bootstrap should allow a `Future[Html]` rather than just an `Html`
@Singleton
class ErrorHandler @Inject() (
  val messagesApi: MessagesApi,
  config: FrontendAppConfig
) extends HttpErrorHandler
    with I18nSupport
    with Logging {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String = ""): Future[Result] =
    statusCode match {
      case NOT_FOUND =>
        Future.successful(Redirect(config.notFoundUrl))
      case result if isClientError(result) =>
        Future.successful(Redirect(s"${config.departureHubUrl}/bad-request"))
      case _ =>
        Future.successful(Redirect(config.technicalDifficultiesUrl))
    }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logError(request, exception)

    exception match {
      case ApplicationException(result, _) =>
        Future.successful(result)
      case _ =>
        Future.successful(Redirect(s"${config.departureHubUrl}/internal-server-error"))
    }
  }

  private def logError(request: RequestHeader, ex: Throwable): Unit =
    logger.error(
      """
        |
        |! %sInternal server error, for (%s) [%s] ->
        | """.stripMargin.format(
        ex match {
          case p: PlayException => "@" + p.id + " - "
          case _                => ""
        },
        request.method,
        request.uri
      ),
      ex
    )
}
