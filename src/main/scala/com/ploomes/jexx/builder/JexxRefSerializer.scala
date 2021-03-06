package com.ploomes.jexx.builder

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JField, JObject, JString}

/**
  * Created by rafaeleal on 20/05/17.
  */
class JexxRefSerializer extends CustomSerializer[JexxRef](format => (
  {
    case JObject(JField("Ref", JString(s)) :: Nil) => JexxRef(s)
  },
  {
    case ref: JexxRef=>
      ref.Source match {
        case "xml" => JString(s"JexxXmlRef{${ref.Ref}}")
        case "json" => JString(s"JexxRef{${ref.Ref}}")
      }
  }
))
