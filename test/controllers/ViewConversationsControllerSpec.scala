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

package controllers

import akka.util.Timeout
import connectors.SecureMessageFrontendConnector
import org.mockito.Matchers
import org.mockito.Matchers.{ any, eq => eqTo }
import org.mockito.Mockito.{ times, verify, when }
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.{ contentAsString, status, stubMessagesControllerComponents }
import play.twirl.api.Html
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }
import views.html.{ error_page, view_conversation_messages, view_conversations }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ViewConversationsControllerSpec extends PlaySpec with ScalaFutures {

  "conversations function" must {
    "call SecureMessageFrontendConnector" in new TestCase {
      when(secureMessageFrontendConnector.conversationsPartial(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, "html content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      controller.conversations()(FakeRequest())

      verify(secureMessageFrontendConnector, times(1))
        .conversationsPartial(any())(any(), any())
    }

    "call SecureMessageFrontendConnector with filters" in new TestCase {
      val queryParams: Seq[(String, String)] = Seq(
        ("enrolmentKey", "HMRC-CUS-ORG"),
        ("enrolmentKey", "IR-SA"),
        ("enrolment", "HMRC-CUS-ORG~EORINumber~GB1234567890"),
        ("tag", "notificationType~cds-export")
      )

      when(secureMessageFrontendConnector.conversationsPartial(Matchers.eq(queryParams))(any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, "html content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      controller.conversations()(
        FakeRequest(
          "GET",
          "/secure-message-stub/messages?enrolmentKey=HMRC-CUS-ORG&enrolmentKey=IR-SA&enrolment=HMRC-CUS-ORG~EORINumber~GB1234567890&tag=notificationType~cds-export"
        ))

      verify(secureMessageFrontendConnector, times(1))
        .conversationsPartial(any())(any(), any())
    }

    "return 404 if reponse from secureMessageFrontendConnector is 404" in new TestCase {
      when(secureMessageFrontendConnector.conversationsPartial(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(404, "no content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val result = controller.conversations()(FakeRequest())

      status(result) mustBe Status.NOT_FOUND
    }

    "return SERVICE_UNAVAILABLE if reponse from secureMessageFrontendConnector is 503" in new TestCase {
      when(secureMessageFrontendConnector.conversationsPartial(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(500, "no content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val result = controller.conversations(FakeRequest())

      status(result) mustBe Status.SERVICE_UNAVAILABLE
    }

  }
  "message function" must {
    "call secure message frontend connector" in new TestCase {
      when(
        secureMessageFrontendConnector.messagePartial(eqTo("some-client-id"), eqTo("111"), eqTo(false))(any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, "html content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val _ = controller.message("some-client-id", "111", false)(FakeRequest())

      verify(secureMessageFrontendConnector, times(1))
        .messagePartial(eqTo("some-client-id"), eqTo("111"), eqTo(false))(any(), any())
    }

    "return 404 if reponse from secureMessageFrontendConnector is 404" in new TestCase {
      when(
        secureMessageFrontendConnector.messagePartial(eqTo("some-client-id"), eqTo("111"), eqTo(false))(any(), any()))
        .thenReturn(Future.successful(HttpResponse(404, "no content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val result = controller.message("some-client-id", "111", false)(FakeRequest())

      status(result) mustBe Status.NOT_FOUND
    }

    "return SERVICE_UNAVAILABLE if reponse from secureMessageFrontendConnector is 503" in new TestCase {
      when(
        secureMessageFrontendConnector.messagePartial(eqTo("some-client-id"), eqTo("111"), eqTo(false))(any(), any()))
        .thenReturn(Future.successful(HttpResponse(500, "no content")))

      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val result = controller.message("some-client-id", "111", false)(FakeRequest())

      status(result) mustBe Status.SERVICE_UNAVAILABLE
    }
  }

  "reply function" must {

    "return redirect if response from secureMessageFrontend is 200" in new TestCase {
      when(secureMessageFrontendConnector.messageReply(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, "")))
      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val result = controller.reply("client", "conversationId")(FakeRequest())
      status(result) mustBe 303
    }

    "return BadRequest if response from secureMessageFrontend is BAD_GATEWAY" in new TestCase {
      when(secureMessageFrontendConnector.messageReply(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(502, "")))
      when(error_page.apply(any())(any(), any())).thenReturn(Html("error content"))
      val controller = new ViewConversations(
        stubMessagesControllerComponents(),
        viewConversations,
        viewConversationMessages,
        error_page,
        secureMessageFrontendConnector)

      val result = controller.reply("client", "conversationId")(FakeRequest())
      status(result) mustBe Status.BAD_GATEWAY
      contentAsString(result) mustBe "error content"
    }

  }

  class TestCase {
    implicit val duration: Timeout = 5 seconds
    val secureMessageFrontendConnector: SecureMessageFrontendConnector = mock[SecureMessageFrontendConnector]
    val viewConversations: view_conversations = mock[view_conversations]
    val error_page: error_page = mock[error_page]
    val viewConversationMessages: view_conversation_messages = mock[view_conversation_messages]
  }

}
