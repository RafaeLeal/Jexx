package com.ploomes.jexx.builder

import com.ploomes.jexx.Jexx.JexxObject
import com.ploomes.jexx.exception.JexxException
import org.json4s.DefaultFormats
import org.json4s.native.Json
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
/**
  * Created by rafaeleal on 20/05/17.
  */
object JexxJsonRefBuilder {
  def substituteReference(d: Any): Any = d match {
    case l: List[Map[_, _]] =>
      val list = l.asInstanceOf[List[Map[String, Any]]]
      list.map(_.map(substituteReference))
    case obj: Map[String, Any] =>
      val isReference = obj.get("Ref").isDefined
      if (isReference) {
        val ref = obj("Ref").asInstanceOf[String]
        val source = obj.getOrElse("Source", "json").asInstanceOf[String]
        JexxRef(ref, source)
      } else obj.mapValues(value => substituteReference(value))
    case null => null
    case any: Any => any
  }
  def apply(jsonString: String): String = {
    implicit val formats = DefaultFormats + new JexxRefSerializer
    val map: Map[String, Any] = parse(jsonString).extract[JexxObject]
    val mapWithRef: JexxObject = try {
        substituteReference(map).asInstanceOf[JexxObject]
      } catch {
        case t: Throwable => throw new JexxException("Could not substitute references")
      }
      write(mapWithRef)
    }
}