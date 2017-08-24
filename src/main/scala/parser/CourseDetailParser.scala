package parser

import java.util.Calendar

import model._
import util.matching._

class CourseDetailParser(courseMatch: String) {

  import CourseDetailParser._

  val courseDetailToCorrespondingDataMap = {
    val courseDetailMatches = CourseDetail.CourseDetailsPattern.findAllIn(courseMatch).toSeq

    courseDetailMatches.zipWithIndex.flatMap {
      case (courseDetailMatchedString, indexOfMatchedString) =>
        CourseDetail.IndexToCourseDetailMap.get(indexOfMatchedString).map { courseDetail =>
          courseDetail -> courseDetailMatchedString
        }
    }.toMap
  }

  def getCourseRegistrationNumber: CourseRegistrationNumber = {
    courseDetailToCorrespondingDataMap(RegistrationNumberDetail) match {
      case RegistrationNumberDetail.Pattern(registrationNumber) => CourseRegistrationNumber(registrationNumber)
    }
  }

  def getCourseNumber: CourseNumber = {
    courseDetailToCorrespondingDataMap(CourseNumberDetail) match {
      case CourseNumberDetail.Pattern(_, num, _, _) => CourseNumber(num.toInt)
      //case CourseNumberDetail.CrossListedPattern(_, num, _, _) => CourseNumber(num.toInt)
      //case CourseNumberDetail.NonCrossListedPattern(_, num, _) => CourseNumber(num.toInt)
    }
  }

  def getCourseTitle: CourseTitle = {
    courseDetailToCorrespondingDataMap(CourseTitleDetail) match {
      case CourseTitleDetail.Pattern(title) => CourseTitle(title)
    }
  }

  def getCourseCredits: CourseCredits = {
    courseDetailToCorrespondingDataMap(CourseCreditsDetail) match {
      case CourseCreditsDetail.Pattern(courseCredits) => CourseCredits(courseCredits)
    }
  }

  def getCourseInstructor: Instructor = {
    courseDetailToCorrespondingDataMap(InstructorDetail) match {
      case InstructorDetail.Pattern(name) => Instructor(name)
    }
  }

  def getCourseSubject: CourseSubject = {
    courseDetailToCorrespondingDataMap(CourseNumberDetail) match {
      case CourseSubjectDetail.Pattern(subject, _, _, _) => CourseSubject(subject)
    }
  }

  def getCourseSection: CourseSection = {
    courseDetailToCorrespondingDataMap(CourseNumberDetail) match {
      case CourseSectionDetail.Pattern(_, _, section, _) => CourseSection(section)
    }
  }

  def getCourseTime: Option[CourseTime] = {
    // Replace the parentheses in the string to make it easier to match
    var replacedTimeLine: String = courseDetailToCorrespondingDataMap(CourseTimeDetail).replaceAll("[()]", "_")
    // if there is a </p> tag in the time line, we cut it off at at the end of the </p> tag to prevent regexing the td that goes to the subsequent line
    if (replacedTimeLine.indexOf("</p>") != -1) replacedTimeLine = replacedTimeLine.substring(0, replacedTimeLine.indexOf("</p>") + 4)
    val (startTimeOpt, endTimeOpt, daysOpt) = replacedTimeLine match {
      case CourseTimeDetail.TimeListedPattern(start, end, days) => (
        Some(stringToCalendarObject(start)),
        Some(stringToCalendarObject(end)),
        Some(days.toCharArray.flatMap(Day.fromAbbreviation))
      )
      case CourseTimeDetail.TimeUnlistedPattern(_) => (None, None, None)
    }

    for {
      startTime <- startTimeOpt
      endTime <- endTimeOpt
      days <- daysOpt
    } yield {
      CourseTime(startTime, endTime, days)
    }
  }
}

object CourseDetailParser {
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
