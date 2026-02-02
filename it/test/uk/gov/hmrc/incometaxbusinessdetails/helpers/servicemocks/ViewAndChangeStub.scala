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

package uk.gov.hmrc.incometaxbusinessdetails.helpers.servicemocks

import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.incometaxbusinessdetails.helpers.WiremockHelper
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.*
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.request.UpdateIncomeSourceRequestModel

object ViewAndChangeStub {

  def url: String = {
    s"/income-tax-view-change/update-income-source"
  }


  def stubUpdateIncomeSourceSuccess(request: UpdateIncomeSourceRequestModel, response: UpdateIncomeSourceResponseModel): Unit = {
    WiremockHelper.stubPut(
      url = url,
      status = Status.OK,
      requestBody = Json.toJson(request).toString,
      responseBody = Json.toJson(response).toString
    )
  }

  def stubUpdateIncomeSourceError(status: Int, responseBody: String): Unit = {
    WiremockHelper.stubPut(
      url = url,
      status = status,
      responseBody = responseBody
    )
  }
}