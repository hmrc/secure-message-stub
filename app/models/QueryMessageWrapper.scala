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

import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.apache.commons.codec.binary.Base64._
import java.util.UUID

import play.api.libs.json._

final case class QueryMessageWrapper(queryMessageRequest: QueryMessageRequest)
object QueryMessageWrapper {
  implicit val queryMessageWrapperReads: Reads[QueryMessageWrapper] = (
    (JsPath \ "querymessageRequest").read[QueryMessageRequest]
  ).map(QueryMessageWrapper(_))
}

final case class QueryMessageRequest(requestCommon: RequestCommon, requestDetail: RequestDetail)
object QueryMessageRequest {
  implicit val queryMessageRequestReads: Reads[QueryMessageRequest] = Json.reads[QueryMessageRequest]
}

final case class RequestCommon(
  originatingSystem: String,
  receiptDate: org.joda.time.DateTime,
  acknowledgementReference: String)
object RequestCommon {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
  implicit val JodaDateReads: Reads[org.joda.time.DateTime] = JodaReads.jodaDateReads(dateFormat)
  implicit val JodaDateWrites: Writes[org.joda.time.DateTime] = JodaWrites.jodaDateWrites(dateFormat)
  implicit val JodaDateTimeFormat: Format[org.joda.time.DateTime] = Format(JodaDateReads, JodaDateWrites)
  implicit val requestCommonReads: Reads[RequestCommon] = Json.reads[RequestCommon]
}

case class RequestDetail(id: String, conversationId: String, message: Base64String) {
  assert(
    !conversationId.isEmpty && conversationId.length <= RequestDetail.MaxConversationIdLength,
    s"conversationId size: ${conversationId.size} is invalid")
  assert(
    !message.isEmpty && (decodeBase64(message).toString.length <= RequestDetail.MaxMessageLength),
    s"message size: ${message.size} is invalid")
}

object RequestDetail {
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

  implicit val requestDetailReads: Reads[RequestDetail] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "conversationId").read[String](verifying[String](a =>
        !a.isEmpty && a.length <= MaxConversationIdLength)) and
      (JsPath \ "message").read[String](verifying[String](a => {
        !a.isEmpty && isBase64(a) && (decodeBase64(a).toString.length <= MaxMessageLength)
      }))
  )(RequestDetail.apply _)
}
