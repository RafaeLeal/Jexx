package com.ploomes.jexx.parser

/**
  * Created by rafaeleal on 25/05/17.
  */
object JexxXmlRefParser extends JexxParser {
  override def apply(matched: String): List[String] = {
    require(matched.startsWith("\"JexxXmlRef{") && matched.endsWith("}\""))
    val hashNotationBracketed = matched.drop("\"JexxXmlRef".length).dropRight("\"".length)
    JexxXmlParser(s"#$hashNotationBracketed")
  }
}
