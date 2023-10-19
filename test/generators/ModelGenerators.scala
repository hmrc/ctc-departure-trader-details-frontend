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

package generators

import models.AddressLine.{Country => _}
import models.LockCheck.{LockCheckFailure, Locked, Unlocked}
import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs._

trait ModelGenerators {
  self: Generators =>

  lazy val arbitraryDeclarationType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("T", "T1", "T2", "T2F", "TIR")
    }

  lazy val arbitrarySecurityDetailsType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("0", "1", "2", "3")
    }

  lazy val arbitrarySomeSecurityDetailsType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("1", "2", "3")
    }

  lazy val arbitraryNonTIRDeclarationType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("T", "T1", "T2", "T2F")
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      Gen
        .pick(CountryCode.Constants.countryCodeLength, 'A' to 'Z')
        .map(
          code => CountryCode(code.mkString)
        )
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        code <- arbitrary[CountryCode]
        name <- nonEmptyString
      } yield Country(code, name)
    }

  implicit lazy val arbitraryLocalReferenceNumber: Arbitrary[LocalReferenceNumber] =
    Arbitrary {
      for {
        lrn <- stringsWithMaxLength(22: Int, Gen.alphaNumChar)
      } yield new LocalReferenceNumber(lrn)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] =
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17: Int)
      } yield EoriNumber(number)
    }

  implicit lazy val arbitraryDynamicAddress: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- nonEmptyString
        city            <- nonEmptyString
        postalCode      <- Gen.option(nonEmptyString)
      } yield DynamicAddress(numberAndStreet, city, postalCode)
    }

  lazy val arbitraryDynamicAddressWithRequiredPostalCode: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- nonEmptyString
        city            <- nonEmptyString
        postalCode      <- nonEmptyString
      } yield DynamicAddress(numberAndStreet, city, Some(postalCode))
    }

  implicit lazy val arbitraryMode: Arbitrary[Mode] = Arbitrary {
    Gen.oneOf(NormalMode, CheckMode)
  }

  implicit def arbitrarySelectableList[T <: Selectable](implicit arbitrary: Arbitrary[T]): Arbitrary[SelectableList[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield SelectableList(values.distinctBy(_.value))
  }

  implicit lazy val arbitraryIndex: Arbitrary[Index] = Arbitrary {
    for {
      position <- Gen.choose(0: Int, 10: Int)
    } yield Index(position)
  }

  implicit lazy val arbitraryCall: Arbitrary[Call] = Arbitrary {
    for {
      method <- Gen.oneOf(GET, POST)
      url    <- nonEmptyString
    } yield Call(method, url)
  }

  lazy val arbitraryIncompleteTaskStatus: Arbitrary[TaskStatus] = Arbitrary {
    Gen.oneOf(TaskStatus.InProgress, TaskStatus.NotStarted, TaskStatus.CannotStartYet)
  }

  implicit lazy val arbitraryLockCheck: Arbitrary[LockCheck] =
    Arbitrary {
      Gen.oneOf(Locked, Unlocked, LockCheckFailure)
    }

  implicit lazy val arbitrarySubmissionState: Arbitrary[SubmissionState] = Arbitrary {
    val values = Seq(
      SubmissionState.NotSubmitted,
      SubmissionState.Submitted,
      SubmissionState.RejectedPendingChanges,
      SubmissionState.Amendment
    )
    Gen.oneOf(values)
  }

}
