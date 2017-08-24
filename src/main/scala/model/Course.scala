package model

class Course(crn: CourseRegistrationNumber, num: CourseNumber, title: CourseTitle, credits: CourseCredits,
             instructor: Instructor, courseTime: Option[CourseTime], subject: CourseSubject, section: CourseSection) {

  import Course._

  override def toString: String = {
    val start =
      s"CRN: $crn\n" +
        s"Num: $num\n" +
        s"Title: $title\n" +
        s"Credits: $credits\n" +
        s"model.Instructor: $instructor\n"

    start + courseTime.map { courseTime =>
      "Start Time: " + courseTime.startTime + "\n" +
        "End Time: " + courseTime.endTime + "\n" +
        "Days: " + courseTime.daysOffered + "\n"
    }.getOrElse(NoTimesString)
  }
}

object Course {
  val NoTimesString = "Time: To be determined\n"
}
