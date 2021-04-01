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

import models.QueryMessageWrapper
import play.api.libs.json.{ JsError, JsSuccess, JsValue }
import play.api.mvc.{ Action, MessagesControllerComponents, MessagesRequest }
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.{ Logger, LoggerLike }
import uk.gov.hmrc.http.logging.Authorization
import javax.inject.Inject

class EISController @Inject()(controllerComponents: MessagesControllerComponents)
    extends FrontendController(controllerComponents) {
  private val log: LoggerLike = Logger(this.getClass)
  private val BearerToken = "Bearer AbCdEf123456"

  def queryResponse: Action[JsValue] = Action(parse.json) { request: MessagesRequest[JsValue] =>
    {
      import QueryMessageWrapper._

      if (!hasValidBearerToken(request))
        Unauthorized
      else
        request.body.validate[QueryMessageWrapper] match {
          case JsError(errors) =>
            log.warn(s"error parsing QueryMessageWrapper $errors")
            BadRequest
          case JsSuccess(value, _) if value.queryMessageRequest.requestDetail.conversationId.endsWith("err") =>
            log.warn(s"error processing the QueryMessageWrapper")
            InternalServerError
          case JsSuccess(value, _) =>
            log.warn(s"EIS processed QueryMessageWrapper $value")
            NoContent
        }
    }
  }

  def hasValidBearerToken(req: MessagesRequest[JsValue]): Boolean =
    HeaderCarrierConverter.fromHeadersAndSession(req.headers).authorization match {
      case Some(Authorization(BearerToken)) => true
      case Some(_) =>
        log.warn(s"the authorization token received was invalid")
        false
      case None =>
        log.warn("no authorization header set")
        false
    }
}
