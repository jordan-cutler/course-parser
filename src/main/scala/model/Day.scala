package model

sealed trait Day {
  val abbreviation: Char
}

case object Monday extends Day {
  override val abbreviation: Char = 'M'
}

case object Tuesday extends Day {
  override val abbreviation: Char = 'T'
}

case object Wednesday extends Day {
  override val abbreviation: Char = 'W'
}

case object Thursday extends Day {
  override val abbreviation: Char = 'R'
}

case object Friday extends Day {
  override val abbreviation: Char = 'F'
}

object Day {
  def fromAbbreviation(abbreviation: Char): Option[Day] = abbreviation match {
    case Monday.abbreviation => Some(Monday)
    case Tuesday.abbreviation => Some(Tuesday)
    case Wednesday.abbreviation => Some(Wednesday)
    case Thursday.abbreviation => Some(Thursday)
    case Friday.abbreviation => Some(Friday)
    case _ => None
  }
}