package com.ploomes.jexx.config

import com.ploomes.jexx.Jexx.{JexxList, JexxObject}
import com.ploomes.jexx.notation.DotNotation
import com.ploomes.jexx.parser.{JexxDefaultParser, JexxParser}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
  * Created by rafaeleal on 19/05/17.
  */
class JexxDefaultConfig extends JexxConfig {
  implicit val formats = DefaultFormats
  override val regex: Regex = """\$[\w:çãáéíóúàõôê]+(\([\w:\{\}\[\]\(\)\\çãáéíóúàõôê", $]*\))*|\$\{([\w\.ç:ãáéíóúàõôê]|\(.+?\))+?\}""".r
  override val parser: JexxParser = JexxDefaultParser
  override val find: (List[String], Any) => Any = {
    (nav: List[String], any: Any) => {
      DotNotation.getValue(any, nav)
    }
  }
  override val listedBy: (Any) => JexxObject = {
    case obj: Map[String, Any] => obj
  }
  override val notFoundHandler: (List[String]) => String = _ => ""
  override val foundHandler: (Any) => String = {
    case v @(_: String | _: Int | _: Boolean | _: Integer | _:BigDecimal) => v.toString
    case j: JexxObject =>  write(j)
    case jl: JexxList =>  write(jl)
    case l: List[Any] =>  write(l)
    case null => "null"
    case any: Any => any.toString
  }
}
