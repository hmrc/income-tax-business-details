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

package uk.gov.hmrc.incometaxbusinessdetails.connectors

import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.incometaxbusinessdetails.config.AppConfig
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.
      {UpdateIncomeSourceResponse, UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.request.UpdateIncomeSourceRequestModel

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewAndChangeConnector @Inject()(val http: HttpClientV2,
                                       val appConfig: AppConfig
                                      )(implicit ec: ExecutionContext) extends RawResponseReads {

  def updateIncomeSource(body: UpdateIncomeSourceRequestModel)(implicit headerCarrier: HeaderCarrier): Future[UpdateIncomeSourceResponse] = {
    val url = s"${appConfig.viewAndChangeBaseUrl}/update-income-source"
    http.put(url"$url")
      .withBody(Json.toJson[UpdateIncomeSourceRequestModel](body))
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK =>
            logger.info(s"RESPONSE status: ${response.status}, body: ${response.body}")
            response.json.validate[UpdateIncomeSourceResponseModel](UpdateIncomeSourceResponseModel.format).fold(
              invalid => {
                logger.error(s"Json validation error: $invalid")
                UpdateIncomeSourceResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data")
              },
              valid => {
                logger.info("successfully parsed response to UpdateIncomeSourceResponseModel")
                valid
              }
            )
          case _ =>
            logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}")
            response.json.as[UpdateIncomeSourceResponseError]
        }
      } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        UpdateIncomeSourceResponseError(INTERNAL_SERVER_ERROR, s"Unexpected failed future")
    }
  }
}



