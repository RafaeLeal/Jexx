package com.ploomes.jexx.types

import com.ploomes.jexx.Jexx.{JexxList, JexxObject}
import com.ploomes.jexx.exception.{JexxException, JexxValueNotFoundException}
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.parse
import sift.Sift

import scala.util.matching.Regex

/**
  * Created by rafaeleal on 18/05/17.
  */
case class NavigationChunk(operation: String, path: String)


object RichTypes {
  sealed trait RichJexx
  implicit class RichJexxObject(jexxObject: JexxObject) extends RichJexx {
    def richGet(key: String): Any = {
      jexxObject.get(key) match {
        case Some(x) => x
        case None => throw new JexxValueNotFoundException(jexxObject, key)
      }
    }
  }
  implicit class RichJexxList(jexxList: JexxList) {
    def richGet(indexOrSift: Any): Any = {
      val value = indexOrSift match {
        case _: String => throw new JexxException("Can't navigate through lists with key as string")
        case i: Int => jexxList.lift(i)
        case sift: Sift =>
          val option = jexxList.find(sift.testQuery)
          option
        case _ => throw new JexxException(s"Non valid list navigation. Found: $indexOrSift")
      }
      value match {
        case Some(x) => x
        case None => throw new JexxValueNotFoundException(jexxList, indexOrSift)
      }
    }
  }
}
