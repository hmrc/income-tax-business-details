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

package uk.gov.hmrc.incometaxbusinessdetails.config


import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.incometaxbusinessdetails.models.hip.{CreateIncomeSourceHipApi, GetBusinessDetailsHipApi, HipApi}
import uk.gov.hmrc.incometaxbusinessdetails.utils.DateUtils
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.util.{Base64, UUID}
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig) {

  private def loadConfig(key: String) = servicesConfig.getString(key)

  val desToken: String = s"Bearer ${loadConfig("microservice.services.des.authorization-token")}"
  val desEnvironment: String = loadConfig("microservice.services.des.environment")

  val appName: String = servicesConfig.getString("appName")
  lazy val hipUrl: String = servicesConfig.baseUrl("hip")
  
  private def getHipCredentials: String = {
    val clientId = loadConfig(s"microservice.services.hip.clientId")
    val secret = loadConfig(s"microservice.services.hip.secret")

    val encoded = Base64.getEncoder.encodeToString(s"$clientId:$secret".getBytes("UTF-8"))

    s"Basic $encoded"
  }

  def getHIPHeaders(hipApi: HipApi, messageTypeHeaderValue: Option[String] = None): Seq[(String, String)] = {
    val additionalHeaders: Seq[(String, String)] = {
      hipApi match {
        case GetBusinessDetailsHipApi =>
          Seq(
            ("X-Originating-System", "MDTPITVC"),
            ("X-Receipt-Date", DateUtils.nowAsUtc),
            ("X-Regime-Type", "ITSA"),
            ("X-Transmitting-System", "HIP")
          )
        case CreateIncomeSourceHipApi =>
          Seq(
            ("X-Originating-System", "MDTPITVC"),
            ("X-Receipt-Date", DateUtils.nowAsUtc),
            ("X-Regime", "ITSA"),
            ("X-Transmitting-System", "HIP")
          )
       
      }
    }
    messageTypeHeaderValue.map(mtv => Seq(("X-Message-Type", mtv))).getOrElse(Seq()) ++ Seq(
      (HeaderNames.authorisation, getHipCredentials),
      ("correlationId", UUID.randomUUID().toString)
    ) ++ additionalHeaders
  }
  val confidenceLevel: Int = servicesConfig.getInt("auth.confidenceLevel")
  
  val ifUrl: String = loadConfig("microservice.services.if.url")

  def getIFHeaders(api: String): Seq[(String, String)] = {
    val ifEnvironment: String = loadConfig("microservice.services.if.environment")
    val ifToken: String = loadConfig(s"microservice.services.if.authorization-token-$api")
    Seq(
      "Environment" -> ifEnvironment,
      "Authorization" -> s"Bearer $ifToken"
    )
  }
}

