/*
 * Copyright 2020 HM Revenue & Customs
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

package utils

import com.google.inject.Inject
import play.api.{Configuration, Environment, Logger, Mode}

class EnvironmentConfig @Inject()(configuration: Configuration, environment: Environment) {

  val rootServices =   s"${environment.mode}.microservice.services"

  protected lazy val defaultProtocol: String =
    configuration
      .getOptional[String](s"$rootServices.protocol")
      .getOrElse("http")

  def baseUrl(serviceName: String) = {
    val protocol = getConfString(s"$serviceName.protocol", defaultProtocol)
    val host     = getConfString(s"$serviceName.host", throwConfigNotFoundError(s"$serviceName.host"))
    val port     = getConfInt(s"$serviceName.port", throwConfigNotFoundError(s"$serviceName.port"))

    s"$protocol://$host:$port"
  }

  def getConfInt(confKey: String, defInt: => Int) =
    configuration
      .getOptional[Int](s"$rootServices.$confKey")
      .getOrElse(defInt)

  def getConfString(confKey: String, defString: => String) =
    configuration
      .getOptional[String](s"$rootServices.$confKey")
      .getOrElse(defString)

  private def throwConfigNotFoundError(key: String) =
    throw new RuntimeException(s"Could not find config key '$key'")

}
