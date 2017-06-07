package com.ploomes.jexx.config

import com.ploomes.jexx.Jexx.JexxObject
import com.ploomes.jexx.parser.JexxParser

import scala.util.matching.Regex

/**
  * Created by rafaeleal on 19/05/17.
  */
trait JexxConfig {
  val regex: Regex
  val parser: JexxParser
  val foundHandler: (Any) => String
  /**
    * Find function
    * List[String] is the parsed value
    * Any is the variables
    */
  val find: (List[String], JexxObject) => Any
  val notFoundHandler: (String, List[String]) => String
  val listedBy: (Any) => JexxObject = {
    case obj: Map[String, Any] => obj
  }
}
