package com.ploomes.jexx.xml

import java.io.StringReader

import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.{Format, XMLOutputter}
import org.jdom2.xpath.XPathFactory
import org.jdom2.{Content, Element, Namespace}

/**
  * Created by rafaeleal on 25/05/17.
  */
case class JexxXML(xml: String) {
  import scala.collection.JavaConverters._
  private val builder = new SAXBuilder()
  private val document = builder.build(new StringReader(xml))
  private val xPathFactory = XPathFactory.instance()

  private val outputter = new XMLOutputter
  outputter.setFormat(Format.getPrettyFormat)
  private def getAllNamespaces(e: Element) = {
    val map: List[Namespace] = e.getChildren.asScala.toList.flatMap(_.getAdditionalNamespaces.asScala.toList)
    val thisNodeNamespaces = e.getAdditionalNamespaces.asScala.toList
    thisNodeNamespaces ++ map
  }
  def evalXPath(xpath: String): String = {
    val xpathObj = xPathFactory.compile(xpath, Filters.fpassthrough(), null, getAllNamespaces(document.getRootElement).asJava)
    val evaluate = xpathObj.evaluate(document).asScala.toList.map(_.asInstanceOf[Content]).asJava
    outputter.outputString(evaluate)
  }
}
