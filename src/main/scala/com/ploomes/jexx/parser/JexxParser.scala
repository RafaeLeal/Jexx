package com.ploomes.jexx.parser

/**
  * Created by rafaeleal on 16/05/17.
  */
trait JexxParser {
  def apply(matched: String): List[String]
}
