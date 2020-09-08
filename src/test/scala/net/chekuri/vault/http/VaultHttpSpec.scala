package net.chekuri.vault.http

import net.chekuri.vault.models.VaultModels.Http.{VaultHttpBodyModel, VaultHttpResponse}
import org.apache.logging.log4j.scala.Logging
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VaultHttpSpec extends AnyFlatSpec with Logging with BeforeAndAfterAll {
  val vault_http: VaultHttp = new VaultHttp
  "GetRequest" should "correctly make HTTP GET Request when proper URL is supplied" in {
    val url: String = "http://www.google.com"
    val response: VaultHttpResponse[String] = vault_http.GetRequest(url)
    logger.info("Received response:")
    logger.info(response.toJson)
  }

  "AsyncGetRequest" should "correctly make HTTP GET Request when proper URL is supplied" in {
    val url: String = "http://www.google.com"
    val f_response: Future[VaultHttpResponse[String]] = vault_http.AsyncGetRequest(url)
    f_response.map(x => {
      logger.info("Received response:")
      logger.info(x.toJson)
    })
  }

  "GetRequest" should "throw exception when incorrect URL is supplied" in {
    val url: String = "http://google.community"
    assertThrows[java.net.UnknownHostException] {
      vault_http.GetRequest(url)
    }
  }

  "PostRequest" should "correctly make HTTP POST Request when proper URL and no body is supplied" in {
    val url: String = "https://reqbin.com/echo/post/json"
    val response: VaultHttpResponse[String] = vault_http.PostRequest(url)
    logger.info("Received response:")
    logger.info(response.toJson)
  }

  "PostRequest" should "correctly make HTTP POST Request when proper URL and body is supplied" in {
    val url: String = "https://reqbin.com/echo/post/json"
    val body: VaultHttpBodyModel = VaultHttpBodyModel("test string payload", "text/plain")
    val response: VaultHttpResponse[String] = vault_http.PostRequest(url, Some(body))
    logger.info("Received response:")
    logger.info(response.toJson)
  }

  "AsyncPostRequest" should "correctly make HTTP POST Request" in {
    val url: String = "https://reqbin.com/echo/post/json"
    val f_response: Future[VaultHttpResponse[String]] = vault_http.AsyncPostRequest(url)
    f_response.map(x => {
      logger.info("Received response:")
      logger.info(x.toJson)
    })
  }
}
