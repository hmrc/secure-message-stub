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

package connectors

import com.google.inject.Inject
import models.{ ConversationRequest, SendMessageRequest }
import models.ConversationRequest.conversionRResultWrites
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }
import utils.EnvironmentConfig

import java.net.URI
import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageConnector @Inject() (httpClient: HttpClientV2, envConfig: EnvironmentConfig) {
  private val secureMessageBaseUrl: String = envConfig.baseUrl("secure-message")
  val logger: Logger = Logger(this.getClass.getName)

  def create(
    client: String,
    conversationId: String,
    conversation: ConversationRequest
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[HttpResponse] =
    httpClient
      .put(URI(s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId").toURL)
      .withBody(Json.toJson(conversation))
      .execute[HttpResponse]

  def createSecureMessage(
    sendRequest: SendMessageRequest
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[HttpResponse] = {
    val sourceData: String =
      "WW91IG5lZWQgdG8gZmlsZSBhIFNlbGYgQXNzZXNzbWVudCB0YXggcmV0dXJuIGZvciB0aGUgMjAyNCB0byAyMDI1IHRheCB5ZWFyIGlmIHlvdSBoYXZlbid0IGFscmVhZHkuIFRoZSB0YXggeWVhciBlbmRlZCBvbiA1IEFwcmlsIDIwMjUuCgpZb3UgbXVzdCBmaWxlIHlvdXIgb25saW5lIHJldHVybiBieSAzMSBKYW51YXJ5IDIwMjYuCgpJZiB5b3UndmUgYWxyZWFkeSBjb21wbGV0ZWQgeW91ciB0YXggcmV0dXJuIGZvciB0aGUgMjAyNCB0byAyMDI1IHRheCB5ZWFyLCBvciB3ZSd2ZSB0b2xkIHlvdSB0aGF0IHlvdSBkb24ndCBuZWVkIHRvIHNlbmQgdXMgYSAyMDI0IHRvIDIwMjUgdGF4IHJldHVybiwgeW91IGRvbid0IG5lZWQgdG8gZG8gYW55dGhpbmcgZWxzZS4KCllvdSBjYW4gcGF5IHRocm91Z2ggeW91ciBQYXkgQXMgWW91IEVhcm4gdGF4IGNvZGUgaWYgeW91IG93ZSBsZXNzIHRoYW4"
    def request =
      s"""
         |{
         |   "externalRef":{
         |      "id":"${sendRequest.id}",
         |      "source":"gmc"
         |   },
         |   "recipient":{
         |      "taxIdentifier":{
         |         "name":"nino",
         |         "value":"${sendRequest.nino}"
         |      },
         |      "name":{
         |         "line1":"Bob",
         |         "line2":"Jones"
         |      },
         |      "email":"${sendRequest.email}",
         |      "regime":"paye"
         |   },
         |   "messageType":"mailout-batch",
         |   "details":{
         |      "formId":"SA316",
         |      "sourceData":"$sourceData",
         |      "batchId":"IOSSMessage",
         |      "issueDate":"2025-08-01"
         |   },
         |   "content":[
         |      {
         |         "lang":"en",
         |         "subject":"Import One Stop Shop (IOSS)",
         |         "body": "${content(sendRequest)}"
         |      }
         |   ],
         |   "language":"en"
         |}""".stripMargin

    val jsonRequest = Json.parse(request)
    val bytesSize = jsonRequest.toString.getBytes("UTF-8").length
    val sizeInMB = bytesSize.toDouble / (1024 * 1024) // Convert to MB
    logger.warn(s"Send Message Request size $sizeInMB ")

    httpClient
      .post(URI(s"$secureMessageBaseUrl/secure-messaging/v4/message").toURL)
      .withBody(Json.toJson(jsonRequest))
      .execute[HttpResponse]
  }

  def content(sendRequest: SendMessageRequest): String = {
    val contentRows: Int = sendRequest.contentRows
    val contentBody = "V2UgaGF2ZSByZWNlaXZlZCB5b3VyIFZBVCByZXR1cm4KV2UgaGF2ZSByZWN" +
      "laXZlZCB5b3VyIEltcG9ydCBPbmUgU3RvcCBTaG9wIChJT1NTKSBWQVQgcmV0dXJuIGZvciBBdWd1" +
      "c3QgMjAyMy4KCllvdSBtdXN0IHBheSB0aGUgcmV0dXJuIGluIGZ1bGwgYnkgNSBKdWx5IDIwMjMu" +
      "CgpFVSBjb3VudHJpZXMgY2FuIGNoYXJnZSB5b3UgaW50ZXJlc3Qgb3IgcGVuYWx0aWVzIGZvciBsY" +
      "XRlIHBheW1lbnRzIGFuZCB3ZSBtYXkgcmVtb3ZlIHlvdSBmcm9tIHRoZSBJT1NTIHNjaGVtZSBpZ" +
      "iB5b3UgZG8gbm90IHBheSBpbiBmdWxsLgoKUGF5IG5vdyAKWW91ciBJT1NTIG51bWJlciBpczogWE0wMjk5OTk5OTk5"

    val range: Seq[Int] = 1 to contentRows
    range.map(i => contentBody).mkString(" ")
  }
}
