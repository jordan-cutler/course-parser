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
        case CourseTimeDetail.TimeListedPattern(start, end, daysListed) => CourseTime(
          startTime = start,
          endTime = end,
          daysOffered = daysListed.toCharArray.flatMap(Day.fromAbbreviation)
        )
      }.toList
    )
  }
}