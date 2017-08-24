package parser

import model.CourseSubject
import util.CourseSitePostRequestUtil.courseSitePostRequest
class CourseSubjectParser {

  import CourseSubjectParser._

  def getSubjectsBySemester(semester: String): Seq[CourseSubject] = {
    val pageWithSubjectsSection = courseSitePostRequest(semester).body
    val subjectsSection = SubjectSectionPattern.findFirstIn(pageWithSubjectsSection).mkString
    val subjects = SingleSubjectPattern.findAllIn(subjectsSection)
    subjects.map {
      case SingleSubjectPattern(subject, _) => CourseSubject(subject)
    }.toSeq
  }
}

object CourseSubjectParser {
  val SubjectSectionPattern = "<select id='subject' name='subject'>(?s)(.*)</select>".r
  val SingleSubjectPattern = "<option value='([A-Z_]+)'>([ A-Za-z&\\',]+)</option>".r
}
