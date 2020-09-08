package net.chekuri.vault

import net.chekuri.vault.http.VaultHttp
import net.chekuri.vault.models.VaultModels.Http
import net.chekuri.vault.models.VaultModels.Http.VaultHttpBodyModel
import net.chekuri.vault.models.VaultModels.Request.AppRoleLoginModel
import net.chekuri.vault.models.VaultModels.Response.AppRoleAuthModel
import org.apache.logging.log4j.scala.Logging
import org.json4s._
import org.json4s.native.JsonMethods._

class VaultJava extends VaultHttp with Logging {
  implicit val formats: DefaultFormats.type = DefaultFormats

  /**
    * Method to authenticate and get associated vault token.
    *
    * @param url       vault server url.
    * @param role_id   role id.
    * @param secret_id associated secret id.
    * @return Returns AppRoleAuthModel object where we can find
    *         authentication properties [client_token, accessor, etc...]
    */
  def GetVaultToken(
    url: String,
    role_id: String,
    secret_id: String,
    namespace: Option[String] = None,
    path: String = "/v1/auth/approle/login"
  ): AppRoleAuthModel = {
    logger.info("Retrieving vault token.")
    logger.debug("Generating payload for request.")
    val payload: AppRoleLoginModel = AppRoleLoginModel(role_id, secret_id)
    logger.debug("Converting payload to HTTP Request Body and setting content-type header.")
    val request_body: VaultHttpBodyModel = VaultHttpBodyModel(payload.toJson, "application/json")
    logger.debug("Make HTTP Request and get response.")
    var uri = s"$url$path"
    if (namespace.isDefined) {
      uri += s"?namespace=${namespace.get}"
    }

    logger.debug(s"Making POST Request to URI: $uri")
    val make_request: Http.VaultHttpResponse[String] = PostRequest(uri, Some(request_body))
    logger.trace("Received Response:")
    logger.trace(make_request.body)
    logger.debug("Extract the response body.")
    val response_body = make_request.body
    logger.debug("Parse response body to App Role Auth Model.")
    val parsed = parse(response_body).extract[AppRoleAuthModel]
    logger.info("Successfully retrieved vault token.")
    parsed
  }
}
