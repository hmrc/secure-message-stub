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

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents, Result }
import uk.gov.hmrc.http.{ HttpClient, HttpReads, HttpResponse }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.EnvironmentConfig

import scala.concurrent.ExecutionContext

class SwaggerController @Inject()(
  httpClient: HttpClient,
  environmentConfig: EnvironmentConfig,
  controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendController(controllerComponents) with I18nSupport {

  private val secureMessageBaseUrl = environmentConfig.baseUrl("secure-message")

  def getSwaggerAPISchema(fileName: String): Action[AnyContent] =
    Action.async { implicit request =>
      httpClient.GET(url = s"$secureMessageBaseUrl/assets/$fileName")
    }

  def createConversation(client: String, conversationId: String): Action[AnyContent] =
    Action.async { implicit request =>
      httpClient.PUT[Option[JsValue], Result](
        url = s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId",
        request.body.asJson,
        Seq.empty
      )
    }

  def createCaseworkerMessage(client: String, conversationId: String): Action[AnyContent] =
    Action.async { implicit request =>
      httpClient.POST[Option[JsValue], Result](
        url = s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId/caseworker-message",
        request.body.asJson,
        Seq.empty
      )
    }

  def createCustomerMessage(client: String, conversationId: String): Action[AnyContent] =
    Action.async { implicit request =>
      httpClient.POST[Option[JsValue], Result](
        url = s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId/customer-message",
        request.body.asJson,
        Seq.empty
      )
    }

  def getConversations(enrolmentKey: String, enrolmentName: String): Action[AnyContent] =
    Action.async { implicit request =>
      httpClient.GET(
        url = s"$secureMessageBaseUrl/secure-messaging/conversations/$enrolmentKey/$enrolmentName"
      )
    }

  def getConversation(
    client: String,
    conversationId: String,
    enrolmentKey: String,
    enrolmentName: String): Action[AnyContent] =
    Action.async { implicit request =>
      httpClient.GET(
        url =
          s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId/$enrolmentKey/$enrolmentName"
      )
    }

  def resultHttpReads: HttpReads[Result] = new HttpReads[Result] {
    override def read(method: String, url: String, response: HttpResponse): Result =
      Status(response.status)(response.body)
  }
  implicit val resultReader: HttpReads[Result] = resultHttpReads
}
