package com.ploomes.jexx.config

import com.ploomes.jexx.parser.JexxXmlRefParser

import scala.util.matching.Regex

/**
  * Created by rafaeleal on 25/05/17.
  */
class JexxXmlRefJsonConfig extends JexxXmlConfig {
  private val xmlConfig = new JexxXmlConfig
  override val regex: Regex = "\"JexxXmlRef\\{.*?\\}\"".r
  override val parser = JexxXmlRefParser
  override val foundHandler: (Any) => String = {
    case str: String => s""""$str""""
    case null => xmlConfig.foundHandler(null)
    case any: Any => xmlConfig.foundHandler(any)
  }
}