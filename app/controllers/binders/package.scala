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

import models.{ CustomerEnrolment, Tag }
import play.api.mvc.QueryStringBindable

package object binders {
  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Nothing"))
  implicit def queryStringBindableCustomerEnrolment(
    implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[CustomerEnrolment] =
    new QueryStringBindable[CustomerEnrolment] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CustomerEnrolment]] =
        stringBinder.bind("enrolment", params) map {
          case Right(customerEnrolment) => Right(CustomerEnrolment.parse(customerEnrolment))
          case _                        => Left("Unable to bind a CustomerEnrolment")
        }

      override def unbind(key: String, customerEnrolment: CustomerEnrolment): String =
        customerEnrolment.key + "~" + customerEnrolment.name + "~" + customerEnrolment.value
    }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Nothing"))
  implicit def queryStringBindableTag(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Tag] =
    new QueryStringBindable[Tag] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Tag]] =
        stringBinder.bind("tag", params) map {
          case Right(tag) => Right(Tag.parse(tag))
          case _          => Left("Unable to bind a Tag")
        }

      override def unbind(key: String, tag: Tag): String =
        tag.key + "~" + tag.value
    }
}
