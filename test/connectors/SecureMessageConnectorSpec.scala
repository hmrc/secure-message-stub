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

/*
 * Copyright 2020 HM Revenue & Customs
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
import org.mockito.Matchers.{ any, anyString }
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play._
import play.api.http.Status.CREATED
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpResponse }
import utils.EnvironmentConfig
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageConnectorSpec extends PlaySpec with ScalaFutures {

  "Secure message connector" must {
    "return HttpResonse" in new TestCase {
      val httpResponse = HttpResponse(CREATED, "body")
      when(
        httpClient.PUT[ConversationRequest, HttpResponse](
          anyString(),
          any(),
          any()
        )(any(), any(), any(), any())
      ).thenReturn(Future.successful(httpResponse))
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
        .futureValue mustBe (httpResponse)
    }

    class TestCase {
      val httpClient: HttpClient = mock[HttpClient]
      val envConf: EnvironmentConfig = mock[EnvironmentConfig]
      implicit val hc: HeaderCarrier = new HeaderCarrier
      implicit val ec = implicitly[ExecutionContext]
      val secureMessage = new SecureMessageConnector(httpClient, envConf)
    }
  }
}
