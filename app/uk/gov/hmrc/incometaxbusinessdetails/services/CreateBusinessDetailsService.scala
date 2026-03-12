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

import connectors.hip.CreateBusinessDetailsHipConnector
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.createIncomeSource.*
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, IncomeSource}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.{Ok, Status}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxbusinessdetails.connectors.hip.ViewAndChangeConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateBusinessDetailsService @Inject()(createBusinessDetailsHipConnector: CreateBusinessDetailsHipConnector,
                                             viewAndChangeConnector: ViewAndChangeConnector)
                                            (implicit ec: ExecutionContext) extends Logging {

  def createBusinessDetails(body: CreateIncomeSourceHipRequest)
                           (implicit headerCarrier: HeaderCarrier): Future[Result] = {
    createBusinessDetailsHipConnector.create(body).flatMap {
      case Right(success) => Future(
          Ok {
          Json.toJson(success)
        })
      case _ => viewAndChangeConnector.create(body).map(res => Status(res.status)(res.json))
    }
  }
}
  

