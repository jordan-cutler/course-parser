package util.matching

object GeneralPatterns {
  // <td>AAS 003-010</td>
  val BetweenTdsPattern = "<td>(?s)(.*?)</td>".r
}
