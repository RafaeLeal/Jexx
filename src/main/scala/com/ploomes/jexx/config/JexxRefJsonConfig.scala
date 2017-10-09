package com.ploomes.jexx.config

import com.ploomes.jexx.notation.DotNotation
import com.ploomes.jexx.parser.JexxRefParser
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JField, JObject, JString}

import scala.util.matching.Regex

class JexxRefJsonConfig extends JexxDefaultConfig {
  private val defaultConfig = new JexxDefaultConfig
  override val regex: Regex = "\"JexxRef\\{.*?\\}\"".r
  override val parser = JexxRefParser
  override val foundHandler: (Any) => String = {
    case str: String =>

      val replaced = str
        .replaceAllLiterally("\\", "\\\\")
        .replaceAllLiterally("\r", "\\r")
        .replaceAllLiterally("\f", "\\f")
        .replaceAllLiterally("\t", "\\t")
        .replaceAllLiterally("\n", "\\n")
        .replaceAllLiterally("\b", "\\b")
        .replaceAllLiterally("\"", "\\\"")

      s""""$replaced""""
    case null => defaultConfig.foundHandler(null)
    case any: Any => defaultConfig.foundHandler(any)
  }
}

