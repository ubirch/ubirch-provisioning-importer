package com.ubirch

import java.io.{ File, FilenameFilter }

object FileFilters {

  val CSV = new FilenameFilter {
    override def accept(file: File, s: String): Boolean = s.endsWith(".csv")
  }

}
