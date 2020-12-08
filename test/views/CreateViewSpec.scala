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

///*
// * Copyright 2020 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
package views

import controllers.{routes}
import forms.mappings.ConversationForm
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import views.html.create

class CreateViewSpec extends PlaySpec with GuiceOneAppPerSuite{
  val messagesApi = app.injector.instanceOf[MessagesApi]
  implicit val messages = MessagesImpl(Lang("en"), messagesApi)
  implicit val request = FakeRequest("GET", "/")

  "Create a conversation form" must {
    "have all fields" in {
      val app: Application = new GuiceApplicationBuilder().build()
      val template = app.injector.instanceOf[create]
      val call = routes.ConversationController.submitQuery()

      val document = template(ConversationForm(), call, Seq.empty)

      document.body must include("query.subject")
      document.body must include("query.message")
      document.body must include("query.language")
      document.body must include("sender.name")
      document.body must include("sender.conversation-id")
      document.body must include("sender.display-name")
      document.body must include("sender-parameter1.key")
      document.body must include("sender-parameter1.value")
      document.body must include("sender-parameter2.key")
      document.body must include("sender-parameter2.value")
      document.body must include("customer.name")
      document.body must include("customer.email")
      document.body must include("customer.enrolment-key")
      document.body must include("customer.enrolment-name")
      document.body must include("customer.enrolment-value")
      document.body must include("alert.template-id")
      document.body must include("alert-parameter1.key")
      document.body must include("alert-parameter1.value")
      document.body must include("alert-parameter2.key")
      document.body must include("alert-parameter2.value")
      document.body must include("tags-parameter1.key")
      document.body must include("tags-parameter1.value")
      document.body must include("tags-parameter2.key")
      document.body must include("tags-parameter2.value")
      document.body must include("tags-parameter3.key")
      document.body must include("tags-parameter3.value")
      document.body must include("tags-parameter4.key")
      document.body must include("tags-parameter4.value")
      document.body must include("tags-parameter5.key")
      document.body must include("tags-parameter5.value")
    }
  }
 }


