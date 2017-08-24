package util

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

import scalaj.http.{Http, HttpOptions, HttpResponse}

object CourseSitePostRequestUtil {

  val RegistrarUrl = "http://webapps.lehigh.edu/registrar/schedule/"

  def courseSitePostRequest(semester: String, subject: String = "", title: String = ""): HttpResponse[String] = {

//    val browser = JsoupBrowser()
//    val params = s"?term=$semester&subject=$subject&title=$title"
//    browser.get(RegistrarUrl + params)
    Http(RegistrarUrl)
      .postForm(Array(("term", semester), ("title", title), ("subject", subject)))
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(20000)).asString
  }
}
