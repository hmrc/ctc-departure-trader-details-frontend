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

package config

import com.google.inject.{Inject, Singleton}
import models.LocalReferenceNumber
import play.api.Configuration

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: MyServicesConfig) {

  val appName: String = configuration.get[String]("appName")

  val enrolmentProxyUrl: String           = servicesConfig.fullServiceUrl("enrolment-store-proxy")
  val eccEnrolmentSplashPage: String      = configuration.get[String]("urls.eccEnrolmentSplashPage")
  lazy val enrolmentKey: String           = configuration.get[String]("enrolment.key")
  lazy val enrolmentIdentifierKey: String = configuration.get[String]("enrolment.identifierKey")

  lazy val referenceDataUrl: String = servicesConfig.fullServiceUrl("customs-reference-data")

  val loginUrl: String         = configuration.get[String]("urls.login")
  val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  val hubUrl: String     = configuration.get[String]("urls.manageTransitMovementsFrontend")
  val serviceUrl: String = s"$hubUrl/what-do-you-want-to-do"

  val departureHubUrl: String = configuration.get[String]("urls.manageTransitMovementsDepartureFrontend")

  val notFoundUrl: String              = s"$departureHubUrl/not-found"
  val technicalDifficultiesUrl: String = s"$departureHubUrl/technical-difficulties"
  val sessionExpiredUrl: String        = s"$departureHubUrl/this-service-has-been-reset"

  val unauthorisedUrl: String                = s"$departureHubUrl/error/cannot-use-service-no-eori"
  val unauthorisedWithGroupAccessUrl: String = s"$departureHubUrl/unauthorised-group-access"

  val lockedUrl: String = s"$departureHubUrl/cannot-open"

  def keepAliveUrl(lrn: LocalReferenceNumber): String = s"$departureHubUrl/$lrn/keep-alive"

  def signOutUrl(lrn: LocalReferenceNumber): String = s"$departureHubUrl/$lrn/delete-lock"

  def sessionExpiredUrl(lrn: LocalReferenceNumber): String = s"$departureHubUrl/this-service-has-been-reset/$lrn"

  def taskListUrl(lrn: LocalReferenceNumber): String = s"$departureHubUrl/$lrn/declaration-summary"

  val cacheUrl: String = servicesConfig.fullServiceUrl("manage-transit-movements-departure-cache")

  val transportDetailsUrl: String = configuration.get[String]("urls.manageTransitMovementsDepartureTransportDetailsFrontend")
  val itemsUrl: String            = configuration.get[String]("urls.manageTransitMovementsDepartureItemsFrontend")

  val dependentTasks: Seq[String] = configuration.get[Seq[String]]("dependent-tasks")

  def absoluteURL(url: String): String = configuration.get[String]("host") + url
}
