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

package uk.gov.hmrc.incometaxbusinessdetails.services

import org.mockito.ArgumentMatchers.{any, eq as matches}
import org.mockito.Mockito.{mock, when}
import play.api.test.Helpers.*
import uk.gov.hmrc.incometaxbusinessdetails.connectors.UpdateIncomeSourceConnector
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.UpdateIncomeSourceResponseModel
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.request.UpdateIncomeSourceRequestModel
import uk.gov.hmrc.incometaxbusinessdetails.connectors.ViewAndChangeConnector
import uk.gov.hmrc.incometaxbusinessdetails.constants.UpdateIncomeSourceTestConstants
import uk.gov.hmrc.incometaxbusinessdetails.constants.UpdateIncomeSourceTestConstants.{successResponse, failureResponse}
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.*
import uk.gov.hmrc.incometaxbusinessdetails.utils.TestSupport
import scala.concurrent.Future

class UpdateIncomeSourceServiceSpec extends TestSupport {

  trait Setup {
    val updateIncomeSourceConnector: UpdateIncomeSourceConnector = mock(classOf[UpdateIncomeSourceConnector])
    val viewAndChangeConnector: ViewAndChangeConnector = mock(classOf[ViewAndChangeConnector])
    val service = new UpdateIncomeSourceService(
      updateIncomeSourceConnector,
      viewAndChangeConnector
    )
  }

  "updateIncomeSource" when {
    s"the call to DES is successful" should {
      s"return the success model" in new Setup {
        when(updateIncomeSourceConnector.updateIncomeSource(matches(UpdateIncomeSourceTestConstants.request))(any()))
          .thenReturn(Future.successful(successResponse))

        val result: Future[UpdateIncomeSourceResponse] = service.updateIncomeSource(UpdateIncomeSourceTestConstants.request)(hc, ec)

        await(result) shouldBe successResponse
      }
    }

    "the call to DES is unsuccessful" should {
      "call the view and change connector and return its response" in new Setup {
        when(updateIncomeSourceConnector.updateIncomeSource(any[UpdateIncomeSourceRequestModel])(any()))
          .thenReturn(Future.successful(failureResponse))
        when(viewAndChangeConnector.updateIncomeSource(any[UpdateIncomeSourceRequestModel])(any()))
          .thenReturn(Future.successful(successResponse))
        val result: Future[UpdateIncomeSourceResponse] = service.updateIncomeSource(UpdateIncomeSourceTestConstants.request)(hc, ec)
        await(result) shouldBe successResponse
      }
    }
  }
  
  }


