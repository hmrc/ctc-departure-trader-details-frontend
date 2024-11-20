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

package forms

import config.PhaseConfig
import forms.mappings.Mappings
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form

import javax.inject.Inject

sealed trait NameFormProvider extends Mappings {

  val maxNameLength: Int

  def apply(prefix: String): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxNameLength, s"$prefix.error.length"),
            regexp(stringFieldRegex, s"$prefix.error.invalid")
          )
        )
    )
}

class StaticNameFormProvider extends NameFormProvider {
  override val maxNameLength: Int = Constants.maxNameLength
}

class DynamicNameFormProvider @Inject() (implicit phaseConfig: PhaseConfig) extends NameFormProvider {
  override val maxNameLength: Int = phaseConfig.values.maxNameLength
}
