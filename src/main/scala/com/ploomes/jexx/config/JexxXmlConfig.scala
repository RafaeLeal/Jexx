package com.ploomes.jexx.config
import com.ploomes.jexx.Jexx.JexxObject
import com.ploomes.jexx.parser.{JexxParser, JexxXmlParser}
import com.ploomes.jexx.xml.JexxXML

import scala.util.Try
import scala.util.matching.Regex

/**
  * Created by rafaeleal on 25/05/17.
  */
class JexxXmlConfig extends JexxConfig{
  override val regex: Regex = """#\{.*?\}""".r
  override val parser: JexxParser = JexxXmlParser
  override val foundHandler: (Any) => String = {
    case null => "null"
    case any: Any => any.toString
  }
  override val find: (List[String], JexxObject) => Any = {
    case (parsed: List[String], variables: JexxObject) if parsed.size == 2 =>
      val variableName = parsed.head
      val xpathStr = parsed(1)
      val xmlStr = variables(variableName).asInstanceOf[String]
      JexxXML(xmlStr).evalXPath(xpathStr) match {
        case int: String if Try(int.toInt).isSuccess => int.toInt
        case double: String if Try(double.toDouble).isSuccess => double.toDouble
        case bool: String if Try(bool.toBoolean).isSuccess => bool.toBoolean
        case str: String => str
      }
    case _ => null
  }
  override val notFoundHandler: (String, List[String]) => String = {
    case (nparsed, _) => nparsed
  }
}
