package model

class Course(crn: CourseRegistrationNumber, num: CourseNumber, title: CourseTitle, credits: CourseCredits,
             instructor: Instructor, courseTimesOpt: Option[Seq[CourseTime]], subject: CourseSubject, section: CourseSection) {

  import Course._

  override def toString: String = {
    val start =
      s"CRN: $crn\n" +
        s"Course: $subject $num\n" +
        s"Section: $section\n" +
        s"Title: $title\n" +
        s"Credits: $credits\n" +
        s"Instructor: $instructor\n"

    val timeInfo = "Meeting Times: \n" +
      courseTimesOpt.map { courseTimes =>
        courseTimes.map { courseTime =>
          s"${courseTime.startTime} - ${courseTime.endTime} (${courseTime.daysOffered.map(_.abbreviation).mkString})\n"
        }.mkString
      }.getOrElse(Seq(NoTimesString).mkString)

    start + timeInfo
  }
}

object Course {
  val NoTimesString = "Time: To be determined\n"
}
