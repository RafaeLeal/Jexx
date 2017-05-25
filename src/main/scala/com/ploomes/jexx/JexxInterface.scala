package com.ploomes.jexx
import scala.util.matching.Regex

/**
  * Created by rafaeleal on 25/05/17.
  */
trait JexxInterface {

  def jexxBy(x: Any): String

  def jexx(f: String => String): String

  def jexxp(f: List[String] => String): String

  def jexxMatches: List[Regex.Match]

  def jexxMatchesStr: List[String]

  def jexxPaths: List[List[String]]

  def jexxDeps: Set[String] =
    jexxPaths.map(_.head).toSet
}
