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
  val find: (List[String], Any) => Any
  val notFoundHandler: (List[String]) => String
  val listedBy: (Any) => JexxObject
}
