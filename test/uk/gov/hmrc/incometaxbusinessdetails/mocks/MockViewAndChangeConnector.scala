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

package uk.gov.hmrc.incometaxbusinessdetails.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.incometaxbusinessdetails.connectors.hip.ViewAndChangeConnector
import uk.gov.hmrc.incometaxbusinessdetails.constants.BaseTestConstants.{mtdRef, testNino}
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipIncomeSourceDetailsTestConstants

import scala.concurrent.Future


trait MockViewAndChangeConnector extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockViewAndChangeConnector: ViewAndChangeConnector = mock(classOf[ViewAndChangeConnector])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewAndChangeConnector)
  }
  
  def mockGetBusinessDetailsByNinoResult(): OngoingStubbing[Future[HttpResponse]] =
    when(mockViewAndChangeConnector.getBusinessDetailsByNino(ArgumentMatchers.eq(testNino))(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(200, Json.toJson(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel), Map.empty)))

  def mockGetBusinessDetailsByMtdidResult(): OngoingStubbing[Future[HttpResponse]] =
    when(mockViewAndChangeConnector.getBusinessDetailsByMtdid(ArgumentMatchers.eq(mtdRef))(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(HttpResponse(200, Json.toJson(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel), Map.empty)))
}
