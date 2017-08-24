package parser

import builder.CourseBuilder
import model.{Course, CourseSubject}
import util.CourseSitePostRequestUtil._

class CoursePageParser {

  import CoursePageParser._

  val courseSubjectParser = new CourseSubjectParser

  def getCoursesBySemester(semester: String): Seq[Course] = {
    val subjects = courseSubjectParser.getSubjectsBySemester(semester)
    subjects.par.flatMap { subject =>
      getCoursesBySubject(semester, subject)
    }.toList
  }

  private def getCoursesBySubject(semester: String, subject: CourseSubject): Seq[Course] = {
    val coursePageForGivenSubjectAndSemester = courseSitePostRequest(semester, subject.subject).body

    val courseMatches = CoursePattern.findAllIn(coursePageForGivenSubjectAndSemester)

    val courseBuilder = CourseBuilder.getInstance()
    courseMatches.map { courseMatch =>
      courseBuilder.build(new CourseDetailParser(courseMatch))
    }.toSeq
  }
}

object CoursePageParser {
  val CoursePattern = "<tr class=''>(?s)(.*?)</tr>".r
}