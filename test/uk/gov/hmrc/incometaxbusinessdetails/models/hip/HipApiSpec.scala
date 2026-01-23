/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.incometaxbusinessdetails.models.hip

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HipApiSpec extends AnyWordSpec with Matchers {
  "GetBusinessDetailsHipApi" should {

    "have the correct name value" in {
      GetBusinessDetailsHipApi.name shouldBe "get-business-details"
    }

    "return the correct value when apply() is called" in {
      GetBusinessDetailsHipApi.apply() shouldBe "get-business-details"
    }

    "behave as a singleton object" in {
      val ref1 = GetBusinessDetailsHipApi
      val ref2 = GetBusinessDetailsHipApi

      ref1 shouldBe theSameInstanceAs(ref2)
    }
  }
  "CreateIncomeSourceHipApi" should {

    "have the correct name value" in {
      CreateIncomeSourceHipApi.name shouldBe "create-income-source"
    }

    "return the correct value when apply() is called" in {
      CreateIncomeSourceHipApi.apply() shouldBe "create-income-source"
    }

    "behave as a singleton object" in {
      val ref1 = CreateIncomeSourceHipApi
      val ref2 = CreateIncomeSourceHipApi

      ref1 shouldBe theSameInstanceAs(ref2)
    }
  }
}
