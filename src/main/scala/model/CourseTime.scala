package model

import java.util.Calendar

case class CourseTime(startTime: String, endTime: String, days: Seq[Day])

object CourseTime {
  def apply(startTime: Calendar, endTime: Calendar, days: Seq[Day]): CourseTime = {
    new CourseTime(getTime(startTime), getTime(endTime), days)
  }

  def getTime(cal: Calendar): String = {
    var amPm = "AM"
    var minutes = String.valueOf(cal.get(Calendar.MINUTE))
    var hour = String.valueOf(cal.get(Calendar.HOUR))

    if (hour.equalsIgnoreCase("0")) hour = "12"
    if (cal.get(Calendar.AM_PM) == 1) amPm = "PM"
    if (minutes.equalsIgnoreCase("0")) minutes = "00"
    hour + ":" + minutes + amPm
  }
}

