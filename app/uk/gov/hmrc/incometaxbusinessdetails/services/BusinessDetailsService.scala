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
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incometaxbusinessdetails.config.AppConfig
import uk.gov.hmrc.incometaxbusinessdetails.connectors.hip.{GetBusinessDetailsConnector, ViewAndChangeConnector}
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.incomeSourceDetails.{IncomeSourceDetailsModel, IncomeSourceDetailsResponseModel}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BusinessDetailsService @Inject()(val getBusinessDetailsFromHipConnector: GetBusinessDetailsConnector,
                                       val viewAndChangeConnector: ViewAndChangeConnector,
                                       val appConfig: AppConfig
                                      ) extends Logging {

  def getBusinessDetails(nino: String)
                        (implicit headerCarrier: HeaderCarrier,
                         ec:ExecutionContext): Future[IncomeSourceDetailsResponseModel] = {
    logger.debug("Requesting Income Source Details from Connector")
      getBusinessDetailsFromHipConnector.getBusinessDetails(nino, models.hip.incomeSourceDetails.Nino).flatMap{
        case success: IncomeSourceDetailsModel => Future.successful(success)
        case _ => viewAndChangeConnector.getBusinessDetails(nino, models.hip.incomeSourceDetails.Nino)
      }
  }
}
