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

package utils

import uk.gov.hmrc.crypto.{ AesCrypto, Crypted, PlainText }

import scala.util.control.NonFatal

class Encryption {

  def encrypt(cryptoKey: String, text: String): String = {
    def crypto: AesCrypto = aesCrypto(cryptoKey)
    crypto.encrypt(PlainText(text)).value
  }

  def decrypt(cryptoKey: String, scrambledText: String): String = {
    def crypto: AesCrypto = aesCrypto(cryptoKey)
    crypto.decrypt(Crypted(scrambledText)).value
  }

  private def aesCrypto(key: String): AesCrypto =
    try {
      val crypto = new AesCrypto {
        override val encryptionKey = key
      }
      crypto.decrypt(crypto.encrypt(PlainText("assert-valid-key")))
      crypto
    } catch {
      case NonFatal(ex) =>
        throw new SecurityException("Invalid encryption key", ex)
    }
}

object Encryption {
  val KEY = "enrolment"
}
