package com.ploomes.jexx.exception

import com.ploomes.jexx.Jexx.{JexxList, JexxObject}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

import scala.util.{Left, Right, Try}

/**
  * Created by rafaeleal on 18/05/17.
  */
class JexxValueNotFoundException(either: Either[JexxObject, JexxList], key: Any) extends JexxException("Value not found") {
  def this(obj: JexxObject, key: Any) {
    this(Left(obj), key)
  }
  def this(list: JexxList, key: Any) {
    this(Right(list), key)
  }
  override def getMessage: String = {
    implicit val formats = DefaultFormats
    val str = either match {
      case Left(obj) =>
        Try(Serialization.writePretty(obj)).getOrElse(obj.toString)
      case Right(list) =>
        Try(Serialization.writePretty(list)).getOrElse(list.toString)
    }
    s"$str does not have $key"
  }
}
