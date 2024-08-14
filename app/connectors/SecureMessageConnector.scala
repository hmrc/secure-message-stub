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
import models.ConversationRequest
import models.ConversationRequest.conversionRResultWrites
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }
import utils.EnvironmentConfig

import java.net.URI
import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageConnector @Inject() (httpClient: HttpClientV2, envConfig: EnvironmentConfig) {
  val secureMessageBaseUrl = envConfig.baseUrl("secure-message")

  def create(
    client: String,
    conversationId: String,
    conversation: ConversationRequest
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[HttpResponse] =
    httpClient
      .put(URI(s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId").toURL)
      .withBody(Json.toJson(conversation))
      .execute[HttpResponse]
}
