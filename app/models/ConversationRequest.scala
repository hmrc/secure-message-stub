/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.{ Format, Json, OFormat, Reads, Writes }

final case class Alert(templateId: String, parameters: Option[Map[String, String]])
object Alert {
  implicit val alertFormat: OFormat[Alert] =
    Json.format[Alert]
}

final case class CustomerEnrolment(key: String, name: String, value: String)
object CustomerEnrolment {
  implicit val enrolmentReads: Reads[CustomerEnrolment] = {
    Json.reads[CustomerEnrolment]
  }

  def parse(enrolmentString: String): CustomerEnrolment = {
    val enrolment = enrolmentString.split('~')
    CustomerEnrolment(enrolment.head, enrolment(1), enrolment.last)
  }
}

final case class Tag(key: String, value: String)
object Tag {
  implicit val tagReads: Reads[Tag] = {
    Json.reads[Tag]
  }

  def parse(tagString: String): Tag = {
    val tag = tagString.split('~')
    Tag(tag.head, tag(1))
  }
}

final case class Enrolment(key: String, name: String, value: String)
object Enrolment {
  implicit val enrolmentFormat: OFormat[Enrolment] =
    Json.format[Enrolment]
}

final case class Identifier(name: String, value: String, enrolment: Option[String])
object Identifier {
  implicit val identifierFormat: Format[Identifier] = Json.format[Identifier]
}

final case class System(name: String, identifier: Identifier, parameters: Map[String, String], display: String)
object System {
  implicit val systemFormat: OFormat[System] =
    Json.format[System]
}

final case class Customer(enrolment: Enrolment, name: Option[String], email: Option[String])
object Customer {
  implicit val customerFormat: OFormat[Customer] =
    Json.format[Customer]
}

final case class Sender(system: System)
object Sender {
  implicit val senderFormat: OFormat[Sender] =
    Json.format[Sender]
}

final case class Recipient(customer: Customer)
object Recipient {
  implicit val recipientFormat: OFormat[Recipient] =
    Json.format[Recipient]
}

final case class ConversationRequest(
  sender: Sender,
  recipients: List[Recipient],
  alert: Alert,
  tags: Map[String, String],
  subject: String,
  message: String,
  language: Option[String])
object ConversationRequest {
  implicit val conversionRResultReads: Reads[ConversationRequest] = Json.reads[ConversationRequest]
  implicit val conversionRResultWrites: Writes[ConversationRequest] = Json.writes[ConversationRequest]
}
