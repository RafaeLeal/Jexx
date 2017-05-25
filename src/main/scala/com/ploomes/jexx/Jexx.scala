package com.ploomes.jexx

import com.ploomes.jexx.config.{JexxConfig, JexxDefaultConfig}

import scala.util.Try
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

object Jexx {
  type JexxObject = Map[String, Any]
  type JexxList = List[Any]

  implicit val config = new JexxDefaultConfig
  implicit class JexxImpl(str: String)(implicit config: JexxConfig) extends JexxInterface {
    override def jexxBy(x: Any): String = {
      val variables: JexxObject = config.listedBy(x)
      str.jexxp(parsed => {
        val navigatedValue = config.find(parsed, variables)
        config.foundHandler(navigatedValue)
      })
    }

    override def jexx (f: String => String): String = {
      val matches = str.jexxMatchesStr
      matches.foldLeft(str) { (acc, m) =>
        val replacement: String = f(m)
        acc.replaceAllLiterally(m, replacement)
      }
    }

    override def jexxp (f: List[String] => String): String = {
      val matches = str.jexxMatches
      matches.foldLeft(str) { (acc, m) =>
        val parsed = config.parser(m.matched)
        val replacement: String = f(parsed)
        acc.replaceAllLiterally(m.matched, replacement)
      }
    }

    override def jexxMatches : List[Match] = {
      config.regex
        .findAllMatchIn(str)
        .filter(m => Try(config.parser(m.matched)).isSuccess)
        .toList
    }
    override def jexxMatchesStr: List[String] = {
      str.jexxMatches
        .map(_.matched)
    }
    override def jexxPaths: List[List[String]] = {
      config.regex
        .findAllMatchIn(str)
        .map(m => Try(config.parser(m.matched)))
        .filter(_.isSuccess)
        .map(_.get)
        .toList
    }

  }


}






