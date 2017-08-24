package util

import scalaj.http.{Http, HttpOptions, HttpResponse}

object CourseSitePostRequestUtil {

  val RegistrarUrl = "http://webapps.lehigh.edu/registrar/schedule/"

  def courseSitePostRequest(semester: String, subject: String = "", title: String = ""): HttpResponse[String] = {

    Http(RegistrarUrl)
      .postForm(Array(("term", semester), ("title", title), ("subject", subject)))
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .options(Seq(HttpOptions.readTimeout(7000), HttpOptions.connTimeout(10000))).asString
  }
}
