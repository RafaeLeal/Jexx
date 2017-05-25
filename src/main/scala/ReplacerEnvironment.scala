package com.ploomes.util

import org.json4s.Formats
import org.json4s.JsonAST.JString
import org.json4s.ParserUtil.ParseException
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import sift.Sift

import scala.util.matching.Regex
import scala.util.matching.Regex.{Match, MatchIterator}

/**
  * Created by rafaeleal on 3/1/17.
  */
case class ReplacerEnvironment(variables: Map[String, Any])(implicit formats: Formats) {
  def getValue(path: String): Any = {
    val navList = path.split('.').toList

    this.getValue(variables, navList)
  }
  def matcher(data: Map[String, Any], m: Match): String = {
    val navList = m.matched.split('.').toList
    getValue(data, navList.drop(1)).toString
  }
  def replace(str: String): String = {
    variables.foldLeft(str)((acc, kv) => {
      val key = kv._1
      val value = kv._2
      replaceVariable(key, acc)
    })
  }
  def replaceVariable(name: String, str: String): String = {
    val regex = s"""\\$$$name(\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\))?(\\.[\\w:]+\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\)|\\.[\\w:]+)*|\\$$\\{$name(\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\))?(\\.[\\w:]+\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\)|\\.[\\w:]+)*\\}""".r
    val regexWithQuotes = s"""\\"\\$$$name(\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\))?(\\.[\\w:]+\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\)|\\.[\\w:]+)*\\"|\\"\\$$\\{$name(\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\))?(\\.[\\w:]+\\([\\w:\\{\\}\\[\\]\\(\\)\\\\çãáéíóúàõôê", $$]*\\)|\\.[\\w:]+)*\\}\\"""".r
    def replaceValue(str: String, replacement: String) = regex.replaceAllIn(str, replacement.replace("$", "\\$"))
    def replaceQuotes(str: String, replacement: String) = {
      val s: String = regexWithQuotes.replaceAllIn(str, replacement.replace("$", "\\$"))
      replaceValue(s, replacement)
    }
    def getJsonArraySerializable(arr: List[Map[String, Any]]) = arr.map(getJsonSerializable)
    def getJsonSerializable(obj: Map[String, Any]): Any = {
      obj.mapValues({
        case str: String => str.replace("\"", "\\\\\"")
        case map: Map[String, Any] => getJsonSerializable(map)
        case list: List[Map[String, Any]] => list.map(map => getJsonSerializable(map))
        case any => any
      })
    }
    variables.getOrElse(name, "") match {
      case valueStr: String => replaceValue(str, valueStr)
      case value @ (_:Int | _:Boolean| _: BigInt) => replaceQuotes(str, value.toString)
      case _ @ (_:Map[_, _] | _:List[_] ) =>
        regex.replaceAllIn(str, (regexMatch: Match) => {
          val navList = regexMatch.matched match {
            case brackets if brackets.startsWith("${") && brackets.endsWith("}") =>
              brackets.drop(2).dropRight(1).split('.').toList
            case noBrackets if noBrackets.startsWith("$") =>
              noBrackets.drop(1).split('.').toList
            case error => error.split('.').toList
          }
          //          val navList = regexMatch.matched.split('.').toList
          val navigatedValue: Any = getValue(variables, navList)
          navigatedValue match {
            case valueStr: String => valueStr.replace("$", "\\$")
            case value @ (_:Int | _:Boolean | _: BigInt | _:Double) => value.toString
            case jsonArray: List[Map[String, Any]] =>
              val str = write(getJsonArraySerializable(jsonArray)).replace("\"", "\\\\\"")
              str
            case jsonObj: Map[String, Any] =>
              val obj = write(getJsonSerializable(jsonObj).asInstanceOf[Map[String, Any]])
              val replaced = obj.replace("\"", "\\\\\"")
              replaced
            case null => "null"
          }
        })
    }
  }
  def getQuery(rawStr: String): Map[String, Any] = {
    val eval = StringContext.processEscapes(rawStr)
    getQuery(parse(eval).extract[Map[String, Any]])
  }
  def getQuery(raw: Map[String, Any]): Map[String, Any] = {
    raw.toList.map({
      case (key, map: Map[String, Any]) if map.isDefinedAt("Ref") =>
        val navList = map("Ref").asInstanceOf[List[String]]
        map.get("Type") match {
          case Some("integer") => key -> getValue(navList = navList).asInstanceOf[Int]
          case Some("boolean") => key -> getValue(navList = navList).asInstanceOf[Boolean]
          case None => key -> getValue(navList = navList)
        }
      case (key, map: Map[String, Any]) =>
        key -> getQuery(map)
      case (key, any) =>
        key -> any
    }).toMap
  }
  def getValue(data: Map[String, Any] = variables, navList: List[String]) : Any = {
    val regexPickIndexOrSift = """^\$?([\w:]+)(\(.*\))?""".r
    if (navList.isEmpty) return data
    val matches = regexPickIndexOrSift.findFirstMatchIn(navList.head)

    val subgroups: List[String] = matches.get.subgroups.map(str => if (str == "") null else str)
    val propertyName = subgroups.head
    val parameterList = subgroups(1)
    val dataNavigated = if(parameterList != null) {
      val list = data(propertyName).asInstanceOf[List[Any]] // nav to the list
      val parameterRaw = parameterList.drop(1).dropRight(1) // remove parenthesis
      // when its an array and should be one cell, should concat
      // in this case, parameter is the char for concat. For example, if parameter is ; we have Apple;Banana;Orange
      if (parameterRaw.length == 1 && """\d""".r.findAllIn(parameterRaw).isEmpty) { // if is one char
        return list.foldLeft("")((acc, item) => {
          val itemMap = item.asInstanceOf[Map[String, Any]]
          val propertyForConcat = getValue(itemMap, navList.drop(1))
          propertyForConcat + parameterRaw + acc
        })
      }
      // parameter can be an index ...
      val optionIndex: Option[Int] = try {
        Some(parameterRaw.toInt)
      } catch { case _: Throwable => None}
      // ... or a Sift
      val optionQuery = try {
        val option = Some(getQuery(parameterRaw))
        option
      } catch {
        case pe : ParseException => None
        case t: Throwable =>
          t.printStackTrace()
          None
      }

      val param: Either[Int, Sift] = optionIndex.toLeft(Sift(optionQuery.get))
      param match {
        case Left(int) => list(int)
        case Right(sift) => list.find(sift.testQuery).getOrElse(Map())
      }
    } else { data.getOrElse(propertyName, "") }
    if(navList.drop(1).nonEmpty) {
      getValue(dataNavigated.asInstanceOf[Map[String, Any]], navList.drop(1))
    } else { dataNavigated }
  }
}