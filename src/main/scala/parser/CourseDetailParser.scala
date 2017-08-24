package parser

import java.util.Calendar

import model._
import util.matching._

class CourseDetailParser(courseMatch: String) {

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

  def getCourseTimes: Option[Seq[CourseTime]] = {
    val timesLine = courseDetailToCorrespondingDataMap(CourseTimeDetail)
    val timesMatches = CourseTimeDetail.TimeListedPattern.findAllIn(timesLine)
    if (timesMatches.isEmpty) return None

    Some(
      timesMatches.map {
        case CourseTimeDetail.TimeListedPattern(start, end, daysListed) => new CourseTime(
          start,
          end,
          daysListed.toCharArray.flatMap(Day.fromAbbreviation)
        )
      }.toList
    )
  }
}

object CourseDetailParser {
  private[parser] def stringToCalendarObject(str: String): Calendar = {
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
