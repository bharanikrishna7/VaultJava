package net.chekuri.vault.http

import com.google.api.client.http._
import com.google.api.client.http.javanet.NetHttpTransport
import net.chekuri.vault.models.VaultModels.Http.{VaultHttpBodyModel, VaultHttpResponse}
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters.CollectionHasAsScala

class VaultHttp extends Logging {
  logger.debug("Initializing http request factory.")
  val _request_factory: HttpRequestFactory = new NetHttpTransport().createRequestFactory()
  logger.debug("Successfully initialized http request factory.")

  /**
    * Async method to make post request
    * and fetch associated response.
    *
    * @param uri     uri/url
    * @param body    http body model object. It will contain body and content type (optional)
    * @param headers custom headers (optional)
    * @return Future http response
    */
  def AsyncPostRequest(
    uri: String,
    body: Option[VaultHttpBodyModel] = None,
    headers: Map[String, String] = Map[String, String]()
  ): Future[VaultHttpResponse[String]] = {
    Future {
      PostRequest(uri, body, headers)
    }
  }

  /**
    * Method to make post request and
    * fetch associated response.
    *
    * @param uri     uri/url
    * @param body    http body model object. It will contain body and content type (optional)
    * @param headers custom headers (optional)
    * @return Future http response
    */
  def PostRequest(
    uri: String,
    body: Option[VaultHttpBodyModel] = None,
    headers: Map[String, String] = Map[String, String]()
  ): VaultHttpResponse[String] = {
    logger.debug(s"Preparing HTTP Post Request. URI: $uri")
    val request = PreparePostRequest(uri, body, headers)
    logger.debug(s"Executing HTTP Post Request.")
    ExecuteRequest(request)
  }

  /**
    * Method to prepare post request.
    *
    * @param uri     uri/url
    * @param body    body along with it's content type (optional)
    * @param headers custom headers (optional)
    * @return prepared http post request
    */
  private def PreparePostRequest(
    uri: String,
    body: Option[VaultHttpBodyModel] = None,
    headers: Map[String, String] = Map[String, String]()
  ): HttpRequest = {
    val url = new GenericUrl(uri)
    val content: ByteArrayContent = if (body.isDefined) {
      ByteArrayContent.fromString(body.get.content_type, body.get.body)
    } else {
      null
    }
    val request = _request_factory.buildPostRequest(url, content)
    val request_headers = AddHeaders(request.getHeaders, headers)
    request.setHeaders(request_headers)
  }

  /**
    * Method to add headers from a scala map to
    * supplied http headers.
    *
    * @param headers     original http headers
    * @param add_headers scala map where key is header key and value is header value
    * @return Original http headers with supplied values appended to it
    */
  def AddHeaders(headers: HttpHeaders, add_headers: Map[String, String]): HttpHeaders = {
    val result: HttpHeaders = headers
    logger.debug("Appending new headers to supplied http headers.")
    for ((k, v) <- add_headers) {
      result.set(k, v)
    }
    logger.debug("Successfully appended new headers supplied http headers.")
    result
  }

  def ExecuteRequest(request: HttpRequest): VaultHttpResponse[String] = {
    val response = request.execute()
    logger.debug(s"Finished executing HTTP Get Request.")
    val result = if (response.isSuccessStatusCode) {
      logger.debug("Found valid response.")
      ParseResponse(response)
    } else {
      logger.warn("Found non-success status code when looking at response status code.")
      logger.warn(s"Obtained Status Code : ${response.getStatusCode}")
      logger.warn(s"Obtained Status Message : ${response.getStatusMessage}")
      logger.warn("Throwing exception.")
      throw new Exception(
        "Encountered exception while making GET Request.",
        new Throwable(s"Received HTTP Status Code: ${response.getStatusCode}")
      )
    }
    DisconnectClient(response)
    result
  }

  /**
    * Method to parse HttpResponse to VaultHttpResponse object.
    *
    * @param response HttpResponse
    * @return VaultHttpResponse with response body as string
    */
  def ParseResponse(response: HttpResponse): VaultHttpResponse[String] = {
    val status_code: Int = response.getStatusCode
    val status_message: String = response.getStatusMessage
    val status_headers: Map[String, String] = HeadersToMap(response.getHeaders)
    val body: String = response.parseAsString()
    VaultHttpResponse[String](status_code, status_message, status_headers, body)
  }

  /**
    * Method to convert HttpHeaders to Scala Map.
    *
    * @param headers HttpHeaders
    * @return Scala Map where key is header key and value is associated header value
    */
  def HeadersToMap(headers: HttpHeaders): Map[String, String] = {
    logger.debug("Converting http headers to scala map.")
    var result: Map[String, String] = Map[String, String]()
    for (header <- headers.keySet().asScala) {
      result = result + (header -> headers.get(header).toString)
    }
    logger.debug("Successfully converted http headers to scala map.")
    result
  }

  /**
    * Method to disconnect client from server
    *
    * @param response Http Response to disconnect.
    */
  def DisconnectClient(response: HttpResponse): Unit = {
    logger.debug("Disconnecting the HTTP Request.")
    response.disconnect()
    logger.debug("Successfully disconnected the HTTP Request.")
  }

  /**
    * Async method to make get request and
    * fetch associated response.
    *
    * @param uri     uri/url
    * @param headers custom headers (optional)
    * @return Future http response
    */
  def AsyncGetRequest(
    uri: String,
    headers: Map[String, String] = Map[String, String]()
  ): Future[VaultHttpResponse[String]] = {
    Future {
      GetRequest(uri, headers)
    }
  }

  /**
    * Method to make get request and
    * fetch associated response.
    *
    * @param uri     uri/url
    * @param headers custom headers (optional)
    * @return http response
    */
  def GetRequest(
    uri: String,
    headers: Map[String, String] = Map[String, String]()
  ): VaultHttpResponse[String] = {
    logger.debug(s"Preparing HTTP Get Request. URI: $uri")
    val request = PrepareGetRequest(uri, headers)
    logger.debug(s"Executing HTTP Get Request.")
    ExecuteRequest(request)
  }

  /**
    * Method to prepare get request.
    *
    * @param uri     uri/url
    * @param headers custom headers (optional)
    * @return prepared http get request
    */
  private def PrepareGetRequest(
    uri: String,
    headers: Map[String, String] = Map[String, String]()
  ): HttpRequest = {
    val url = new GenericUrl(uri)
    val request = _request_factory.buildGetRequest(url)
    val request_headers = AddHeaders(request.getHeaders, headers)
    request.setHeaders(request_headers)
  }

}
