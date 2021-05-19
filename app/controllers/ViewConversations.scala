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

import connectors.SecureMessageFrontendConnector
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{ error_page, view_conversation_messages, view_conversations }

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ViewConversations @Inject()(
  controllerComponents: MessagesControllerComponents,
  viewConversations: view_conversations,
  viewConversationMessages: view_conversation_messages,
  errorPage: error_page,
  secureMessageFrontendConnector: SecureMessageFrontendConnector)(implicit ec: ExecutionContext)
    extends FrontendController(controllerComponents) with I18nSupport {

  def conversations: Action[AnyContent] = Action.async { implicit request =>
    {
      val queryParams = queryStringToParams(request.queryString)

      for {
        response <- secureMessageFrontendConnector.conversationsPartial(queryParams)
        messageCount <- secureMessageFrontendConnector.messageCount(queryParams)
      } yield (response.status, response.body) match {
        case (OK, body)     => Ok(viewConversations(messageCount, HtmlFormat.raw(body)))
        case (NOT_FOUND, _) => NotFound
        case (_, _)         => ServiceUnavailable
      }
    }
  }

  def message(client: String, conversationId: String, showReplyForm: Boolean) = Action.async { implicit request =>
    secureMessageFrontendConnector
      .messagePartial(client, conversationId, showReplyForm)
      .map { response =>
        (response.status, response.body) match {
          case (OK, body)          => Ok(viewConversationMessages(HtmlFormat.raw(body)))
          case (BAD_REQUEST, body) => BadRequest(viewConversationMessages(HtmlFormat.raw(body)))
          case (NOT_FOUND, _)      => NotFound
          case (_, _)              => ServiceUnavailable
        }
      }
      .recover {
        case _ => InternalServerError
      }
  }

  def reply(client: String, conversationId: String) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    secureMessageFrontendConnector.messageReply(client, conversationId, request).map { response =>
      (response.status, response.body) match {
        case (OK, body)          => Redirect(body)
        case (BAD_REQUEST, body) => BadRequest(viewConversationMessages(HtmlFormat.raw(body)))
        case (NOT_FOUND, _)      => NotFound
        case (BAD_GATEWAY, _)    => BadGateway(errorPage("Sorry, there is a problem with the service"))
        case (_, _)              => ServiceUnavailable
      }
    }
  }

  def result(client: String, conversationId: String) = Action.async { implicit request =>
    secureMessageFrontendConnector
      .resultPartial(client, conversationId)
      .map { response =>
        (response.status, response.body) match {
          case (OK, body)     => Ok(viewConversationMessages(HtmlFormat.raw(body)))
          case (NOT_FOUND, _) => NotFound
          case (_, _)         => ServiceUnavailable
        }
      }
      .recover {
        case _ => InternalServerError
      }
  }

  def viewLetterOrConversation(id: String) = Action.async { implicit request =>
    secureMessageFrontendConnector
      .letterOrConversationPartial(id)
      .map { response =>
        (response.status, response.body) match {
          case (200, body) => Ok(viewConversationMessages(HtmlFormat.raw(body)))
          case (404, _)    => NotFound
          case (_, _)      => ServiceUnavailable
        }
      }
      .recover {
        case _ => InternalServerError
      }
  }
}
