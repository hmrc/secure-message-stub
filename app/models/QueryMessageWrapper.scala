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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import org.apache.commons.codec.binary.Base64._

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

final case class QueryMessageWrapper(queryMessageRequest: QueryMessageRequest)
object QueryMessageWrapper {
  implicit val queryMessageWrapperReads: Reads[QueryMessageWrapper] =
    (JsPath \ "querymessageRequest")
      .read[QueryMessageRequest]
      .map(QueryMessageWrapper(_))
}

final case class QueryMessageRequest(requestCommon: RequestCommon, requestDetail: RequestDetail)
object QueryMessageRequest {
  implicit val queryMessageRequestReads: Reads[QueryMessageRequest] = Json.reads[QueryMessageRequest]
}

final case class RequestCommon(originatingSystem: String, receiptDate: Instant, acknowledgementReference: String)
object RequestCommon {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  implicit val instantReads: Reads[Instant] = Reads.instantReads(DateTimeFormatter.ISO_INSTANT)
  implicit val instantWrites: Writes[Instant] =
    Writes.temporalWrites[Instant, DateTimeFormatter](DateTimeFormatter.ISO_INSTANT)
  implicit val instantFormat: Format[Instant] = Format(instantReads, instantWrites)
  implicit val requestCommonReads: Reads[RequestCommon] = Json.reads[RequestCommon]
}

case class RequestDetail(id: String, conversationId: String, message: Base64String) {
  assert(
    conversationId.nonEmpty && conversationId.length <= RequestDetail.MaxConversationIdLength,
    s"conversationId size: ${conversationId.length} is invalid"
  )
  assert(
    message.nonEmpty && (decodeBase64(message).length <= RequestDetail.MaxMessageLength),
    s"message size: ${message.size} is invalid"
  )
}

object RequestDetail {
  val MaxConversationIdLength = 22
  val MaxMessageLength = 4000
  object UUIDFormatter extends Format[UUID] {
    def writes(uuid: UUID): JsString = JsString(uuid.toString)

    def reads(json: JsValue) = json match {
      case JsString(s) =>
        try
          JsSuccess(UUID.fromString(s))
        catch {
          case _: IllegalArgumentException => JsError("error.expected.uuid")
        }
      case _ => JsError("error.expected.JsString")
    }
  }

  implicit val uuidFormat: UUIDFormatter.type = UUIDFormatter

  implicit val requestDetailReads: Reads[RequestDetail] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "conversationId").read[String](
        verifying[String](a => a.nonEmpty && a.length <= MaxConversationIdLength)
      ) and
      (JsPath \ "message").read[String](verifying[String] { a =>
        a.nonEmpty && isBase64(a) && (decodeBase64(a).toString.length <= MaxMessageLength)
      })
  )(RequestDetail.apply)
}
