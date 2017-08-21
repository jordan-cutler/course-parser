package util.matching

import util.matching.CourseDetail._

// courseDetails contains all the possible info for the given course we are looking at (all the td elements)
sealed trait CourseDetail {
  val index: Int
}

case object CourseCredits extends CourseDetail {
  val index = 3
  val Pattern = GeneralPatterns.BetweenTdsPattern
}

case object CourseNumber extends CourseDetail {
  val index = 1
  val NonCrossListedPattern = GeneralPatterns.BetweenTdsPattern
  // <td>AAS 066-010<p class='xlist'><strong>Cross listed:</strong></p><p class='xlist'>THTR 066-010</p></td>
  val CrossListedPattern = "<td>(?s)(.*?)<p class(?s)(.*?)</td>".r
}

case object CourseTime extends CourseDetail {
  val index = 7
  // <td class='nowrap'><p>11:10am - 12:25pm (MF)</p>
  // </td>
  val TimeListedPattern = "<td class='nowrap'><p>([a-z0-9:]+) - ([a-z0-9:]+) _([A-Z]+)_</p>".r

  // <td class='nowrap'>To Be Determined</td>
  val TimeUnlistedPattern = "<td(?s)(.*?)</td>".r
}

case object CourseTitle extends CourseDetail {
  val index = 2
  val Pattern = GeneralPatterns.BetweenTdsPattern
}

case object Instructor extends CourseDetail {
  val index = 4
  // <td><p>Susan Kart</p></td>
  val Pattern = "<td><p>(?s)(.*?)</p></td>".r
}

case object RegistrationNumber extends CourseDetail {
  val index = 0
  // <td>43508<a id='43508'></a></td>
  val Pattern = "<td>(\\d{5})<a id='\\d{5}'></a></td>".r
}

object CourseDetail {

  // the format of what a single course looks like on the html page
  val CourseDetailsPattern = "<td(?s)(.*?)</td>".r

  val CourseDetailToIndexMap = Map(
    CourseCredits -> CourseCredits.index,
    CourseNumber -> CourseNumber.index,
    CourseTime -> CourseTime.index,
    CourseTitle -> CourseTitle.index,
    Instructor -> Instructor.index,
    RegistrationNumber -> RegistrationNumber.index
  )

  val IndexToCourseDetailMap = Map(
    CourseCredits.index -> CourseCredits,
    CourseNumber.index -> CourseNumber,
    CourseTime.index -> CourseTime,
    CourseTitle.index -> CourseTitle,
    Instructor.index -> Instructor,
    RegistrationNumber.index -> RegistrationNumber
  )
}
