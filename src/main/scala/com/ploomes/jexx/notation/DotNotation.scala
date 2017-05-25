package com.ploomes.jexx.notation

import com.ploomes.jexx.Jexx.{JexxList, JexxObject}
import com.ploomes.jexx.exception.JexxException
import com.ploomes.jexx.types.{NavigationChunk, RichTypes}
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.parse
import sift.Sift

import scala.util.matching.Regex

/**
  * Created by rafaeleal on 20/05/17.
  */
object DotNotation extends NavigationNotation {
  import RichTypes._
  private def extractParam(param: String)(implicit formats: DefaultFormats): Any = {
    val indexRegex: Regex = "\\d+".r
    param match {
      case index
        if index.startsWith("(") &&
          index.endsWith(")") &&
          indexRegex.findAllMatchIn(index.drop(1).dropRight(1)).nonEmpty =>
        index.drop(1).dropRight(1).toInt
      case concat
        if  concat.startsWith("(") &&
          concat.endsWith(")") &&
          concat.drop(1).dropRight(1).length == 1 =>
        concat.drop(1).dropRight(1).toCharArray.head
      case sift
        if sift.startsWith("(") &&
          sift.endsWith(")") =>
        val siftStr = sift.drop(1).dropRight(1)
        val query = parse(siftStr).extract[Map[String, Any]]
        Sift(query)
      case str: String => str
    }
  }
  override def getValue(data: Any, navList: List[NavigationChunk]): Any = {
    implicit val formats = DefaultFormats
    getValue(data, navList.map(_.path))
  }

  def getValue(data: Any, navList: List[String])(implicit formats: DefaultFormats): Any = {
    if (navList.isEmpty) {
      return data
    }
    val param = extractParam(navList.head)
    data match {
      case obj: JexxObject =>
        val key = try { param.asInstanceOf[String] } catch {
          case _: ClassCastException =>
            throw new JexxException("Key must be string")
        }
        val navigatedValue = obj.richGet(key)
        this.getValue(navigatedValue, navList.drop(1))
      case arr: JexxList =>
        param match {
          case c: Char => arr.map(this.getValue(_, navList.drop(1))).mkString(c.toString)
          case any: Any => this.getValue(arr.richGet(any), navList.drop(1))
        }
      case list: List[Any] =>
        param match {
          case c: Char => list.map(this.getValue(_, navList.drop(1))).mkString(c.toString)
          case i: Int =>
            try {
              list.lift(i).get
            } catch {
              case _: NoSuchElementException => throw new JexxException(s"List without $i index")
            }
          case any: Any => list.richGet(any)
        }
    }

  }
}
