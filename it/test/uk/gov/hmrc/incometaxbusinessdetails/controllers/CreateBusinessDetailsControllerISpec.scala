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

import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.CreateBusinessDetailsHipErrorResponse
import play.api.http.Status.*
import play.api.libs.json.Json
import uk.gov.hmrc.incometaxbusinessdetails.constants.BaseIntegrationTestConstants.testMtdbsa
import uk.gov.hmrc.incometaxbusinessdetails.constants.CreateBusinessDetailsHipIntegrationTestConstants.
{testCreateBusinessDetailsSuccessResponse, testCreateForeignPropertyHipRequest, testCreateForeignPropertyRequest,
  testCreateForeignPropertyRequestNoFlag, testCreateHipForeignPropertyRequestNoFlag,
  testCreateSelfEmploymentHipIncomeSourceRequest, testCreateSelfEmploymentIncomeSourceRequest,
  testCreateUKPropertyHipRequest, testCreateUKPropertyRequest, testIncomeSourceId}
import uk.gov.hmrc.incometaxbusinessdetails.helpers.ComponentSpecBase
import uk.gov.hmrc.incometaxbusinessdetails.helpers.servicemocks.HipCreateBusinessDetailsStub


class CreateBusinessDetailsControllerISpec extends ComponentSpecBase {

  "Calling CreateBusinessDetailsController.createBusinessDetails method" when {
    "authorised with a CreateBusinessIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK response with an incomeSourceId" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(CREATED, testCreateSelfEmploymentHipIncomeSourceRequest(),
              testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = BusinessDetailsFrontend.createBusinessDetails(testCreateSelfEmploymentIncomeSourceRequest())

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateSelfEmploymentHipIncomeSourceRequest())

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }
      }

    }
    "authorised with a CreateUKPropertyIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK response with an incomeSourceId" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(CREATED, testCreateUKPropertyHipRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = BusinessDetailsFrontend.createBusinessDetails(testCreateUKPropertyRequest)

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateUKPropertyHipRequest)

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }
      }

    }
    "authorised with a CreateForeignPropertyIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK with an incomeSourceId" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(CREATED, testCreateForeignPropertyHipRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = BusinessDetailsFrontend.createBusinessDetails(testCreateForeignPropertyRequest)

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateForeignPropertyHipRequest)

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }

        s"return $OK with an incomeSourceId with no flag" in {
          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(CREATED, testCreateHipForeignPropertyRequestNoFlag, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = BusinessDetailsFrontend.createBusinessDetails(testCreateForeignPropertyRequestNoFlag)

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateHipForeignPropertyRequestNoFlag)

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }
      }

    }
    "authorised with a invalid request" should {
      s"return $BAD_REQUEST" in {

        isAuthorised(true)

        val invalidRequest = Json.obj()

        When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
        val res = BusinessDetailsFrontend.createBusinessDetails(invalidRequest)

        Then(s"a status of $BAD_REQUEST is returned ")

        res should have(httpStatus(BAD_REQUEST))
      }
    }
    "authorised with a valid request" when {
      "API returns an error" should {
        s"return $INTERNAL_SERVER_ERROR" in {

          isAuthorised(true)

          And(s"I wiremock stub an $INTERNAL_SERVER_ERROR response from HIP")
          HipCreateBusinessDetailsStub.stubPostHipBusinessDetails(
            INTERNAL_SERVER_ERROR,
            testCreateHipForeignPropertyRequestNoFlag,
            Json.toJson(CreateBusinessDetailsHipErrorResponse(INTERNAL_SERVER_ERROR, "failed to create income source"))
          )

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = BusinessDetailsFrontend.createBusinessDetails(
            Json.toJson(testCreateHipForeignPropertyRequestNoFlag)
          )

          Then(s"a status of $INTERNAL_SERVER_ERROR is returned")

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateHipForeignPropertyRequestNoFlag)

          res should have(httpStatus(INTERNAL_SERVER_ERROR))
        }
      }
    }
  }
}
