package com.ploomes.jexx.parser

/**
  * Created by rafaeleal on 19/05/17.
  */
object JexxDefaultParser extends JexxParser {
  override def apply(matched: String): List[String] = {
    case class JexxStack(stack: List[Char] = List(), isIgnoringDot: Boolean = false) {
      def pop: JexxStack = this.copy(stack = stack.dropRight(1))
      def push(c: Char): JexxStack = this.copy(stack = stack :+ c)
      def top: Char = stack.last
      def topOption: Option[Char] = stack.lastOption
      def isEmpty: Boolean = stack.isEmpty
      def ignoreDot: JexxStack = this.copy(isIgnoringDot = true)
      def stopIgnoringDot: JexxStack = this.copy(isIgnoringDot = false)
    }
    val (resp, helper) = matched.foldLeft(List[String](), JexxStack()) {(acc, c) =>
      def addToLast(list: List[String], c: Char) = {
        val last = list.last + c
        list.dropRight(1) :+ last
      }
      val (navList, helper) = acc
      (c, helper.topOption) match {
        case ('$', None) => (navList :+ "", helper.push('$'))
        case (k, Some('\\')) => (addToLast(navList, k), helper.pop)
        case ('.', Some(l)) =>
          if (helper.isIgnoringDot)
            (addToLast(navList, '.'), helper)
          else
            (navList :+ "", helper)
        case ('(', Some(l)) => (navList :+ "(", helper.push('('))
        case ('{', Some('$')) => (navList, helper.push('{'))
        case ('{', Some(l)) => (addToLast(navList, '{'), helper.push('{'))
        case ('[', Some(l)) => (addToLast(navList, '['), helper.push('['))
        case ('"', Some('"')) => (addToLast(navList, '"'), helper.pop.stopIgnoringDot)
        case ('"', Some(l)) => (addToLast(navList, '"'), helper.push('"').ignoreDot)
        case ('}', Some('{')) =>
          if (helper.pop.topOption.contains('$'))
            (navList, helper.pop)
          else
            (addToLast(navList, '}'), helper.pop)
        case (']', Some('[')) => (addToLast(navList, ']'), helper.pop)
        case (')', Some('(')) => (addToLast(navList, ')'), helper.pop)
        case ('\\', Some(l)) => (addToLast(navList, '\\'), helper.push('\\'))
        case (k, Some(l)) => (addToLast(navList, k), helper)
      }
    }
    require(helper.pop.isEmpty)
    resp
  }
}

