package com.nulabinc.jira.client

import java.io.{File, FileOutputStream}
import java.nio.channels.{Channels, ReadableByteChannel}
import java.nio.charset.Charset

import org.apache.commons.codec.binary.Base64
import org.apache.http._
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost, HttpUriRequest}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{
  BasicCredentialsProvider,
  CloseableHttpClient,
  HttpClientBuilder,
  HttpClients
}
import org.slf4j.{Logger, LoggerFactory}
import spray.json.{JsArray, JsonParser}

import scala.io.Source

sealed abstract class HttpClientError(val message: String) {
  override def toString: String = message
}
case object AuthenticateFailedError              extends HttpClientError("Bad credential")
case class ApiNotFoundError(url: String)         extends HttpClientError(url)
case class BadRequestError(error: String)        extends HttpClientError(error)
case class GetContentError(throwable: Throwable) extends HttpClientError(throwable.getMessage)
case class ThrowableError(throwable: Throwable)  extends HttpClientError(throwable.getMessage)
case class UndefinedError(statusCode: Int)
    extends HttpClientError(s"Unknown status code: $statusCode")

sealed trait DownloadResult
case object DownloadSuccess extends DownloadResult
case object DownloadFailure extends DownloadResult

class HttpClient(url: String, username: String, apiKey: String) {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  private val auth: String = username + ":" + apiKey
  private val encodedAuth: Array[Byte] =
    Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")))
  private val authHeader: String = "Basic " + new String(encodedAuth)

  private val optProxyConfig: Option[HttpHost] =
    (
      Option(System.getProperty("https.proxyHost")),
      Option(System.getProperty("https.proxyPort"))
    ) match {
      case (Some(host), Some(port)) => Some(new HttpHost(host, port.toInt))
      case _                        => None
    }

  private val optCredentialsProvider: Option[BasicCredentialsProvider] =
    (
      optProxyConfig,
      Option(System.getProperty("https.proxyUser")),
      Option(System.getProperty("https.proxyPassword"))
    ) match {
      case (Some(proxy), Some(user), Some(pass)) =>
        val credsProvider = new BasicCredentialsProvider()
        credsProvider.setCredentials(
          new AuthScope(proxy),
          new UsernamePasswordCredentials(user, pass)
        )
        Some(credsProvider)
      case _ =>
        None
    }

  def get(path: String): Either[HttpClientError, String] = {

    val closableHttpClient = createHttpClient()
    val httpRequest        = createHttpGetRequest(url + "/rest/api/2" + path)

    try {
      val closableHttpResponse = httpExecute(closableHttpClient, httpRequest)

      closableHttpResponse.getStatusLine.getStatusCode match {
        case HttpStatus.SC_OK =>
          try {
            val content = getContent(closableHttpResponse.getEntity)
            Right(content)
          } catch {
            case e: Throwable => Left(GetContentError(e))
          } finally {
            closableHttpResponse.close()
          }
        case HttpStatus.SC_BAD_REQUEST =>
          try {
            val content = getContent(closableHttpResponse.getEntity)
            JsonParser(content).asJsObject.getFields("errorMessages") match {
              case Seq(JsArray(e)) => Left(BadRequestError(e.mkString(" ")))
              case _ => Left(BadRequestError("Bad Request"))
            }
          } catch {
            case e: Throwable => Left(GetContentError(e))
          } finally {
            closableHttpResponse.close()
          }
        case HttpStatus.SC_NOT_FOUND =>
          Left(ApiNotFoundError(httpRequest.getURI.toString))
        case HttpStatus.SC_UNAUTHORIZED => Left(AuthenticateFailedError)
        case HttpStatus.SC_FORBIDDEN => Left(AuthenticateFailedError)
      }
    } finally {
      closableHttpClient.close()
    }
  }

  def post(path: String, body: String): Either[HttpClientError, String] = {

    val closableHttpClient = createHttpClient()
    val httpRequest        = createHttpPostRequest(url + "/rest/api/2" + path, body)

    try {
      val closableHttpResponse = httpExecute(closableHttpClient, httpRequest)

      closableHttpResponse.getStatusLine.getStatusCode match {
        case HttpStatus.SC_OK =>
          try {
            val content = getContent(closableHttpResponse.getEntity)
            Right(content)
          } catch {
            case e: Throwable => Left(GetContentError(e))
          } finally {
            closableHttpResponse.close()
          }
        case HttpStatus.SC_BAD_REQUEST =>
          try {
            val content = getContent(closableHttpResponse.getEntity)
            JsonParser(content).asJsObject.getFields("errorMessages") match {
              case Seq(JsArray(e)) => Left(BadRequestError(e.mkString(" ")))
              case _               => Left(BadRequestError("Bad Request"))
            }
          } catch {
            case e: Throwable => Left(GetContentError(e))
          } finally {
            closableHttpResponse.close()
          }
        case HttpStatus.SC_NOT_FOUND =>
          Left(ApiNotFoundError(httpRequest.getURI.toString))
        case HttpStatus.SC_UNAUTHORIZED => Left(AuthenticateFailedError)
        case HttpStatus.SC_FORBIDDEN    => Left(AuthenticateFailedError)
        case statusCode                 => Left(UndefinedError(statusCode))
      }
    } catch {
      case e: Throwable => Left(ThrowableError(e))
    } finally {
      closableHttpClient.close()
    }
  }

  def download(url: String, destinationFilePath: String): DownloadResult = {

    def getChannel(entity: HttpEntity): ReadableByteChannel =
      Channels.newChannel(entity.getContent)

    def createOutputStream(channel: ReadableByteChannel): Long = {
      val outputStream = new FileOutputStream(new File(destinationFilePath))
      outputStream.getChannel.transferFrom(channel, 0, java.lang.Long.MAX_VALUE)
    }

    val closableHttpClient = createHttpClient()
    val httpRequest        = createHttpGetRequest(url)

    try {
      val closableHttpResponse = httpExecute(closableHttpClient, httpRequest)

      closableHttpResponse.getStatusLine.getStatusCode match {
        case HttpStatus.SC_OK =>
          try {
            val channel = getChannel(closableHttpResponse.getEntity)
            createOutputStream(channel)
            DownloadSuccess
          } catch {
            case _: Throwable => DownloadFailure
          } finally {
            closableHttpResponse.close()
          }
        case _ =>
          closableHttpResponse.close()
          DownloadFailure
      }
    } catch {
      case _: Throwable => DownloadFailure
    } finally {
      closableHttpClient.close()
    }
  }

  private def createHttpClient(): CloseableHttpClient =
    (for {
      proxyConfig        <- optProxyConfig
      credentialProvider <- optCredentialsProvider
    } yield {
      val config = RequestConfig.custom().setProxy(proxyConfig).build()

      HttpClients
        .custom()
        .setDefaultCredentialsProvider(credentialProvider)
        .setDefaultRequestConfig(config)
        .build()
    }).getOrElse(HttpClientBuilder.create().build())

  private def createHttpGetRequest(path: String): HttpGet = {
    logger.info(s"Create HTTP request method: GET and uri: $path")
    new HttpGet(path)
  }

  private def createHttpPostRequest(path: String, body: String): HttpPost = {
    logger.info(s"Create HTTP request method: POST and uri: $path")
    val post = new HttpPost(path)
    post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
    post.setEntity(new StringEntity(body, "UTF-8"))
    post
  }

  private def httpExecute(
      client: CloseableHttpClient,
      request: HttpUriRequest
  ): CloseableHttpResponse = {
    request.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
    client.execute(request)
  }

  private def getContent(entity: HttpEntity): String = {
    val content = entity.getContent
    Source.fromInputStream(content)("UTF-8").getLines.mkString
  }

}
