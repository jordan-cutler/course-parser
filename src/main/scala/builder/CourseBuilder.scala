package builder

import java.util.Calendar

import model.Course
import util.matching._

class CourseBuilder() {

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

    val courseRegistrationNumber = courseDetailToMatchedStringMap(RegistrationNumber) match {
      case RegistrationNumber.Pattern(registrationNumber) => registrationNumber
    }

    val courseNumber = courseDetailToMatchedStringMap(CourseNumber) match {
      case CourseNumber.CrossListedPattern(num, _) => num
      case CourseNumber.NonCrossListedPattern(num) => num
    }

    val courseTitle = courseDetailToMatchedStringMap(CourseTitle) match {
      case CourseTitle.Pattern(title) => title
    }

    val credits = courseDetailToMatchedStringMap(CourseCredits) match {
      case CourseCredits.Pattern(courseCredits) => courseCredits
    }

    val instructor = courseDetailToMatchedStringMap(Instructor) match {
      case Instructor.Pattern(name) => name
    }

    // Replace the parentheses in the string to make it easier to match
    var replacedTimeLine: String = courseDetails(indices(5)).replaceAll("[()]", "_")
    // if there is a </p> tag in the time line, we cut it off at at the end of the </p> tag to prevent regexing the td that goes to the subsequent line
    if (replacedTimeLine.indexOf("</p>") != -1) replacedTimeLine = replacedTimeLine.substring(0, replacedTimeLine.indexOf("</p>") + 4)
    val (startTimeOpt, endTimeOpt, daysOpt) = replacedTimeLine match {
      case CourseTime.TimeListedPattern(s, e, d) => (
        Some(stringToCalendarObject(s)),
        Some(stringToCalendarObject(e)),
        Some(d.toCharArray.flatMap(Day.fromChar))
      )
      case CourseTime.TimeUnlistedPattern(_) => (None, None, None)
    }
    val courseTime =
      for {
        startTime <- startTimeOpt
        endTime <- endTimeOpt
        days <- daysOpt
      } yield {
        CourseTime(startTime, endTime, days)
      }
    //      startTimeOpt.map { startTime =>
    //      endTimeOpt.map { endTime =>
    //        daysOpt.map { days =>
    //          CourseTime(startTime, endTime, days)
    //        }
    //      }
    //    }
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
}