import java.io.StringReader
import java.util

import com.ploomes.jexx.config.JexxXmlConfig
import com.ploomes.jexx.xml.JexxXML
import contextual.Prefix
import kantan.xpath.XPathCompiler
import kantan.xpath.literals.XPathLiteral
import org.jdom2.{Content, Element, Namespace}
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.{Format, XMLOutputter}
import org.jdom2.xpath.XPathFactory
import org.scalatest.FlatSpec
import org.w3c.dom.NamedNodeMap
/**
  * Created by rafaeleal on 23/05/17.
  */
class JexxXMLSpec extends FlatSpec {
    import com.ploomes.jexx.Jexx._

  "JexxXML" must "eval xpath" in {
    val xmlstr =
      """
        |<root xmlns:my="http://www.stojanok.name/2013">
        |	<tag>first</tag>
        |	<tag type="special">second</tag>
        |	<other>third</other>
        |	<other type="">fourth</other>
        | <onetag xmlns:pl="http://www.ploomes.com/xml/2013">
        |   <pl:twotag>Two</pl:twotag>
        | </onetag>
        |	<!-- XPath -->
        |	<deep>
        |		<tag>
        |			<other>fifth</other>
        |		</tag>
        |	</deep>
        |	<!-- namespace -->
        |	<my:other>sixth</my:other>
        |	<my:other my:type="different">seventh</my:other>
        |	<empty></empty>
        |</root>
        |
      """.stripMargin
    val xpathstr = "root/onetag/pl:twotag/text()"
    val result = JexxXML(xmlstr).evalXPath(xpathstr)
    assert(result === "Two")
  }

  it must "eval on SOAP" in {
    val soapResp = """<?xml version="1.0"?>
                     |<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2001/12/soap-envelope" SOAP-ENV:encodingStyle="http://www.w3.org/2001/12/soap-encoding" >
                     |
                     |   <SOAP-ENV:Body xmlns:m="http://www.xyz.org/quotation" >
                     |
                     |      <m:GetQuotationResponse>
                     |         <m:Quotation>Here is the quotation</m:Quotation>
                     |      </m:GetQuotationResponse>
                     |
                     |   </SOAP-ENV:Body>
                     |
                     |</SOAP-ENV:Envelope>""".stripMargin
    val result = JexxXML(soapResp).evalXPath("//m:Quotation/text()")
    assert(result === "Here is the quotation")
  }

  "Jexx with JexxXmlConfig" must "substitute the reference" in {

    implicit val config = new JexxXmlConfig

    val soapResp = """<?xml version="1.0"?>
                     |<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2001/12/soap-envelope" SOAP-ENV:encodingStyle="http://www.w3.org/2001/12/soap-encoding" >
                     |
                     |   <SOAP-ENV:Body xmlns:m="http://www.xyz.org/quotation" >
                     |
                     |      <m:GetQuotationResponse>
                     |         <m:Quotation>I'm the Iron-man</m:Quotation>
                     |      </m:GetQuotationResponse>
                     |
                     |   </SOAP-ENV:Body>
                     |
                     |</SOAP-ENV:Envelope>""".stripMargin

    val starkQuote = "Tony Stark said: #{soapResp # //m:Quotation/text()}" jexxBy Map(
      "soapResp" -> soapResp
    )

    assert(starkQuote === "Tony Stark said: I'm the Iron-man")
  }
}