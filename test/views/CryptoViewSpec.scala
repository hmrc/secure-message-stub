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

package views

import config.FrontendAppConfig
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{ Lang, MessagesApi, MessagesImpl }
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.crypto

class CryptoViewSpec extends PlaySpec with GuiceOneAppPerSuite {
  val messagesApi = app.injector.instanceOf[MessagesApi]
  implicit val messages: MessagesImpl = MessagesImpl(Lang("en"), messagesApi)
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
  implicit val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  "Create a crypto form" must {
    "have all fields" in {
      val app: Application = new GuiceApplicationBuilder().build()
      val template = app.injector.instanceOf[crypto]
      val document = template("deciphered text here2", Seq.empty)

      document.body must include("crypto-key")
      document.body must include("scrambled-text")
      document.body must include("deciphered text here2")
    }
  }
}
