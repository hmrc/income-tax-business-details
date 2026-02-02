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

package uk.gov.hmrc.incometaxbusinessdetails.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, reset, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import uk.gov.hmrc.incometaxbusinessdetails.models.updateIncomeSource.UpdateIncomeSourceResponse
import uk.gov.hmrc.incometaxbusinessdetails.services.UpdateIncomeSourceService

import scala.concurrent.Future

trait MockUpdateIncomeSourceService extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockUpdateIncomeSourceService: UpdateIncomeSourceService = mock(classOf[UpdateIncomeSourceService])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUpdateIncomeSourceService)
  }

  def mockUpdateIncomeSource(response: UpdateIncomeSourceResponse): Unit = {
    when(mockUpdateIncomeSourceService.updateIncomeSource(ArgumentMatchers.any())(ArgumentMatchers.any(),ArgumentMatchers.any()))
    .thenReturn(Future.successful(response))
  }
}
