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

import connectors.SecureMessageConnector
import forms.mappings.ConversationForm
import forms.mappings.ConversationForm.ConversationData
import models.QueryLanguage.{ ENGLISH, WELSH }
import models.{ Alert, ConversationRequest, Customer, Enrolment, Identifier, QueryLanguage, Recipient, Sender, System }
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{ create, success_feedback }
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import config.FrontendAppConfig

class ConversationController @Inject()(
  controllerComponents: MessagesControllerComponents,
  secureMessage: SecureMessageConnector,
  success: success_feedback,
  view: create
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendController(controllerComponents) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = Action { implicit request =>
    val call = routes.ConversationController.submitQuery()
    Ok(view(ConversationForm(), call, Seq.empty))
  }

  def submitQuery() = Action.async { implicit request =>
    val call = routes.ConversationController.onPageLoad()

    ConversationForm().bindFromRequest.fold[Future[Result]](
      hasErrors =>
        Future.successful(
          BadRequest(
            view(ConversationForm(), call, hasErrors.errors.map(_.message))
          )
      ),
      form => saveConversation(form)(request)
    )
  }

  private[controllers] def saveConversation(
    form: ConversationData
  )(implicit request: Request[_]) = form match {
    case ConversationData(
        (Some(subject), Some(message), language),
        (Some(senderName), Some(conversationId), Some(identifierName), Some(identifierValue), Some(displayName)),
        (senderParameter1),
        (senderParameter2),
        (
          name,
          email,
          Some(enrolmentKey),
          Some(enrolmentName),
          Some(enrolmentValue)
        ),
        Some(alertTemplate),
        (alertParameter1),
        alertParameter2,
        tagsParameter1,
        tagsParameter2,
        tagsParameter3,
        tagsParameter4,
        tagsParameter5
        ) => {

      val lang = languageSelection(language.getOrElse(QueryLanguage.ENGLISH))

      val senderParameters = keyValuePair(senderParameter1) ++ keyValuePair(
        senderParameter2
      )
      val alertParameters = Some(
        keyValuePair(alertParameter1) ++ keyValuePair(alertParameter2)
      )
      val tagsParameters = keyValuePair(tagsParameter1) ++ keyValuePair(
        tagsParameter2
      ) ++ keyValuePair(tagsParameter3) ++ keyValuePair(tagsParameter4) ++ keyValuePair(
        tagsParameter5
      )

      val query = ConversationRequest(
        Sender(System(senderName, Identifier(identifierName, identifierValue, None), senderParameters, displayName)),
        List(
          Recipient(
            Customer(
              Enrolment(enrolmentKey, enrolmentName, enrolmentValue),
              name,
              email
            )
          )
        ),
        Alert(alertTemplate, alertParameters),
        tags = tagsParameters,
        subject,
        message,
        lang
      )

      secureMessage
        .create(senderName, conversationId, query)
        .map(_.status match {
          case CREATED => Ok(success("Query creation complete"))
          case _       => BadRequest(success("Query creation unsuccessfull"))
        })
        .recover {
          case _ => NotFound(success("Something went wrong"))
        }
    }
    case _ =>
      Future.successful(BadRequest("Input invalid"))
  }

  private def languageSelection(selection: String) = selection match {
    case "query.language-welsh" => Some(WELSH)
    case _                      => Some(ENGLISH)
  }

  private def keyValuePair(
    t: (Option[String], Option[String])
  ): Map[String, String] = t match {
    case (Some(k), Some(v)) => Map(k -> v)
    case _                  => Map.empty
  }
}
