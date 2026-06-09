/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxbusinessdetails.services

import connectors.hip.CreateBusinessDetailsHipConnector
import org.mockito.ArgumentMatchers.{any, eq as matches}
import org.mockito.Mockito.{mock, when}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Status as ResultStatus
import play.api.test.Helpers.*
import uk.gov.hmrc.incometaxbusinessdetails.constants.CreateHipBusinessDetailsTestConstants.validCreateSelfEmploymentRequest
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, IncomeSource}
import uk.gov.hmrc.incometaxbusinessdetails.utils.TestSupport

import scala.concurrent.Future

class CreateBusinessDetailsServiceSpec extends TestSupport {

  trait Setup {
    val createBusinessDetailsHipConnector: CreateBusinessDetailsHipConnector =
      mock(classOf[CreateBusinessDetailsHipConnector])

    val service = new CreateBusinessDetailsService(createBusinessDetailsHipConnector)
  }

  val testIncomeSourceId: String = "AAIS12345678901"
  val hipError: CreateBusinessDetailsHipErrorResponse =
    CreateBusinessDetailsHipErrorResponse(INTERNAL_SERVER_ERROR, "failed to create income source")

  "createBusinessDetails" when {

    "the call to HIP is successful" should {

      "return a list of Income Sources" in new Setup {
        when(createBusinessDetailsHipConnector.create(matches(validCreateSelfEmploymentRequest))(any()))
          .thenReturn(
            Future.successful(
              Right(List(IncomeSource(testIncomeSourceId)))
            )
          )

        val result: Future[Result] = service.createBusinessDetails(validCreateSelfEmploymentRequest)(hc)

        await(result) shouldBe ResultStatus(OK)(Json.toJson(List(IncomeSource(testIncomeSourceId))))
      }
    }

    "the call to HIP is unsuccessful" should {

      "return the HIP error response" in new Setup {
        when(createBusinessDetailsHipConnector.create(matches(validCreateSelfEmploymentRequest))(any()))
          .thenReturn(Future.successful(Left(hipError)))

        val result: Future[Result] = service.createBusinessDetails(validCreateSelfEmploymentRequest)(hc)

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(hipError)
      }
    }
  }
}
