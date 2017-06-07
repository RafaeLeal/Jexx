package com.ploomes.jexx

import com.ploomes.jexx.config.JexxConfig

import scala.util.matching.Regex

/**
  * Created by rafaeleal on 25/05/17.
  */
object JexxCombinator {
  implicit class JexxCombinatorImpl(str: String)(implicit configs: List[JexxConfig]) extends JexxInterface {
    override def jexxBy(x: Any): String = {
      import Jexx.JexxImpl
      configs.foldLeft(str) {(acc, conf) => {
        JexxImpl(acc)(conf).jexxBy(x)
      }}
    }

    override def jexx(f: (String) => String): String = {
      import Jexx.JexxImpl
      configs.foldLeft(str) {(acc, conf) => {
        JexxImpl(acc)(conf).jexx(f)
      }}
    }

    override def jexxp(f: (String, List[String]) => String): String = {
      import Jexx.JexxImpl
      configs.foldLeft(str) {(acc, conf) => {
        JexxImpl(acc)(conf).jexxp(f)
      }}
    }

    override def jexxMatches: List[Regex.Match] = {
      import Jexx.JexxImpl
      configs.flatMap(config => {
        JexxImpl(str)(config).jexxMatches
      })
    }

    override def jexxMatchesStr: List[String] = {
      import Jexx.JexxImpl
      configs.flatMap(config => {
        JexxImpl(str)(config).jexxMatchesStr
      })
    }

    override def jexxPaths: List[List[String]] = {
      import Jexx.JexxImpl
      configs.flatMap(config => {
        JexxImpl(str)(config).jexxPaths
      })
    }
  }
}
