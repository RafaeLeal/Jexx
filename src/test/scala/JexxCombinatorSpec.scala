import com.ploomes.jexx.builder.JexxJsonRefBuilder
import com.ploomes.jexx.config.{JexxRefJsonConfig, JexxXmlRefJsonConfig}
import org.json4s.{DefaultFormats, JValue}
import org.scalatest.FlatSpec

/**
  * Created by rafaeleal on 25/05/17.
  */
class JexxCombinatorSpec extends FlatSpec {
  "JexxCombinator" must "work well" in {
    val json =
      """
        |{
        |  "One": {
        |     "Two": {
        |       "Ref": "ref1"
        |     },
        |     "Three": {
        |       "Ref": "ref2 # //test/text()",
        |       "Source": "xml"
        |     }
        |  }
        |}
      """.stripMargin
    val built: String = JexxJsonRefBuilder(json)

    import com.ploomes.jexx.JexxCombinator.JexxCombinatorImpl
    implicit val configs = List(new JexxRefJsonConfig, new JexxXmlRefJsonConfig)
    val combinated = built jexxBy Map(
      "ref1" -> "Two",
      "ref2" -> "<root><test>Foo</test></root>"
    )
    import org.json4s.native.JsonMethods._
    implicit val formats = DefaultFormats
    val jsonCombinated: JValue = parse(combinated)
    assert((jsonCombinated \ "One" \ "Two").extract[String] === "Two")
    assert((jsonCombinated \ "One" \ "Three").extract[String] === "Foo")
  }
  it must "get dependencies right" in {
    val json =
      """
        |{
        |  "One": {
        |     "Two": {
        |       "Ref": "ref1"
        |     },
        |     "Three": {
        |       "Ref": "ref2 # //test/text()",
        |       "Source": "xml"
        |     }
        |  }
        |}
      """.stripMargin

    val built: String = JexxJsonRefBuilder(json)

    import com.ploomes.jexx.JexxCombinator.JexxCombinatorImpl
    implicit val configs = List(new JexxRefJsonConfig, new JexxXmlRefJsonConfig)
    val deps = built.jexxDeps
    assert(deps == Set("ref1", "ref2"))
  }
}
