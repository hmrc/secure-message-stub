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

import config.FrontendAppConfig
import connectors.SecureMessageFrontendConnector
import javax.inject.Inject
import play.api.Logger.logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.{ Html, HtmlFormat }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.play.partials.HtmlPartial
import views.html.{ error_page, view_conversation_messages, view_conversations }

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NonFatal

class ViewConversations @Inject()(
  controllerComponents: MessagesControllerComponents,
  viewConversations: view_conversations,
  viewConversationMessages: view_conversation_messages,
  errorPage: error_page,
  secureMessageFrontendConnector: SecureMessageFrontendConnector)(
  implicit ec: ExecutionContext,
  appConfig: FrontendAppConfig)
    extends FrontendController(controllerComponents) with I18nSupport {

  def conversations: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    {
      val queryParams = queryStringToParams(request.queryString)
      for {
        partial      <- secureMessageFrontendConnector.conversationsPartial(queryParams)
        messageCount <- secureMessageFrontendConnector.messageCount(queryParams)
      } yield
        partial match {
          case HtmlPartial.Success(_, content) => Ok(viewConversations(messageCount, content))
          case HtmlPartial.Failure(Some(NOT_FOUND), body) =>
            logger.error(s"[ViewConversations][conversations] - status code:NOT_FOUND, body:$body")
            NotFound(body)
          case HtmlPartial.Failure(Some(status), body) =>
            logger.error(s"[ViewConversations][conversations] - status code:$status, body:$body")
            ServiceUnavailable(body)
          case HtmlPartial.Failure(None, body) =>
            logger.error(s"[ViewConversations][conversations] - body:$body")
            ServiceUnavailable(body)
        }
    }
  }

  def message(client: String, conversationId: String, showReplyForm: Boolean) = Action.async {
    implicit request: MessagesRequest[AnyContent] =>
      secureMessageFrontendConnector
        .messagePartial(client, conversationId, showReplyForm)
        .map {
          case HtmlPartial.Success(_, content) => Ok(viewConversationMessages(content))
          case HtmlPartial.Failure(Some(BAD_REQUEST), body) =>
            logger.error(s"[ViewConversations][message] - status code:BAD_REQUEST, body:$body")
            BadRequest(body)
          case HtmlPartial.Failure(Some(NOT_FOUND), body) =>
            logger.error(s"[ViewConversations][message] - status code:NOT_FOUND, body:$body")
            NotFound(body)
          case HtmlPartial.Failure(_, body) =>
            logger.error(s"[ViewConversations][message] - body:$body")
            ServiceUnavailable(body)
        }
        .recover {
          case NonFatal(err) => {
            logger.error(s"[ViewConversations][result] - InternalServerError", err)
            InternalServerError
          }
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

  def result(client: String, conversationId: String) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    secureMessageFrontendConnector
      .resultPartial(client, conversationId)
      .map {
        case HtmlPartial.Success(_, content) => Ok(viewConversationMessages(content))
        case HtmlPartial.Failure(Some(NOT_FOUND), body) =>
          logger.error(s"[ViewConversations][result] - status code:NOT_FOUND, body:$body")
          NotFound(body)
        case HtmlPartial.Failure(Some(status), body) =>
          logger.error(s"[ViewConversations][result] - status code:$status, body:$body")
          ServiceUnavailable(body)
        case HtmlPartial.Failure(_, body) =>
          logger.error(s"[ViewConversations][result] - body:$body")
          ServiceUnavailable(body)
      }
      .recover {
        case NonFatal(err) => {
          logger.error(s"[ViewConversations][result] - InternalServerError", err)
          InternalServerError
        }
      }
  }

  def viewLetterOrConversation(id: String) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    secureMessageFrontendConnector
      .letterOrConversationPartial(id)
      .map {
        case HtmlPartial.Success(_, content) => Ok(viewConversationMessages(content))
        case HtmlPartial.Failure(Some(NOT_FOUND), body) =>
          logger.error(s"[ViewConversations][viewLetterOrConversation] - status code:NOT_FOUND, body:$body")
          NotFound(body)
        case HtmlPartial.Failure(Some(status), body) =>
          logger.error(s"[ViewConversations][viewLetterOrConversation] - status code:$status, body:$body")
          ServiceUnavailable(body)
        case HtmlPartial.Failure(_, body) =>
          logger.error(s"[ViewConversations][viewLetterOrConversation] - body:$body")
          ServiceUnavailable(body)
      }
      .recover {
        case NonFatal(err) => {
          logger.error(s"[ViewConversations][result] - InternalServerError", err)
          InternalServerError
        }
      }
  }
}
