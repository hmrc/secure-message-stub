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

package models

import models.QueryResponse.{ MaxConversationIdLength, MaxMessageLength }
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.apache.commons.codec.binary.Base64._
import java.util.UUID

case class QueryResponse(id: String, conversationId: String, message: Base64String) {
  assert(
    !conversationId.isEmpty && conversationId.length <= MaxConversationIdLength,
    s"conversationId size: ${conversationId.size} is invalid")
  assert(!message.isEmpty && (decodeBase64(message).toString.length <= MaxMessageLength), s"message size: ${message.size} is invalid")
}

object QueryResponse {
  val MaxConversationIdLength = 22
  val MaxMessageLength = 4000
  object UUIDFormatter extends Format[UUID] {
    def writes(uuid: UUID) = JsString(uuid.toString)

    def reads(json: JsValue) = json match {
      case JsString(s) =>
        try {
          JsSuccess(UUID.fromString(s))
        } catch {
          case e: IllegalArgumentException => JsError("error.expected.uuid")
        }
      case _ => JsError("error.expected.JsString")
    }
  }

  implicit val uuidFormat = UUIDFormatter

  implicit val queryResponseReads: Reads[QueryResponse] = (
    (JsPath \ "queryResponse" \ "id").read[String] and
      (JsPath \ "queryResponse" \ "conversationId").read[String](verifying[String](a =>
        !a.isEmpty && a.length <= MaxConversationIdLength)) and
      (JsPath \ "queryResponse" \ "message").read[String](verifying[String](a => {
        !a.isEmpty && isBase64(a) && (decodeBase64(a).toString.length <= MaxMessageLength)
      }))
  )(QueryResponse.apply _)
}
