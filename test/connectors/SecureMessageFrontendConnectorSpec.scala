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

import models.Count
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.http.client.{ HttpClientV2, RequestBuilder }
import uk.gov.hmrc.http.{ HeaderCarrier, HttpReads }
import uk.gov.hmrc.play.partials.{ HeaderCarrierForPartialsConverter, HtmlPartial }
import uk.gov.hmrc.play.partials.HtmlPartial.Success
import utils.EnvironmentConfig

import java.net.URL
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageFrontendConnectorSpec extends PlaySpec with ScalaFutures {

  "Secure message frontend connector" must {
    "return conversationsPartial" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("body"))
      when(requestBuilder.execute[HtmlPartial]).thenReturn(Future.successful(htmlPartial))

      val response: HtmlPartial = secureMessageFrontend.conversationsPartial().futureValue

      response mustBe Success(None, Html("body"))
    }

    "return conversationsPartial with query parameters passed" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("body"))
      when(requestBuilder.execute[HtmlPartial]).thenReturn(Future.successful(htmlPartial))

      val queryParams: Seq[(String, String)] = List(("key1", "value1"), ("key1", "value2"))
      val response = secureMessageFrontend.conversationsPartial(queryParams).futureValue

      response mustBe Success(None, Html("body"))
    }

    "return messagePartial" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("messagebody"))
      when(requestBuilder.execute[HtmlPartial]).thenReturn(Future.successful(htmlPartial))

      val response = secureMessageFrontend.messagePartial("some-client-id", "111", false).futureValue

      response mustBe Success(None, Html("messagebody"))
    }

    "return letterOrConversationPartial when the message type for a message is conversation" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("body"))
      when(requestBuilder.execute[HtmlPartial]).thenReturn(Future.successful(htmlPartial))

      val response = secureMessageFrontend.letterOrConversationPartial("some-client-id").futureValue

      response mustBe Success(None, Html("body"))
    }

    "return messageCount" in new TestCase {
      implicit val hc: HeaderCarrier = new HeaderCarrier
      val count = Count(5, 2)

      when(requestBuilder.execute[Count](using any[HttpReads[Count]], any[ExecutionContext]))
        .thenReturn(Future.successful(count))

      val response = secureMessageFrontend.messageCount().futureValue

      response mustBe Count(5, 2)
    }

    "return messageCount with query parameters passed" in new TestCase {
      implicit val hc: HeaderCarrier = new HeaderCarrier
      val count = Count(2, 1)

      when(requestBuilder.execute[Count](using any[HttpReads[Count]], any[ExecutionContext]))
        .thenReturn(Future.successful(count))

      val queryParams: Seq[(String, String)] = List(("key1", "value1"), ("key1", "value2"))
      val response = secureMessageFrontend.messageCount(queryParams).futureValue

      response mustBe Count(2, 1)
    }

    class TestCase {
      val httpClient: HttpClientV2 = mock[HttpClientV2]
      val requestBuilder = mock[RequestBuilder]
      val config = Configuration.from(
        Map(
          "microservice.services.secure-message-frontend.host"  -> "localhost",
          " microservice.services.secure-message-frontend.port" -> "9055"
        )
      )
      val envConf: EnvironmentConfig = EnvironmentConfig(config)
      val header = mock[HeaderCarrierForPartialsConverter]
      implicit val rh: RequestHeader = mock[RequestHeader]
      val secureMessageFrontend = new SecureMessageFrontendConnector(httpClient, envConf, header)

      when(httpClient.get(any[URL])(any[HeaderCarrier])).thenReturn(requestBuilder)
    }
  }
}
