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

import org.mockito.Matchers.{ any, anyString }
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpResponse }
import utils.EnvironmentConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SecureMessageFrontendConnectorSpec extends PlaySpec with ScalaFutures {

  "Secure message frontend connector" must {
    "return conversationPartial" in new TestCase {
      val httpResponse = HttpResponse(200, "body")
      when(
        httpClient.GET[HttpResponse](
          anyString()
        )(any(), any(), any())
      ).thenReturn(Future.successful(httpResponse))
      secureMessageFrontend.conversationsPartial()

      val response = secureMessageFrontend.conversationsPartial().futureValue

      response.status mustBe (200)
      response.body mustBe ("body")

    }

    "return messagePartial" in new TestCase {
      val httpResponse = HttpResponse(200, "messagebody")
      when(
        httpClient.GET[HttpResponse](
          anyString()
        )(any(), any(), any())
      ).thenReturn(Future.successful(httpResponse))

      val response = secureMessageFrontend.messagePartial("111").futureValue

      response.status mustBe (200)
      response.body mustBe ("messagebody")
    }

    class TestCase {
      val httpClient: HttpClient = mock[HttpClient]
      val envConf: EnvironmentConfig = mock[EnvironmentConfig]
      implicit val hc: HeaderCarrier = new HeaderCarrier
      val secureMessageFrontend = new SecureMessageFrontendConnector(httpClient, envConf)
    }
  }
}
