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

package connectors

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, get, okJson, urlEqualTo}
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import itbase.{ItSpecBase, WireMockServerHandler}
import models.reference.*
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with EitherValues {

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.customs-reference-data.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private def countryCodesForAddressResponseJson: String =
    s"""
       |{
       |  "_links": {
       |    "self": {
       |      "href": "/customs-reference-data/lists/CountryCodesForAddress"
       |    }
       |  },
       |  "meta": {
       |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
       |    "snapshotDate": "2023-01-01"
       |  },
       |  "id": "CountryCodesForAddress",
       |  "data": [
       |    {
       |      "activeFrom": "2023-01-23",
       |      "code": "GB",
       |      "state": "valid",
       |      "description": "United Kingdom"
       |    },
       |    {
       |      "activeFrom": "2023-01-23",
       |      "code": "AD",
       |      "state": "valid",
       |      "description": "Andorra"
       |    }
       |  ]
       |}
       |""".stripMargin

  private def countryWithoutZipResponseJson: String =
    s"""
       |{
       |  "_links": {
       |    "self": {
       |      "href": "/customs-reference-data/lists/CountryWithoutZip"
       |    }
       |  },
       |  "meta": {
       |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
       |    "snapshotDate": "2023-01-01"
       |  },
       |  "id": "CountryWithoutZip",
       |  "data": [
       |    {
       |      "activeFrom": "2023-01-23",
       |      "code": "GB",
       |      "state": "valid",
       |      "description": "United Kingdom"
       |    }
       |  ]
       |}
       |""".stripMargin

  private val emptyResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  "Reference Data" - {

    "getCountries" - {
      val url = s"/$baseUrl/lists/CountryCodesForAddress"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
            .willReturn(okJson(countryCodesForAddressResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCountriesFullList().futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCountriesFullList())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountriesFullList())
      }
    }

    "getCountriesWithoutZipCountry" - {
      def url(countryId: String) = s"/$baseUrl/lists/CountryWithoutZip?data.code=$countryId"

      "must return Seq of Country when successful" in {
        val countryId = "GB"
        server.stubFor(
          get(urlEqualTo(url(countryId)))
            .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
            .willReturn(okJson(countryWithoutZipResponseJson))
        )

        val expectedResult = CountryCode(countryId)

        connector.getCountriesWithoutZipCountry(countryId).futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        val countryId = "FR"
        checkNoReferenceDataFoundResponse(url(countryId), connector.getCountriesWithoutZipCountry(countryId))
      }

      "must return an exception when an error response is returned" in {
        val countryId = "FR"
        checkErrorResponse(url(countryId), connector.getCountriesWithoutZipCountry(countryId))
      }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, result: => Future[Either[Exception, ?]]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
        .willReturn(okJson(emptyResponseJson))
    )
    result.futureValue.left.value mustBe a[NoReferenceDataFoundException]
  }

  private def checkErrorResponse(url: String, result: => Future[Either[Exception, ?]]): Assertion = {
    val errorResponses: Gen[Int] = Gen.chooseNum(400: Int, 599: Int)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        result.futureValue.left.value mustBe an[Exception]
    }
  }

}
