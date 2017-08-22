package parser

import builder.CourseBuilder
import model.Course
import util.matching.CourseDetail

import scalaj.http.{Http, HttpOptions, HttpResponse}

class CoursePageParser(semester: String) {

  import CoursePageParser._

  val subjects: Seq[String] = getSubjects

  def getCoursesBySemester(): Seq[Course] = {
    subjects.par.flatMap(getCoursesBySubject).toList
  }

  private def getSubjects: Seq[String] = {
    val pageWithSubjectsSection = postRequestWrapper().body
    val subjectsSection = SubjectSectionPattern.findFirstIn(pageWithSubjectsSection).mkString
    val subjects = SingleSubjectPattern.findAllIn(subjectsSection)
    subjects.map {
      case SingleSubjectPattern(subject, _) => subject
    }.toSeq
  }

  private def getCoursesBySubject(subject: String): Seq[Course] = {
    val subjectPage = postRequestWrapper(subject).body

    val courseMatches = CoursePattern.findAllIn(subjectPage)

    val courseBuilder = CourseBuilder.getInstance()
    courseMatches.map { courseMatch =>
      val courseDetailMatches = CourseDetail.CourseDetailsPattern.findAllIn(courseMatch).toSeq
      courseBuilder.build(courseDetailMatches)
    }.toSeq
  }

  private def postRequestWrapper(subject: String = "", title: String = ""): HttpResponse[String] = {
    Http(RegistrarUrl)
      .postForm(Array(("term", semester), ("title", title), ("subject", subject)))
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(20000)).asString
  }
}

object CoursePageParser {
  val CoursePattern = "<tr class=''>(?s)(.*?)</tr>".r
  val RegistrarUrl = "http://webapps.lehigh.edu/registrar/schedule/?"
  val SubjectSectionPattern = "<select id='subject' name='subject'>(?s)(.*)</select>".r
  val SingleSubjectPattern = "<option value='([A-Z_]+)'>([ A-Za-z&\\',]+)</option>".r
}