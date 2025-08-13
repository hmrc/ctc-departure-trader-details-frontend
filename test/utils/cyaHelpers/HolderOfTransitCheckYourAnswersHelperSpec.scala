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

package utils.cyaHelpers

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.reference.Country
import models.{DynamicAddress, Mode}
import controllers.holderOfTransit.contact.routes as contactRoutes
import controllers.holderOfTransit.routes as hotRoutes
import pages.holderOfTransit.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.html.components.implicits.*
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

class HolderOfTransitCheckYourAnswersHelperSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "HolderOfTransitCheckYourAnswersHelper" - {

    "tirIdentification" - {
      "must return None" - {
        s"when $TirIdentificationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.tirIdentification
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $TirIdentificationPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(TirIdentificationPage, eori)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.tirIdentification

              result.value mustEqual
                SummaryListRow(
                  key = Key("TIR holder’s identification number".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.TirIdentificationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("TIR holder’s identification number"),
                          attributes = Map("id" -> "change-transit-holder-tir-id-number")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "eoriYesNo" - {
      "must return None" - {
        s"when $EoriYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.eoriYesNo
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $EoriYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(EoriYesNoPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.eoriYesNo

              result.value mustEqual
                SummaryListRow(
                  key = Key("Do you know the transit holder’s EORI number?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.EoriYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you know the transit holder’s EORI number"),
                          attributes = Map("id" -> "change-has-transit-holder-eori")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "eori" - {
      "must return None" - {
        s"when $EoriPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.eori
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $EoriPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(EoriPage, eori)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.eori

              result.value mustEqual
                SummaryListRow(
                  key = Key("EORI number".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.EoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the transit holder’s EORI number"),
                          attributes = Map("id" -> "change-transit-holder-eori-number")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "name" - {
      "must return None" - {
        s"when $NamePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.name
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $NamePage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (name, mode) =>
              val answers = emptyUserAnswers.setValue(NamePage, name)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.name

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s name".toText),
                  value = Value(name.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("transit holder’s name"),
                          attributes = Map("id" -> "change-transit-holder-name")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "country" - {
      "must return None" - {
        s"when $CountryPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.country
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $CountryPage is defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(CountryPage, country)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.country

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s country".toText),
                  value = Value(country.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.CountryController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("transit holder’s country"),
                          attributes = Map("id" -> "change-transit-holder-country")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "address" - {
      "must return None" - {
        s"when $AddressPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.address
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddressPage is defined" in {
          forAll(arbitrary[DynamicAddress], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(AddressPage, address)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.address

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s address".toText),
                  value = Value(HtmlContent(Seq(Some(address.numberAndStreet), Some(address.city), address.postalCode).flatten.mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.AddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("transit holder’s address"),
                          attributes = Map("id" -> "change-transit-holder-address")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "addContact" - {
      "must return None" - {
        s"when $AddContactPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addContact
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddContactPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddContactPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.addContact

              result.value mustEqual
                SummaryListRow(
                  key = Key("Do you want to add a contact?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.AddContactController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add a contact"),
                          attributes = Map("id" -> "change-has-transit-holder-contact")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "contactName" - {
      "must return None" - {
        s"when ${contact.NamePage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactName
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${contact.NamePage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactName, mode) =>
              val answers = emptyUserAnswers.setValue(contact.NamePage, contactName)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.contactName

              result.value mustEqual
                SummaryListRow(
                  key = Key("Contact’s name".toText),
                  value = Value(contactName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("contact’s name"),
                          attributes = Map("id" -> "change-transit-holder-contact-name")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }

    "contactTelephoneNumber" - {
      "must return None" - {
        s"when ${contact.TelephoneNumberPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactTelephoneNumber
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${contact.TelephoneNumberPage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactTelephoneNumber, mode) =>
              val answers = emptyUserAnswers.setValue(contact.TelephoneNumberPage, contactTelephoneNumber)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.contactTelephoneNumber

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s contact phone number".toText),
                  value = Value(contactTelephoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("contact’s phone number"),
                          attributes = Map("id" -> "change-transit-holder-contact-phone-number")
                        )
                      )
                    )
                  )
                )
          }
        }
      }
    }
  }

  "HolderOfTransitCheckYourAnswersHelper on amendment journey" - {

    "tirIdentification" - {

      "must return Some(Row)" - {
        s"when $TirIdentificationPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(TirIdentificationPage, eori)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.tirIdentification

              result.value mustEqual
                SummaryListRow(
                  key = Key("TIR holder’s identification number".toText),
                  value = Value(eori.toText),
                  actions = None
                )
          }
        }
      }
    }

    "eoriYesNo" - {

      "must return Some(Row)" - {
        s"when $EoriYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswersWithAmendment.setValue(EoriYesNoPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.eoriYesNo

              result.value mustEqual
                SummaryListRow(
                  key = Key("Do you know the transit holder’s EORI number?".toText),
                  value = Value("Yes".toText),
                  actions = None
                )
          }
        }
      }
    }

    "eori" - {

      "must return Some(Row)" - {
        s"when $EoriPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(EoriPage, eori)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.eori

              result.value mustEqual
                SummaryListRow(
                  key = Key("EORI number".toText),
                  value = Value(eori.toText),
                  actions = None
                )
          }
        }
      }
    }

    "name" - {

      "must return Some(Row)" - {
        s"when $NamePage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (name, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(NamePage, name)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.name

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s name".toText),
                  value = Value(name.toText),
                  actions = None
                )
          }
        }
      }
    }

    "country" - {

      "must return Some(Row)" - {
        s"when $CountryPage is defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(CountryPage, country)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.country

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s country".toText),
                  value = Value(country.toString.toText),
                  actions = None
                )
          }
        }
      }
    }

    "address" - {

      "must return Some(Row)" - {
        s"when $AddressPage is defined" in {
          forAll(arbitrary[DynamicAddress], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(AddressPage, address)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.address

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s address".toText),
                  value = Value(HtmlContent(Seq(Some(address.numberAndStreet), Some(address.city), address.postalCode).flatten.mkString("<br>"))),
                  actions = None
                )
          }
        }
      }
    }

    "addContact" - {

      "must return Some(Row)" - {
        s"when $AddContactPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswersWithAmendment.setValue(AddContactPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.addContact

              result.value mustEqual
                SummaryListRow(
                  key = Key("Do you want to add a contact?".toText),
                  value = Value("Yes".toText),
                  actions = None
                )
          }
        }
      }
    }

    "contactName" - {

      "must return Some(Row)" - {
        s"when ${contact.NamePage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactName, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(contact.NamePage, contactName)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.contactName

              result.value mustEqual
                SummaryListRow(
                  key = Key("Contact’s name".toText),
                  value = Value(contactName.toText),
                  actions = None
                )
          }
        }
      }
    }

    "contactTelephoneNumber" - {

      "must return Some(Row)" - {
        s"when ${contact.TelephoneNumberPage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactTelephoneNumber, mode) =>
              val answers = emptyUserAnswersWithAmendment.setValue(contact.TelephoneNumberPage, contactTelephoneNumber)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.contactTelephoneNumber

              result.value mustEqual
                SummaryListRow(
                  key = Key("Transit holder’s contact phone number".toText),
                  value = Value(contactTelephoneNumber.toText),
                  actions = None
                )
          }
        }
      }
    }
  }
}
