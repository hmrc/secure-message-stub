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

package controllers

import config.FrontendAppConfig
import forms.mappings.CryptoForm.CryptoData
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{ times, verify, when }
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc._
import play.api.test.{ FakeRequest, Helpers }
import uk.gov.hmrc.http.HeaderCarrier
import utils.Encryption
import views.html.crypto
import play.twirl.api.Html

class CryptoControllerSpec extends PlaySpec with ScalaFutures {

  "Crypto Controller" must {
    "submit crypto form to crypto utility" in new TestCase {
      val controller = new CryptoController(
        Helpers.stubMessagesControllerComponents(),
        encryptionMock,
        cryptoViewMock
      )

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
      controller.decrypt(cryptoData)(request)
      verify(encryptionMock, times(1)).decrypt(any(), any())
    }

    class TestCase {
      val encryptionMock = mock[Encryption]
      val cryptoViewMock = mock[crypto]
      implicit val hc: HeaderCarrier = HeaderCarrier()
      implicit val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

      val cryptoData = CryptoData(
        cryptoKey = Some("crypto key"),
        scrambledText = Some("scrambled text")
      )
      when(encryptionMock.decrypt(any(), any())).thenReturn("deciphered text")
      when(cryptoViewMock.apply(any(), any())(any(), any[Messages]))
        .thenReturn(Html("<div>result</div>"))
    }
  }
}
