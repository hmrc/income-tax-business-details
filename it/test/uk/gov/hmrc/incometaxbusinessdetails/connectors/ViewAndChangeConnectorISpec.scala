/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxbusinessdetails.connectors

import play.api.http.Status.*
import play.api.libs.json.Json
import uk.gov.hmrc.incometaxbusinessdetails.constants.UpdateIncomeSourceIntegrationTestConstants.requestJson
import uk.gov.hmrc.incometaxbusinessdetails.constants.UpdateIncomeSourceTestConstants
import uk.gov.hmrc.incometaxbusinessdetails.helpers.{ComponentSpecBase, WiremockHelper}
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.{UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}

class ViewAndChangeConnectorISpec extends ComponentSpecBase {

  val connector: ViewAndChangeConnector = app.injector.instanceOf[ViewAndChangeConnector]

  val updateIncomeSourceUrl = s"/update-income-source"
  ".updateIncomeSource() is called" when {

    "the response is a 200 - OK" should {

      "return a valid update income source model when successfully retrieved" in {
        val responseBody = Json.toJson(UpdateIncomeSourceTestConstants.successResponse).toString()
        WiremockHelper.stubPut(updateIncomeSourceUrl, OK, responseBody)
        val result = connector.updateIncomeSource(UpdateIncomeSourceTestConstants.request).futureValue

        result shouldBe UpdateIncomeSourceTestConstants.successResponse
      }

      "return an UpdateIncomeSourceErrorModel when an UpdateIncomeSourceResponseError is returned" in {
        val errorModel =UpdateIncomeSourceResponseError(NOT_FOUND, "No record found")
        val responseBody = Json.toJson(errorModel).toString()
        WiremockHelper.stubPut(updateIncomeSourceUrl, NOT_FOUND, responseBody)
        val result = connector.updateIncomeSource(UpdateIncomeSourceTestConstants.request).futureValue

        result shouldBe errorModel
      }

      "return an UpdateIncomeSourceResponseError when an non UpdateIncomeSourceResponseError is returned" in {
        val responseBody = requestJson.toString()
        WiremockHelper.stubPut(updateIncomeSourceUrl, INTERNAL_SERVER_ERROR, responseBody)
        val result = connector.updateIncomeSource(UpdateIncomeSourceTestConstants.request).futureValue

        result shouldBe UpdateIncomeSourceResponseError(INTERNAL_SERVER_ERROR, "Unexpected failed future")
      }
    }
  }
}
