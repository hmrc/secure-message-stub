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

package controllers

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{ JsValue, Json }
import play.api.http.Status.{ BAD_REQUEST, INTERNAL_SERVER_ERROR, NO_CONTENT, UNAUTHORIZED }
import play.api.test.{ FakeRequest, NoMaterializer }
import play.api.test.Helpers.{ AUTHORIZATION, CONTENT_TYPE, JSON, PUT, defaultAwaitTimeout, status }

class EISControllerSpec extends PlaySpec with GuiceOneAppPerSuite {
  implicit val mat: Materializer = NoMaterializer

  "EISController QueryMessageWrapper" must {
    "return NO_CONTENT if the call is valid and accepted" in {
      val eisController = app.injector.instanceOf[EISController]
      status(eisController.queryResponse(requestBuilder(Seq(auth, contentType), validQueryMessageWrapper))) mustBe NO_CONTENT
    }
    "return UNAUTHORIZED if there is not a not a AUTHORIZATION header" in {
      val eisController = app.injector.instanceOf[EISController]
      status(eisController.queryResponse(requestBuilder(Seq(contentType), validQueryMessageWrapper))) mustBe UNAUTHORIZED
    }
    "return UNAUTHORIZED if the Bearer token is invalid" in {
      val eisController = app.injector.instanceOf[EISController]
      status(eisController.queryResponse(requestBuilder(Seq(badAuth, contentType), validQueryMessageWrapper))) mustBe UNAUTHORIZED
    }
    "return BAD_REQUEST if the QueryMessageWrapper is not valid" in {
      val eisController = app.injector.instanceOf[EISController]
      status(eisController.queryResponse(requestBuilder(Seq(auth, contentType), inValidQueryMessageWrapper))) mustBe BAD_REQUEST
    }
    "return INTERNAL_SERVER_ERROR if the QueryMessageWrapper is not valid" in {
      val eisController = app.injector.instanceOf[EISController]
      status(eisController.queryResponse(requestBuilder(Seq(auth, contentType), errCausingQueryMessageWrapper))) mustBe INTERNAL_SERVER_ERROR
    }
  }

  def requestBuilder(hdrs: Seq[(String, String)], body: JsValue): FakeRequest[JsValue] =
    FakeRequest(PUT, "/prsup/PRRestService/DMS/Service/QueryMessageRequest")
      .withHeaders(hdrs: _*)
      .withBody(body)

  private val validQueryMessageWrapper = Json.parse(s"""|{
                                                        |  "queryMessageRequest" : {
                                                        |    "requestCommon" : {
                                                        |      "originatingSystem" : "dc-secure-message",
                                                        |      "receiptDate" : "2021-04-01T14:32:48+01:00",
                                                        |      "acknowledgementReference" : "acknowledgementReference"
                                                        |    },
                                                        |    "requestDetail" : {
                                                        |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                                                        |      "conversationId" : "D-80542-20201120",
                                                        |      "message" : "QmxhaCBibGFoIGJsYWg="
                                                        |    }
                                                        |  }
                                                        |}""".stripMargin)

  private val inValidQueryMessageWrapper =
    Json.parse("""{"requestDetail": { "id": "cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                 |"conversationId":  "D-80542-20201120"}}""".stripMargin)

  private val errCausingQueryMessageWrapper = Json.parse(s"""|{
                                                             |  "queryMessageRequest" : {
                                                             |    "requestCommon" : {
                                                             |      "originatingSystem" : "dc-secure-message",
                                                             |      "receiptDate" : "2021-04-01T14:32:48+01:00",
                                                             |      "acknowledgementReference" : "acknowledgementReference"
                                                             |    },
                                                             |    "requestDetail" : {
                                                             |      "id" : "govuk-tax-cdc3f605-cb77-4025-a48d-b733cd88c3e6",
                                                             |      "conversationId" : "D-80542-20201120err",
                                                             |      "message" : "QmxhaCBibGFoIGJsYWg="
                                                             |    }
                                                             |  }
                                                             |}""".stripMargin)

  val contentType: (String, String) = (CONTENT_TYPE, JSON)
  val auth = (AUTHORIZATION, "Bearer AbCdEf123456")
  val badAuth = (AUTHORIZATION, "Bearer AbCdEf123444")
}
