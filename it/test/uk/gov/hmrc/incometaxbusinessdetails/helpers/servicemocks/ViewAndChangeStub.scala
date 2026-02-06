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
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipBusinessDetailsIntegrationTestConstants.{failureResponse, successResponseHip}
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipIncomeSourceIntegrationTestConstants
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipIncomeSourceIntegrationTestConstants.{incomeSourceDetailsError, incomeSourceDetailsNotFoundError, ninoLookupError}
import uk.gov.hmrc.incometaxbusinessdetails.helpers.WiremockHelper
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.core.NinoModel
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.IncomeSourceDetailsModel

object ViewAndChangeStub {

  val viewAndChangeUrl: (String) => String = (mtdRef) => s"""/income-tax-view-change/income-sources/$mtdRef"""

  def stubGetBusinessDetails(mtdRef: String, response: NinoModel): Unit = {
    val expectedResponse = Json.toJson(response).toString
    WiremockHelper.stubGet(viewAndChangeUrl(mtdRef), Status.OK, expectedResponse)
  }

  def stubGetIncomeSourceDetails(nino: String, response: JsValue): Unit = {
    WiremockHelper.stubGet(viewAndChangeUrl(nino), Status.OK, response.toString)
  }

  def stubGetBusinessDetails422NotFound(nino: String): Unit = {
    val ifBusinessDetailsResponse = Json.toJson(incomeSourceDetailsNotFoundError).toString
    WiremockHelper.stubGet(viewAndChangeUrl(nino), Status.NOT_FOUND, ifBusinessDetailsResponse)
  }

  def stubGetBusinessDetailsError(mtdRef: String): Unit = {
    val errorResponse = Json.toJson(incomeSourceDetailsError)
    WiremockHelper.stubGet(viewAndChangeUrl(mtdRef), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

}
