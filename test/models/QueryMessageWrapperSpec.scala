/*
 * Copyright 2022 HM Revenue & Customs
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

import org.joda.time.format.DateTimeFormat
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class QueryMessageWrapperSpec extends PlaySpec {
  "QueryMessageWrapper Reads" must {
    "create QueryMessageWrapper if json valid" in {
      Json.parse(s"""|{
                     |  "querymessageRequest" : {
                     |    "requestCommon" : {
                     |      "originatingSystem" : "dc-secure-message",
                     |      "receiptDate" : "2021-04-01T14:32:48Z",
                     |      "acknowledgementReference" : "acknowledgementReference"
                     |    },
                     |    "requestDetail" : {
                     |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                     |      "conversationId" : "D-80542-20201120",
                     |      "message" : "QmxhaCBibGFoIGJsYWg="
                     |    }
                     |  }
                     |}""".stripMargin).as[QueryMessageWrapper] mustBe queryMessageWrapper
    }

    "fail to create QueryMessageWrapper if message is not Base64 encoded" in {
      Json.parse(s"""|{
                     |  "querymessageRequest" : {
                     |    "requestCommon" : {
                     |      "originatingSystem" : "dc-secure-message",
                     |      "receiptDate" : "2021-04-01T14:32:48Z",
                     |      "acknowledgementReference" : "acknowledgementReference"
                     |    },
                     |    "requestDetail" : {
                     |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                     |      "conversationId" : "D-80542-20201120",
                     |      "message" : "QmxhaCBibGFoIGJsYWg=Ő"
                     |    }
                     |  }
                     |}""".stripMargin).validate[QueryMessageWrapper] mustBe a[JsError]
    }

    "fail to create QueryMessageWrapper if conversationId length greater than 22" in {
      Json.parse(s"""|{
                     |  "querymessageRequest" : {
                     |    "requestCommon" : {
                     |      "originatingSystem" : "dc-secure-message",
                     |      "receiptDate" : "2021-04-01T14:32:48Z",
                     |      "acknowledgementReference" : "acknowledgementReference"
                     |    },
                     |    "requestDetail" : {
                     |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                     |      "conversationId" : "D-80542-20201120123456789",
                     |      "message" : "QmxhaCBibGFoIGJsYWg="
                     |    }
                     |  }
                     |}""".stripMargin).validate[QueryMessageWrapper] mustBe a[JsError]
    }

    "fail to create QueryMessageWrapper if conversationId length is 0" in {
      Json.parse(s"""|{
                     |  "querymessageRequest" : {
                     |    "requestCommon" : {
                     |      "originatingSystem" : "dc-secure-message",
                     |      "receiptDate" : "2021-04-01T14:32:48Z",
                     |      "acknowledgementReference" : "acknowledgementReference"
                     |    },
                     |    "requestDetail" : {
                     |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                     |      "conversationId" : "",
                     |      "message" : "QmxhaCBibGFoIGJsYWg="
                     |    }
                     |  }
                     |}""".stripMargin).validate[QueryMessageWrapper] mustBe a[JsError]
    }

    "fail to create QueryMessageWrapper if message length is 0" in {
      Json.parse(s"""|{
                     |  "querymessageRequest" : {
                     |    "requestCommon" : {
                     |      "originatingSystem" : "dc-secure-message",
                     |      "receiptDate" : "2021-04-01T14:32:48Z",
                     |      "acknowledgementReference" : "acknowledgementReference"
                     |    },
                     |    "requestDetail" : {
                     |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                     |      "conversationId" : "D-80542-20201120",
                     |      "message" : ""
                     |    }
                     |  }
                     |}""".stripMargin).validate[QueryMessageWrapper] mustBe a[JsError]
    }
  }

  val dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
  val dt = dtf.parseDateTime("2021-04-01T14:32:48Z")
  val queryMessageWrapper =
    QueryMessageWrapper(
      QueryMessageRequest(
        RequestCommon(
          originatingSystem = "dc-secure-message",
          receiptDate = dt,
          acknowledgementReference = "acknowledgementReference"),
        RequestDetail("govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6", "D-80542-20201120", "QmxhaCBibGFoIGJsYWg=")
      ))
}
