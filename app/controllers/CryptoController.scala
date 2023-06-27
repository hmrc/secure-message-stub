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

import forms.mappings.CryptoForm
import forms.mappings.CryptoForm.CryptoData
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Encryption

import scala.concurrent.Future

class CryptoController @Inject()(
  controllerComponents: MessagesControllerComponents,
  encryption: Encryption,
  view: views.html.crypto
) extends FrontendController(controllerComponents) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = Action { implicit request =>
    Ok(view("", Seq.empty))
  }

  def submitQuery(): Action[AnyContent] = Action.async { implicit request =>
    CryptoForm()
      .bindFromRequest()
      .fold[Future[Result]](
        hasErrors =>
          Future.successful(
            BadRequest(
              view("", hasErrors.errors.map(_.message))
            )
        ),
        form => decrypt(form)
      )
  }

  private[controllers] def decrypt(
    form: CryptoData
  )(implicit request: Request[_]): Future[Result] = form match {
    case CryptoData(Some(cryptoKey), Some(scrambledText)) => {
      val decipheredText = encryption.decrypt(cryptoKey, scrambledText)
      Future.successful(Ok(view(decipheredText, Seq.empty)))
    }
    case _ =>
      Future.successful(BadRequest("Input invalid"))
  }

}
