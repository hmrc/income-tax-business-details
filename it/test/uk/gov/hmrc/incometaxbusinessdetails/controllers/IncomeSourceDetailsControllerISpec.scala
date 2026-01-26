/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxbusinessdetails.controllers

import uk.gov.hmrc.incometaxbusinessdetails.models.hip.core.{NinoErrorModel, NinoModel}
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsNotFound}
import play.api.http.Status.*
import uk.gov.hmrc.incometaxbusinessdetails.constants.BaseIntegrationTestConstants.{testMtdRef, testNino}
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipBusinessDetailsIntegrationTestConstants.jsonSuccessOutput
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipIncomeSourceIntegrationTestConstants.{incomeSourceDetailsError, incomeSourceDetailsNotFoundError, incomeSourceDetailsSuccess, ninoLookupError}
import uk.gov.hmrc.incometaxbusinessdetails.helpers.ComponentSpecBase
import uk.gov.hmrc.incometaxbusinessdetails.helpers.servicemocks.BusinessDetailsHipStub


class IncomeSourceDetailsControllerISpec extends ComponentSpecBase {

  "Calling the IncomeSourceDetailsController.getNino method" when {
    "authorised with a valid request" when {
      "A success response is returned from HIP" should {
        "return a valid NINO" in {

          isAuthorised(true)

          And("I wiremock stub a successful getIncomeSourceDetails response")
          BusinessDetailsHipStub.stubGetHipBusinessDetails(testMtdRef, incomeSourceDetailsSuccess)

          When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
          val res = IncomeTaxViewChange.getNino(testMtdRef)

          BusinessDetailsHipStub.verifyGetHipBusinessDetails(testMtdRef)

          Then("a successful response is returned with the correct NINO")

          res should have(
            httpStatus(OK),
            jsonBodyAs[NinoModel](NinoModel(testNino))
          )
        }
      }
      "An error response is returned from IF" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          BusinessDetailsHipStub.stubGetBusinessDetailsError(testMtdRef)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getNino(testMtdRef)

          BusinessDetailsHipStub.verifyGetHipBusinessDetails(testMtdRef)

          Then("an error response is returned")

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs[NinoErrorModel](ninoLookupError)
          )
        }
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
        val res = IncomeTaxViewChange.getNino(testMtdRef)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
  "Calling the IncomeSourceDetailsController.getIncomeSourceDetails method" when {
    "authorised with a valid request" when {
      "A successful response is returned from IF" should {
        "return a valid IncomeSourceDetails model" in {

          isAuthorised(true)

          And("I wiremock stub a successful getIncomeSourceDetails response")
          BusinessDetailsHipStub.stubGetHipBusinessDetails(testMtdRef, incomeSourceDetailsSuccess)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getIncomeSources(testMtdRef)

          BusinessDetailsHipStub.verifyGetHipBusinessDetails(testMtdRef)

          Then("a successful response is returned with the correct NINO")

          res should have(
            httpStatus(OK),
            jsonBodyMatching(jsonSuccessOutput())
          )
        }
      }

      "An 422 response is returned from IF" should {
        "return an 404 Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          BusinessDetailsHipStub.stubGetHipBusinessDetails422NotFound(testMtdRef)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getIncomeSources(testMtdRef)

          BusinessDetailsHipStub.verifyGetHipBusinessDetails(testMtdRef)

          Then("an NOT_FOUND response is returned")

          res should have(
            httpStatus(NOT_FOUND),
            jsonBodyAs[IncomeSourceDetailsNotFound](incomeSourceDetailsNotFoundError)
          )
        }
      }
      "An error response is returned from IF" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          BusinessDetailsHipStub.stubGetBusinessDetailsError(testMtdRef)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getIncomeSources(testMtdRef)

          BusinessDetailsHipStub.verifyGetHipBusinessDetails(testMtdRef)

          Then("an error response is returned")

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs[IncomeSourceDetailsError](incomeSourceDetailsError)
          )
        }
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
        val res = IncomeTaxViewChange.getNino(testMtdRef)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
}
