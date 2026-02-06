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


import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxbusinessdetails.connectors.{UpdateIncomeSourceConnector, ViewAndChangeConnector}
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.{UpdateIncomeSourceResponse, UpdateIncomeSourceResponseModel}
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.request.UpdateIncomeSourceRequestModel

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateIncomeSourceService @Inject()(val updateIncomeSourceConnector:  UpdateIncomeSourceConnector,
                                          val viewAndChangeConnector: ViewAndChangeConnector){
  def updateIncomeSource(body: UpdateIncomeSourceRequestModel)
                        (implicit headerCarrier: HeaderCarrier,
                                  ec:ExecutionContext): Future[UpdateIncomeSourceResponse] = {
    updateIncomeSourceConnector.updateIncomeSource(body).flatMap {
      case success: UpdateIncomeSourceResponseModel => Future.successful(success)
      case _ => viewAndChangeConnector.updateIncomeSource(body)
    }
  }
}
                                       
