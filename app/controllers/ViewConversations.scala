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
import views.html.{ view_conversation_messages, view_conversations }

import scala.concurrent.ExecutionContext

class ViewConversations @Inject()(
  controllerComponents: MessagesControllerComponents,
  viewConversations: view_conversations,
  viewConversationMessages: view_conversation_messages,
  secureMessageFrontendConnector: SecureMessageFrontendConnector)(implicit ec: ExecutionContext)
    extends FrontendController(controllerComponents) with I18nSupport {

  def conversations: Action[AnyContent] = Action.async { implicit request =>
    {
      val queryParams = queryStringToParams(request.queryString)
      secureMessageFrontendConnector
        .conversationsPartial(queryParams)
        .map { response =>
          (response.status, response.body) match {
            case (OK, body)     => Ok(viewConversations(HtmlFormat.raw(body)))
            case (NOT_FOUND, _) => NotFound
            case (_, _)         => ServiceUnavailable
          }
        }
        .recover {
          case _ => InternalServerError
        }
    }
  }

  def message(client: String, conversationId: String, showReplyForm: Boolean) = Action.async { implicit request =>
    secureMessageFrontendConnector
      .messagePartial(client, conversationId, showReplyForm)
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

  def reply(client: String, conversationId: String) = Action.async { implicit request =>
    secureMessageFrontendConnector.messageReply(client, conversationId, request).map { response =>
      (response.status, response.body) match {
        case (OK, body)     => Redirect(body)
        case (BAD_REQUEST, body)     => BadRequest(viewConversationMessages(HtmlFormat.raw(body)))
        case (NOT_FOUND, _) => NotFound
        case (_, _)         => ServiceUnavailable
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
}
