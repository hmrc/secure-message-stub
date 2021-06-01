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

package connectors

import models.Count
import org.mockito.Matchers.{ any, anyString }
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpResponse }
import uk.gov.hmrc.play.partials.{ HeaderCarrierForPartialsConverter, HtmlPartial }
import uk.gov.hmrc.play.partials.HtmlPartial.Success
import utils.EnvironmentConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SecureMessageFrontendConnectorSpec extends PlaySpec with ScalaFutures {

  "Secure message frontend connector" must {
    "return conversationsPartial" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("body"))
      when(
        httpClient.GET[HtmlPartial](
          anyString(),
          any[Seq[(String, String)]](),
          any[Seq[(String, String)]]()
        )(any(), any(), any())
      ).thenReturn(Future.successful(htmlPartial))

      val response: HtmlPartial = secureMessageFrontend.conversationsPartial().futureValue

      response mustBe Success(None, Html("body"))
    }

    "return conversationsPartial with query parameters passed" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("body"))
      when(
        httpClient.GET[HtmlPartial](
          anyString(),
          any[Seq[(String, String)]](),
          any[Seq[(String, String)]]()
        )(any(), any(), any())
      ).thenReturn(Future.successful(htmlPartial))

      val queryParams: Seq[(String, String)] = List(("key1", "value1"), ("key1", "value2"))
      val response = secureMessageFrontend.conversationsPartial(queryParams).futureValue

      response mustBe Success(None, Html("body"))
    }

    "return messagePartial" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("messagebody"))
      when(
        httpClient.GET[HtmlPartial](
          anyString(),
          any[Seq[(String, String)]](),
          any[Seq[(String, String)]]()
        )(any(), any(), any())
      ).thenReturn(Future.successful(htmlPartial))

      val response = secureMessageFrontend.messagePartial("some-client-id", "111", false).futureValue

      response mustBe Success(None, Html("messagebody"))
    }

    "return letterOrConversationPartial when the message type for a message is conversation" in new TestCase {
      val htmlPartial = HtmlPartial.Success(None, Html("body"))
      when(
        httpClient.GET[HtmlPartial](
          anyString(),
          any[Seq[(String, String)]](),
          any[Seq[(String, String)]]()
        )(any(), any(), any())
      ).thenReturn(Future.successful(htmlPartial))

      val response = secureMessageFrontend.letterOrConversationPartial("some-client-id").futureValue

      response mustBe Success(None, Html("body"))
    }

    "return messageCount" in new TestCase {
      implicit val hc: HeaderCarrier = new HeaderCarrier
      val count = Count(5, 2)
      when(
        httpClient.GET[Count](
          anyString(),
          any[Seq[(String, String)]](),
          any[Seq[(String, String)]]()
        )(any(), any(), any())
      ).thenReturn(Future.successful(count))

      val response = secureMessageFrontend.messageCount().futureValue

      response mustBe Count(5, 2)
    }

    "return messageCount with query parameters passed" in new TestCase {
      implicit val hc: HeaderCarrier = new HeaderCarrier
      val count = Count(2, 1)
      when(
        httpClient.GET[Count](
          anyString(),
          any[Seq[(String, String)]](),
          any[Seq[(String, String)]]()
        )(any(), any(), any())
      ).thenReturn(Future.successful(count))

      val queryParams: Seq[(String, String)] = List(("key1", "value1"), ("key1", "value2"))
      val response = secureMessageFrontend.messageCount(queryParams).futureValue

      response mustBe Count(2, 1)
    }

    class TestCase {
      val httpClient: HttpClient = mock[HttpClient]
      val envConf: EnvironmentConfig = mock[EnvironmentConfig]
      val header = mock[HeaderCarrierForPartialsConverter]
      implicit val rh: RequestHeader = mock[RequestHeader]
      val secureMessageFrontend = new SecureMessageFrontendConnector(httpClient, envConf, header)
    }
  }
}
