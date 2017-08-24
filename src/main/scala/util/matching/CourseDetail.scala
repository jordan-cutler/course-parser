package util.matching

// courseDetails contains all the possible info for the given course we are looking at (all the td elements)
sealed trait CourseDetail {
  val index: Int
}

case object CourseCreditsDetail extends CourseDetail {
  val index = 3
  val Pattern = GeneralPatterns.BetweenTdsPattern
}

case object CourseNumberDetail extends CourseDetail {
  val index = 1
  val Pattern = "<td>([A-Z]+) (\\d{3})-([A-Z0-9]{3})(?s)(.*?)</td>".r
  //val NonCrossListedPattern = "<td>([A-Z]+) (\\d{3})-([A-Z0-9]{3})</td>".r
  // <td>AAS 066-010<p class='xlist'><strong>Cross listed:</strong></p><p class='xlist'>THTR 066-010</p></td>
  //val CrossListedPattern = "<td>([A-Z]+) (\\d{3})-([A-Z0-9]{3})<p class(?s)(.*?)</td>".r
}

case object CourseSubjectDetail extends CourseDetail {
  val index = 1
  val Pattern = "<td>([A-Z]+) (\\d{3})-([A-Z0-9]{3})(?s)(.*?)</td>".r
}

case object CourseSectionDetail extends CourseDetail {
  val index = 1
  val Pattern = "<td>([A-Z]+) (\\d{3})-([A-Z0-9]{3})(?s)(.*?)</td>".r
}

case object CourseTimeDetail extends CourseDetail {
  val index = 7
  // <td class='nowrap'><p>11:10am - 12:25pm (MF)</p>
  // </td>
  val TimeListedPattern = "<td class='nowrap'><p>([a-z0-9:]+) - ([a-z0-9:]+) _([A-Z]+)_</p>".r

  // <td class='nowrap'>To Be Determined</td>
  val TimeUnlistedPattern = "<td(?s)(.*?)</td>".r
}

case object CourseTitleDetail extends CourseDetail {
  val index = 2
  val Pattern = GeneralPatterns.BetweenTdsPattern
}

case object InstructorDetail extends CourseDetail {
  val index = 4
  // <td><p>Susan Kart</p></td>
  val Pattern = "<td><p>(?s)(.*?)</p></td>".r
}

case object RegistrationNumberDetail extends CourseDetail {
  val index = 0
  // <td>43508<a id='43508'></a></td>
  val Pattern = "<td>(\\d{5})<a id='\\d{5}'></a></td>".r
}

object CourseDetail {

  // the format of what a single course looks like on the html page
  val CourseDetailsPattern = "<td(?s)(.*?)</td>".r

  val CourseDetailToIndexMap = Map(
    CourseCreditsDetail -> CourseCreditsDetail.index,
    CourseNumberDetail -> CourseNumberDetail.index,
    CourseTimeDetail -> CourseTimeDetail.index,
    CourseTitleDetail -> CourseTitleDetail.index,
    InstructorDetail -> InstructorDetail.index,
    RegistrationNumberDetail -> RegistrationNumberDetail.index
  )

  val IndexToCourseDetailMap = Map(
    CourseCreditsDetail.index -> CourseCreditsDetail,
    CourseNumberDetail.index -> CourseNumberDetail,
    CourseTimeDetail.index -> CourseTimeDetail,
    CourseTitleDetail.index -> CourseTitleDetail,
    InstructorDetail.index -> InstructorDetail,
    RegistrationNumberDetail.index -> RegistrationNumberDetail
  )
}
