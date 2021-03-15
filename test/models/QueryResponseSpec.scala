/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class QueryResponseSpec extends PlaySpec {
  "QueryResponse Reads" must {
    "create QueryResponse if json valid" in {
      Json.parse("""{"queryResponse": { "id": "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                   |"conversationId":  "D-80542-20201120",
                   |"message": "QmxhaCBibGFoIGJsYWg=" }}""".stripMargin).as[QueryResponse] mustBe queryResponse
    }

    "fail to create QueryResponse if message is not Base64 encoded" in {
      Json.parse("""{"queryResponse": { "id": "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                   |"conversationId":  "D-80542-20201120",
                   |"message": "QmxhaCBibGFoIGJsYWg=Ő" }}""".stripMargin).validate[QueryResponse] mustBe a[JsError]
    }

    "fail to create QueryResponse if conversationId length greater than 22" in {
      Json.parse("""{"queryResponse": { "id": "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                   |"conversationId":  "D-80542-20201120123456789",
                   |"message": "QmxhaCBibGFoIGJsYWg=" }}""".stripMargin).validate[QueryResponse] mustBe a[JsError]
    }

    "fail to create QueryResponse if conversationId length is 0" in {
      Json.parse("""{"queryResponse": { "id": "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                   |"conversationId":  "",
                   |"message": "QmxhaCBibGFoIGJsYWg=" }}""".stripMargin).validate[QueryResponse] mustBe a[JsError]
    }

    "fail to create QueryResponse if message length is 0" in {
      Json.parse("""{"queryResponse": { "id": "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                   |"conversationId":  "D-80542-20201120",
                   |"message": "" }}""".stripMargin).validate[QueryResponse] mustBe a[JsError]
    }
  }

  val queryResponse =
    QueryResponse("govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6", "D-80542-20201120", "QmxhaCBibGFoIGJsYWg=")
}
