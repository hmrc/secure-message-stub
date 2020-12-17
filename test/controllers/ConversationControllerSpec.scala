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
package controllers

import connectors.SecureMessageConnector
import forms.mappings.ConversationForm.ConversationData
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import views.html.{create, success_feedback}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConversationControllerSpec extends PlaySpec with ScalaFutures {

  "Conversation Controller" must {
    "submit query to secure message" in new TestCase {
      when(secureMessageConnector.create(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(CREATED, "it works")))
      val controller = new ConversationController(
        Helpers.stubMessagesControllerComponents(),
        secureMessageConnector,
        success_feedback,
        create
      )

      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
      controller.saveConversation(conversation)(request)
      verify(secureMessageConnector, times(1))
        .create(any(), any(), any())(any(), any())
    }

    class TestCase {
      val secureMessageConnector = mock[SecureMessageConnector]
      val success_feedback = mock[success_feedback]
      val create = mock[create]
      implicit val hc: HeaderCarrier = HeaderCarrier()

      val conversation = ConversationData(
        query = (Some("subject"), Some("message"), Some("query.language-welsh")),
        sender =
          (Some("senderName"), Some("conversationId"), Some("displayName")),
        senderParameter1 = (Some("senderkey1"), Some("sendervalue1")),
        senderParameter2 = (Some("senderkey1"), Some("sendervalue1")),
        customer = (
          Some("name"),
          Some("test@test.com"),
          Some("enrolmentKey"),
          Some("enrolmentName"),
          Some("enrolmentValue")
        ),
        alertTemplate = Some("alertTemplate"),
        alertParameter1 = (Some("key"), Some("value")),
        alertParameter2 = (Some("key"), Some("value")),
        tagsParameter1 = (Some("key"), Some("value")),
        tagsParameter2 = (Some("key"), Some("value")),
        tagsParameter3 = (Some("key"), Some("value")),
        tagsParameter4 = (Some("key"), Some("value")),
        tagsParameter5 = (Some("key"), Some("value"))
      )
    }
  }
}
