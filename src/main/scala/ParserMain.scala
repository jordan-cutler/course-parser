import scala.collection.mutable
import java.io._

import parser.CoursePageParser
/**
  * Created by jordancutler on 5/24/17.
  */

object ParserMain {
  def main(args : Array[String]): Unit = {
    val fall2017 = new CoursePageParser("201740")
    val courses = fall2017.getCoursesBySemester()
    val pw = new FileWriter(new File("hello7.txt"))
    courses.foreach { course =>
      pw.write(course.toString)
      pw.write("*"*70 + "\n")
    }
    pw.close()
  }
}
