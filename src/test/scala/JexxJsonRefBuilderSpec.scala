import com.ploomes.jexx.builder.JexxJsonRefBuilder
import org.scalatest.FlatSpec

/**
  * Created by rafaeleal on 20/05/17.
  */
class JexxJsonRefBuilderSpec extends FlatSpec{
  "JexxJsonRefBuilder" must "create a json with JexxRefs" in {
    val jsonWithRefs = """{"a":{"Ref": "pokemon.name"}}"""

    val builder = JexxJsonRefBuilder(jsonWithRefs)
    assert(builder.contains("JexxRef{pokemon.name}"))
  }
}
