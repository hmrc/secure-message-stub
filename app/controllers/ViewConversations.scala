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
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{ view_conversation_messages, view_conversations }
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ViewConversations @Inject()(
  controllerComponents: MessagesControllerComponents,
  viewConversations: view_conversations,
  viewConversationMessages: view_conversation_messages,
  secureMessageFrontendConnector: SecureMessageFrontendConnector)(implicit ec: ExecutionContext)
    extends FrontendController(controllerComponents) with I18nSupport {

  def conversations: Action[AnyContent] = Action.async { implicit request =>
    secureMessageFrontendConnector.conversationsPartial
      .map { response =>
        (response.status, response.body) match {
          case (200, body) => Ok(viewConversations(HtmlFormat.raw(body)))
          case (404, _)    => NotFound
          case (_, _)      => ServiceUnavailable
        }
      }
      .recover {
        case _ => InternalServerError
      }
  }

  def message(id: String) = Action.async { implicit request =>
    secureMessageFrontendConnector
      .messagePartial(id)
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
