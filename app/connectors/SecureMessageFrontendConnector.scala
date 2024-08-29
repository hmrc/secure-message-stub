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

package connectors

import com.google.inject.Inject
import models.Count
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.DefaultBodyWritables.writeableOf_urlEncodedForm
import play.api.mvc.{ AnyContent, MessagesRequest, RequestHeader }
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse, UpstreamErrorResponse }
import uk.gov.hmrc.play.partials.{ HeaderCarrierForPartialsConverter, HtmlPartial }
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import utils.EnvironmentConfig

import java.net.{ URI, URLEncoder }
import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageFrontendConnector @Inject() (
  httpClient: HttpClientV2,
  envConfig: EnvironmentConfig,
  headerCarrierForPartialsConverter: HeaderCarrierForPartialsConverter
) {

  val secureMessageFrontendBaseUrl = envConfig.baseUrl("secure-message-frontend")
  val logger: Logger = Logger(this.getClass())

  def conversationsPartial(
    queryParams: Seq[(String, String)] = Seq.empty
  )(implicit ec: ExecutionContext, request: RequestHeader): Future[HtmlPartial] = {
    implicit val hc: HeaderCarrier = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)
    httpClient
      .get(
        URI(
          s"$secureMessageFrontendBaseUrl/secure-message-frontend/secure-message-stub/messages${makeQueryString(queryParams)}"
        ).toURL
      )
      .execute[HtmlPartial]
  }
  private def makeQueryString(queryParams: Seq[(String, String)]) = {
    def urlEncode(u: String): String = URLEncoder.encode(u, "UTF-8")
    val paramPairs = queryParams.map { case (k, v) => s"$k=${urlEncode(v)}" }
    if (paramPairs.isEmpty) "" else paramPairs.mkString("?", "&", "")
  }

  def messagePartial(client: String, conversationId: String, showReplyForm: Boolean)(implicit
    ec: ExecutionContext,
    request: RequestHeader
  ): Future[HtmlPartial] = {
    implicit val hc = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)
    httpClient
      .get(
        URI(
          s"$secureMessageFrontendBaseUrl/secure-message-frontend/secure-message-stub/conversation/$client/$conversationId?showReplyForm=$showReplyForm"
        ).toURL
      )
      .execute[HtmlPartial]
  }

  def letterOrConversationPartial(
    id: String
  )(implicit ec: ExecutionContext, request: RequestHeader): Future[HtmlPartial] = {
    implicit val hc = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)
    httpClient
      .get(URI(s"$secureMessageFrontendBaseUrl/secure-message-frontend/secure-message-stub/messages/$id").toURL)
      .execute[HtmlPartial]
  }

  def messageReply(client: String, conversationId: String, request: MessagesRequest[AnyContent])(implicit
    ec: ExecutionContext
  ): Future[HttpResponse] = {
    implicit val hc = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)
    httpClient
      .post(
        URI(
          s"$secureMessageFrontendBaseUrl/secure-message-frontend/secure-message-stub/conversation/$client/$conversationId"
        ).toURL
      )
      .withBody(request.body.asFormUrlEncoded.getOrElse(Map.empty))
      .execute[HttpResponse]
  }

  def resultPartial(client: String, conversationId: String)(implicit
    ec: ExecutionContext,
    request: RequestHeader
  ): Future[HtmlPartial] = {
    implicit val hc = headerCarrierForPartialsConverter.fromRequestWithEncryptedCookie(request)
    httpClient
      .get(
        URI(
          s"$secureMessageFrontendBaseUrl/secure-message-frontend/secure-message-stub/conversation/$client/$conversationId/result"
        ).toURL
      )
      .execute[HtmlPartial]
  }

  def messageCount(
    queryParams: Seq[(String, String)] = Seq.empty
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Count] =
    httpClient
      .get(
        URI(
          s"$secureMessageFrontendBaseUrl/secure-message-frontend/messages/count${makeQueryString(queryParams)}"
        ).toURL
      )
      .execute[Count]
      .recoverWith { case exc: UpstreamErrorResponse =>
        logger.error(
          s"Received a ${exc.statusCode} response secure-messaging-frontend whilst retrieving message count: ${exc.message}"
        )
        Future.failed(exc)
      }
}
