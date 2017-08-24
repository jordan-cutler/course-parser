import java.io._

import parser.CoursePageParser

object ParserMain {
  def main(args : Array[String]): Unit = {
    val fall2017 = new CoursePageParser
    val courses = fall2017.getCoursesBySemester("201740")

    val pw = new FileWriter(new File("hello8.txt"))
    courses.foreach { course =>
      pw.write(course.toString)
      pw.write("*"*70 + "\n")
    }
    pw.close()
  }
}
