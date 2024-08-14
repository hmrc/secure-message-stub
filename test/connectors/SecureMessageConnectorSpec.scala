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

import models.{ Alert, ConversationRequest, Identifier, Sender, System }
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.*
import play.api.Configuration
import play.api.http.Status.CREATED
import uk.gov.hmrc.http.client.{ HttpClientV2, RequestBuilder }
import uk.gov.hmrc.http.{ HeaderCarrier, HttpReads, HttpResponse }
import utils.EnvironmentConfig

import java.net.URL
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageConnectorSpec extends PlaySpec with ScalaFutures {

  "Secure message connector" must {
    "return HttpResonse" in new TestCase {
      val httpResponse = HttpResponse(CREATED, "body")

      when(httpClient.put(any[URL])(any[HeaderCarrier])).thenReturn(requestBuilder)
      when(requestBuilder.withBody(any)(using any, any, any)).thenReturn(requestBuilder)
      when(requestBuilder.execute[HttpResponse](using any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(httpResponse))

      secureMessage
        .create(
          "cdcm",
          "123456789",
          ConversationRequest(
            Sender(System("name", Identifier("name", "value", None), Map.empty[String, String], "displayName")),
            List.empty,
            Alert("templateId", None),
            Map.empty[String, String],
            "subject",
            "message",
            None
          )
        )
        .futureValue mustBe httpResponse
    }

    class TestCase {
      val httpClient: HttpClientV2 = mock[HttpClientV2]
      val requestBuilder = mock[RequestBuilder]
      val config = Configuration.from(
        Map(
          "microservice.services.secure-message.host"  -> "localhost",
          " microservice.services.secure-message.port" -> "9051"
        )
      )
      val envConf: EnvironmentConfig = EnvironmentConfig(config)
      implicit val hc: HeaderCarrier = new HeaderCarrier
      val secureMessage = new SecureMessageConnector(httpClient, envConf)
    }
  }
}
