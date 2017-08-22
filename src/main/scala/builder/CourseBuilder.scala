package builder

import java.util.Calendar

import model._
import util.matching._

class CourseBuilder() {

  import CourseBuilder._

  def build(courseDetailMatches: Seq[String]): Course = {
    val courseDetailToCourseDetailStringMap: Map[CourseDetail, String] =
      courseDetailMatches.zipWithIndex.flatMap {
        case (courseDetailMatchedString, indexOfMatchedString) =>
          CourseDetail.IndexToCourseDetailMap.get(indexOfMatchedString).map { courseDetail =>
            Predef.ArrowAssoc(courseDetail) -> courseDetailMatchedString
          }
      }.toMap

    createCourse(courseDetailToCourseDetailStringMap)
  }

  // Given the array of indices to check for course details needed and the array of course details,
  // will create an instance of model.Course.
  private def createCourse(courseDetailToMatchedStringMap: Map[CourseDetail, String]): Course = {

    val courseRegistrationNumber = getCourseRegistrationNumber(courseDetailToMatchedStringMap)

    val courseNumber = getCourseNumber(courseDetailToMatchedStringMap)

    val courseTitle = getCourseTitle(courseDetailToMatchedStringMap)

    val credits = getCourseCredits(courseDetailToMatchedStringMap)

    val instructor = getCourseInstructor(courseDetailToMatchedStringMap)

    // Replace the parentheses in the string to make it easier to match
    var replacedTimeLine: String = courseDetailToMatchedStringMap(CourseTimeDetail).replaceAll("[()]", "_")
    // if there is a </p> tag in the time line, we cut it off at at the end of the </p> tag to prevent regexing the td that goes to the subsequent line
    if (replacedTimeLine.indexOf("</p>") != -1) replacedTimeLine = replacedTimeLine.substring(0, replacedTimeLine.indexOf("</p>") + 4)
    val (startTimeOpt, endTimeOpt, daysOpt) = replacedTimeLine match {
      case CourseTimeDetail.TimeListedPattern(s, e, d) => (
        Some(stringToCalendarObject(s)),
        Some(stringToCalendarObject(e)),
        Some(d.toCharArray.flatMap(Day.fromChar))
      )
      case CourseTimeDetail.TimeUnlistedPattern(_) => (None, None, None)
    }
    val courseTime = getCourseTime(startTimeOpt, endTimeOpt, daysOpt)

    new Course(courseRegistrationNumber, courseNumber, courseTitle, credits, instructor, courseTime)
  }

  private def stringToCalendarObject(str: String): Calendar = {
    val patt = "(\\d{1,2}):(\\d{2})([a-zA-Z]{2})".r
    val cal = Calendar.getInstance()
    var (hour, minute) = str match {
      case patt(hr, min, amPm) =>
        if (amPm equalsIgnoreCase "pm") cal.set(Calendar.AM_PM, 1)
        else cal.set(Calendar.AM_PM, 0)
        (Integer parseInt hr, Integer parseInt min)
    }
    if (hour == 12) hour = 0
    cal.set(Calendar.HOUR, hour)
    cal.set(Calendar.MINUTE, minute)
    cal
  }
}

object CourseBuilder {
  private val courseBuilder = new CourseBuilder()

  def getInstance(): CourseBuilder = {
    courseBuilder
  }

  private def getCourseRegistrationNumber(courseDetailToMatchedStringMap: Map[CourseDetail, String]): CourseRegistrationNumber = {
    courseDetailToMatchedStringMap(RegistrationNumberDetail) match {
      case RegistrationNumberDetail.Pattern(registrationNumber) => CourseRegistrationNumber(registrationNumber)
    }
  }

  private def getCourseNumber(courseDetailToMatchedStringMap: Map[CourseDetail, String]): CourseNumber = {
    courseDetailToMatchedStringMap(CourseNumberDetail) match {
      case CourseNumberDetail.CrossListedPattern(num, _) => CourseNumber(num.toInt)
      case CourseNumberDetail.NonCrossListedPattern(num) => CourseNumber(num.toInt)
    }
  }

  private def getCourseTitle(courseDetailToMatchedStringMap: Map[CourseDetail, String]): CourseTitle = {
    courseDetailToMatchedStringMap(CourseTitleDetail) match {
      case CourseTitleDetail.Pattern(title) => CourseTitle(title)
    }
  }

  private def getCourseCredits(courseDetailToMatchedStringMap: Map[CourseDetail, String]): CourseCredits = {
    courseDetailToMatchedStringMap(CourseCreditsDetail) match {
      case CourseCreditsDetail.Pattern(courseCredits) => CourseCredits(courseCredits.toInt)
    }
  }

  private def getCourseInstructor(courseDetailToMatchedStringMap: Map[CourseDetail, String]): Instructor = {
    courseDetailToMatchedStringMap(InstructorDetail) match {
      case InstructorDetail.Pattern(name) => Instructor(name)
    }
  }

  private def getCourseTime(startTimeOpt: Option[Calendar], endTimeOpt: Option[Calendar], daysOpt: Option[Array[Day]]): Option[CourseTime] = {
    for {
      startTime <- startTimeOpt
      endTime <- endTimeOpt
      days <- daysOpt
    } yield {
      CourseTime(startTime, endTime, days)
    }
  }
}