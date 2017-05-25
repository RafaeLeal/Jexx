package com.ploomes.jexx.parser

/**
  * Created by rafaeleal on 25/05/17.
  */
object JexxRefParser extends JexxParser {
  override def apply(matched: String): List[String] = {
    require(matched.startsWith("\"JexxRef{") && matched.endsWith("}\""))
    val dotNotationBracketed = matched.drop("\"JexxRef".length).dropRight("\"".length)
    JexxDefaultParser(s"$$$dotNotationBracketed")
  }
}
