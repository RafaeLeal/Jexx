package com.ploomes.jexx.parser

/**
  * Created by rafaeleal on 25/05/17.
  */
object JexxXmlParser extends JexxParser {
  override def apply(matched: String): List[String] = {
    require(matched.startsWith("#{") && matched.endsWith("}"))
    val hashInside = matched.drop("#{".length).dropRight("}".length) // #{___}
    hashInside.split('#').map(_.trim).toList
  }
}
