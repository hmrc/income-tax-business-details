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

import uk.gov.hmrc.incometaxbusinessdetails.models
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.core.NinoModel
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.mvc.*
import play.api.mvc.Results.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxbusinessdetails.config.AppConfig
import uk.gov.hmrc.incometaxbusinessdetails.connectors.hip.{GetBusinessDetailsConnector, ViewAndChangeConnector}
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.IncomeSourceDetailsModel

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSourceDetailsService @Inject()(val getBusinessDetailsFromHipConnector: GetBusinessDetailsConnector,
                                           val viewAndChangeConnector: ViewAndChangeConnector,
                                           val appConfig: AppConfig
                                          )
                                          (implicit ec: ExecutionContext) extends Logging {

  def getIncomeSourceDetails(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[Result] = {
    getBusinessDetailsFromHipConnector.getBusinessDetails(mtdRef, models.hip.incomeSourceDetails.MtdId).flatMap {
      case success: models.hip.incomeSourceDetails.IncomeSourceDetailsModel =>
        Future(Status(OK)(Json.toJson(success)))
      case _ => viewAndChangeConnector.getBusinessDetailsByMtdid(mtdRef).map(res => Status(res.status)(res.json))
    }
  }

  def getNino(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[Result] = {
    getBusinessDetailsFromHipConnector.getBusinessDetails(mtdRef, models.hip.incomeSourceDetails.MtdId).flatMap {
      case success: models.hip.incomeSourceDetails.IncomeSourceDetailsModel =>
        Future(Status(OK)(Json.toJson(NinoModel(success.nino))))
      case _ => viewAndChangeConnector.getBusinessDetailsByMtdid(mtdRef).map(res => Status(res.status)(res.json))
    }
  }
}
