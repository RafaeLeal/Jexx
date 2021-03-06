import com.ploomes.jexx.config.JexxRefJsonConfig
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JNull
import org.scalatest.FlatSpec
import org.json4s.native.JsonMethods._

import scala.util.Try
/**
  * Created by rafaeleal on 20/05/17.
  */
class JexxJsonSpec extends FlatSpec {
  import com.ploomes.jexx.Jexx._
  implicit val formats = DefaultFormats
  "JexxJsonConfig" must "reference a list" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("b" -> "string", "n" -> null)
      )
    )
    assert(Try(parse(result)).isSuccess)
    assert(Try((parse(result) \ "a").extract[List[Map[String, Any]]]).isSuccess)
  }
  it must "reference a object" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0)}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "string", "i" -> 42, "b" -> true)
      )
    )
    assert(Try(parse(result)).isSuccess)
    assert(Try((parse(result) \ "a").extract[Map[String, Any]]).isSuccess)
  }
  it must "reference a string" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0).s}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "string", "i" -> 42, "b" -> true, "n" -> null)
      )
    )
    assert(Try(parse(result)).isSuccess)
    assert(Try((parse(result) \ "a").extract[String]).isSuccess)
  }
  it must "reference a number" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0).i}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "string", "i" -> 42, "b" -> true, "n" -> null)
      )
    )
    assert(Try(parse(result)).isSuccess)
    assert(Try((parse(result) \ "a").extract[Double]).isSuccess)
  }
  it must "reference a boolean" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0).b}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "string", "i" -> 42, "b" -> true, "n" -> null)
      )
    )
    assert(Try(parse(result)).isSuccess)
    assert(Try((parse(result) \ "a").extract[Boolean]).isSuccess)
  }
  it must "reference a null" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0).n}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "string", "i" -> 42, "b" -> true, "n" -> null)
      )
    )
    val triedJValue = Try(parse(result))
    assert(triedJValue.isSuccess)
    assert((triedJValue.get \ "a") == JNull)
  }

  it must "reference a string with quote" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0).s}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "st\"ring", "i" -> 42, "b" -> true, "n" -> null)
      )
    )
    val triedJValue = Try(parse(result))
    assert(triedJValue.isSuccess)
    assert((triedJValue.get \ "a").extract[String] == "st\"ring")
  }

  it must "reference a string with backslash" in {
    implicit val config = new JexxRefJsonConfig
    val jsonStr = """{
                    |  "a": "JexxRef{a(0).s}"
                    |}""".stripMargin
    val result = jsonStr jexxBy Map(
      "a" -> List(
        Map("s" -> "str\\ing", "i" -> 42, "b" -> true, "n" -> null)
      )
    )
    val triedJValue = Try(parse(result))
    assert(triedJValue.isSuccess)
    assert((triedJValue.get \ "a").extract[String] == "str\\ing")
  }
}
