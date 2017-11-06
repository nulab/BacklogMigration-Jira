package com.nulabinc.jira.client

import java.nio.charset.Charset

import org.apache.commons.codec.binary.Base64
import org.apache.http.{HttpHeaders, HttpStatus}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import spray.json.{JsArray, JsonParser}


sealed abstract class HttpClientError(val message: String) {
  override def toString: String = message
}
case object AuthenticateFailedError extends HttpClientError("Bad credential")
case class ApiNotFoundError(url: String) extends HttpClientError(url)
case class BadRequestError(error: String) extends HttpClientError(error)
case class UndefinedError(statusCode: Int) extends HttpClientError(s"Unknown status code: $statusCode")

class HttpClient(url: String, username: String, password: String) {

  val auth = username + ":" + password
  val encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")))
  val authHeader = "Basic " + new String(encodedAuth)

  def get(path: String): Either[HttpClientError, String] = {
    using(HttpClientBuilder.create().build()) { http =>
      val request = new HttpGet(url + "/rest/api/2" + path)
      request.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
      val httpResponse = http.execute(request)
      httpResponse.getStatusLine.getStatusCode match {
        case HttpStatus.SC_OK =>
          using(httpResponse.getEntity.getContent) { inputStream =>
            val body = io.Source.fromInputStream(inputStream)("UTF-8").getLines.mkString
            Right(body)
          }
        case HttpStatus.SC_BAD_REQUEST => {
          using(httpResponse.getEntity.getContent) { inputStream =>
            val body = io.Source.fromInputStream(inputStream).getLines.mkString
            val errors = JsonParser(body).asJsObject.getFields("errorMessages") match {
              case Seq(JsArray(e)) => e.mkString(" ")
              case _               => "Bad Request"
            }
            Left(BadRequestError(errors))
          }
        }
        case HttpStatus.SC_NOT_FOUND    => Left(ApiNotFoundError(request.getURI.toString))
        case HttpStatus.SC_UNAUTHORIZED => Left(AuthenticateFailedError)
        case _                          => Left(UndefinedError(httpResponse.getStatusLine.getStatusCode))
      }
    }
  }

  private [this] def using[A <: {def close()}, B](resource: A)(func: A => B): B = {
    try {
      func(resource)
    } finally {
      if(resource != null) resource.close()
    }
  }
}
