package model

case class CourseNumber(number: Int) {
  override def toString: String = {
    number.toString
  }
}
