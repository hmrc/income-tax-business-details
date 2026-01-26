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

package uk.gov.hmrc.incometaxbusinessdetails.constants

import uk.gov.hmrc.incometaxbusinessdetails.models.hip.core.NinoErrorModel
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel, IncomeSourceDetailsNotFound}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.incometaxbusinessdetails.constants.BaseIntegrationTestConstants.{testMtdbsa, testNino}
import uk.gov.hmrc.incometaxbusinessdetails.constants.HipBusinessDetailsIntegrationTestConstants.{testBusinessModel, testPropertyDetailsModel}

object HipIncomeSourceIntegrationTestConstants {

  val incomeSourceDetailsSuccess: IncomeSourceDetailsModel =
    IncomeSourceDetailsModel(
      nino = testNino,
      mtdbsa = testMtdbsa,
      yearOfMigration = Some("2019"),
      businesses = testBusinessModel,
      properties = List(testPropertyDetailsModel)
    )

  val errorJson: JsValue = {
    Json.obj("error" ->
      Json.obj(
        "code" -> "500",
        "message" -> "ISE"
      ))
  }

  val errorJson422GeneralError: JsValue = {
    Json.obj("errors" ->
      Json.obj(
        "code" -> "001",
        "text" -> "REGIME missing or invalid"
      ))
  }

  val errorJson422NotFoundError: JsValue = {
    Json.obj("errors" ->
      Json.obj(
        "code" -> "008",
        "text" -> "Subscription data not found"
      ))
  }
  val incomeSourceDetailsError: IncomeSourceDetailsError = IncomeSourceDetailsError(500, errorJson.toString())
  val incomeSourceDetailsNotFoundError: IncomeSourceDetailsNotFound = IncomeSourceDetailsNotFound(404, errorJson422NotFoundError.toString())

  val ninoLookupError: NinoErrorModel = NinoErrorModel(Status.INTERNAL_SERVER_ERROR, errorJson.toString())

}
