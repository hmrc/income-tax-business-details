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

import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.incometaxbusinessdetails.config.AppConfig
import play.api.libs.json.Json
import play.api.Logger
import play.api.http.Status
import play.api.http.Status.*
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.incometaxbusinessdetails.connectors.RawResponseReads
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.{CreateIncomeSourceHipApi, GetBusinessDetailsHipApi}
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{BusinessDetailsAccessType, MtdId, Nino}
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.createIncomeSource.CreateIncomeSourceHipRequest
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, CreateBusinessDetailsHipModel, IncomeSource}

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

  def getHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(GetBusinessDetailsHipApi, Some(xMessageTypeFor5266))

  def getBusinessDetailsByNino(nino: String)
                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/get-business-details/nino/$nino"
    

    logger.debug(s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: $getHeaders")

    http
      .get(url"$url")
      .setHeader(
        getHeaders: _*
      )
      .execute[HttpResponse]
  }

  def getBusinessDetailsByMtdid(mtdRef: String)
                              (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/income-sources/$mtdRef"

    logger.debug(s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: $getHeaders")

    http
      .get(url"$url")
      .setHeader(
        getHeaders: _*
      )
      .execute[HttpResponse]
  }

  val getUrl: String = s"${appConfig.viewAndChangeBaseUrl}/income-tax-view-change/create-income-source/business"

  def createBusinessDetailsGetHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(CreateIncomeSourceHipApi, Some(xMessageTypeFor5265))

  def create(body: CreateIncomeSourceHipRequest)
            (implicit headerCarrier: HeaderCarrier): Future[Either[CreateBusinessDetailsHipErrorResponse, List[IncomeSource]]] = {

    val hc: HeaderCarrier = headerCarrier
      .copy(authorization = Some(Authorization(appConfig.desToken)))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    logWithDebug(s"Calling POST $getUrl \n\nHeaders: $headerCarrier \nAuth Headers: $createBusinessDetailsGetHeaders")

    http.post(url"$getUrl")(hc)
      .setHeader(createBusinessDetailsGetHeaders: _*)
      .withBody(Json.toJson[CreateIncomeSourceHipRequest](body))
      .execute[HttpResponse]
      .map {
        case response if response.status == CREATED =>
          logWithInfo(s"SUCCESS - ${response.json}")
          response.json.validate[CreateBusinessDetailsHipModel].fold(
            invalidJson => {
              logWithError(s"Invalid Json with $invalidJson")
              Left(response.json.as[CreateBusinessDetailsHipErrorResponse])
            },
            (res: CreateBusinessDetailsHipModel) => Right(res.success.incomeSourceIdDetails)
          )
        case errorResponse =>
          logWithError(s"Error with response code: ${errorResponse.status} and body: ${errorResponse.json}")
          Left(errorResponse.json.as[CreateBusinessDetailsHipErrorResponse])
      } recover {
      case ex =>
        logWithError(s"[CreateBusinessDetailsHipConnector] Unexpected error: ${ex.getMessage}")
        Left(CreateBusinessDetailsHipErrorResponse(Status.INTERNAL_SERVER_ERROR, s"${ex.getMessage}"))
    }
  }

  private val logWithError: String => Unit = message => Logger("application").error(message)
  private val logWithDebug: String => Unit = message => Logger("application").debug(message)
  private val logWithInfo: String => Unit = message => Logger("application").info(message)
  
}
