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

import models.journeyDomain.OpsError.ReaderError
import models.journeyDomain.consignment.{ConsignmentConsigneeDomain, ConsignmentConsignorDomain, ConsignmentDomain}
import models.journeyDomain.holderOfTransit.HolderOfTransitDomain
import models.journeyDomain.representative.RepresentativeDomain
import models.journeyDomain.{TraderDetailsDomain, UserAnswersReader}
import models.{EoriNumber, LocalReferenceNumber, RichJsObject, SubmissionState, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait UserAnswersGenerator extends UserAnswersEntryGenerators {
  self: Generators =>

  implicit def arbitraryUserAnswers: Arbitrary[UserAnswers] =
    Arbitrary {
      for {
        lrn        <- arbitrary[LocalReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        status     <- arbitrary[SubmissionState]
        answers    <- buildUserAnswers[TraderDetailsDomain](UserAnswers(lrn, eoriNumber, status = status))
      } yield answers
    }

  protected def buildUserAnswers[T](
    initialUserAnswers: UserAnswers
  )(implicit userAnswersReader: UserAnswersReader[T]): Gen[UserAnswers] = {

    def rec(userAnswers: UserAnswers): Gen[UserAnswers] =
      userAnswersReader.run(userAnswers) match {
        case Left(ReaderError(page, _, _)) =>
          generateAnswer
            .apply(page)
            .map {
              value =>
                userAnswers.copy(
                  data = userAnswers.data.setObject(page.path, value).getOrElse(userAnswers.data)
                )
            }
            .flatMap(rec)
        case Right(_) => Gen.const(userAnswers)
      }

    rec(initialUserAnswers)
  }

  def arbitraryTraderDetailsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TraderDetailsDomain](userAnswers)

  def arbitraryHolderOfTransitAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[HolderOfTransitDomain](userAnswers) {
      HolderOfTransitDomain.userAnswersReader.apply(Nil)
    }

  def arbitraryRepresentativeAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[RepresentativeDomain](userAnswers) {
      RepresentativeDomain.userAnswersReader.apply(Nil)
    }

  def arbitraryConsignmentAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ConsignmentDomain](userAnswers) {
      ConsignmentDomain.userAnswersReader.apply(Nil)
    }

  def arbitraryConsignorAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ConsignmentConsignorDomain](userAnswers) {
      ConsignmentConsignorDomain.userAnswersReader.apply(Nil)
    }

  def arbitraryConsigneeAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ConsignmentConsigneeDomain](userAnswers) {
      ConsignmentConsigneeDomain.userAnswersReader.apply(Nil)
    }
}
