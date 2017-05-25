import com.ploomes.jexx.builder.JexxJsonRefBuilder
import org.scalatest.FlatSpec

/**
  * Created by rafaeleal on 20/05/17.
  */
class JexxJsonRefBuilderSpec extends FlatSpec{
  "JexxJsonRefBuilder" must "create a json with JexxRefs" in {
    val jsonWithRefs = """{"a":{"Ref": "pokemon.name"}}"""

    val built = JexxJsonRefBuilder(jsonWithRefs)
    assert(built.contains("JexxRef{pokemon.name}"))
  }
  it must "create a json with JexxXmlRefs" in {
    val jsonWithRefs = """{"a":{"Ref": "trainers # //ash/name", "Source": "xml"}}"""

    val built = JexxJsonRefBuilder(jsonWithRefs)
    assert(built.contains("JexxXmlRef{trainers # //ash/name}"))
  }
}
