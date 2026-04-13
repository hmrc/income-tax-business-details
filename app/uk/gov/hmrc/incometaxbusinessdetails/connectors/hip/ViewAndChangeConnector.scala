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

package uk.gov.hmrc.incometaxbusinessdetails.connectors.hip

import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.incometaxbusinessdetails.config.AppConfig
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.incometaxbusinessdetails.connectors.RawResponseReads
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{BusinessDetailsAccessType, MtdId, Nino}
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.createIncomeSource.CreateIncomeSourceHipRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ViewAndChangeConnector @Inject()(val http:HttpClientV2,
                                       val appConfig: AppConfig)
                                      (implicit ec: ExecutionContext) extends RawResponseReads with HipConnectorDataHelper {

  def getBusinessDetailsUrl(accessType: BusinessDetailsAccessType, ninoOrMtdRef: String): String = {
    accessType match {
      case Nino => s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/get-business-details/nino/$ninoOrMtdRef"
      case MtdId => s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/income-sources/$ninoOrMtdRef"
    }
  }

  def getBusinessDetailsByNino(nino: String)
                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/get-business-details/nino/$nino"
    

    logger.debug(s"Calling GET $url")

    http
      .get(url"$url")
      .execute[HttpResponse]
  }

  def getBusinessDetailsByMtdid(mtdRef: String)
                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/income-sources/$mtdRef"

    logger.debug(s"Calling GET $url")

    http
      .get(url"$url")
      .execute[HttpResponse]
  }

  val getUrl: String = s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/create-income-source/business"


  def create(body: CreateIncomeSourceHipRequest)
            (implicit headerCarrier: HeaderCarrier): Future[HttpResponse] = {
    

    http.post(url"$getUrl")
      .withBody(Json.toJson[CreateIncomeSourceHipRequest](body))
      .execute[HttpResponse]
  }
}
