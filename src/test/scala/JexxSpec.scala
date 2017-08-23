
import com.ploomes.jexx.config.JexxDefaultConfig
import eval.Eval
import org.scalatest.FlatSpec

/**
  * Created by rafaeleal on 16/05/17.
  */
class JexxSpec extends FlatSpec {
  import com.ploomes.jexx.Jexx._
  "JexxDefaultConfig" must "replace an string with j" in {
    val str: String = "${pokemon.name} attacks with ${pokemon.attack.name}" jexx {
      case "${pokemon.name}" => "Pikachu"
      case "${pokemon.attack.name}" => "thunderbolt"
    }
    assert(str === "Pikachu attacks with thunderbolt")
  }

  it must "replace by Maps" in {
    val str: String =
    "${pokemon.name} attacks with ${pokemon.attack.name}" jexxBy Map(
      "pokemon" -> Map(
        "name" -> "Pikachu",
        "attack" -> Map(
          "name" -> "thunderbolt"
        )
      )
    )
    assert(str === "Pikachu attacks with thunderbolt")
  }

  it must "not replace when not found" in {
    val str: String =
      "${pokemon.name} attacks with ${pokemon.attack.name}" jexxBy Map(
        "pokemon" -> Map(
          "name" -> "Pikachu"
        )
      )
    assert(str === "Pikachu attacks with ${pokemon.attack.name}")
  }

  it must "replace when index is used" in {
    val str: String = {
      "${pokemons(0).name} attacks with ${pokemons(0).attack.name}" jexxBy Map(
        "pokemons" -> List(Map(
          "name" -> "Pikachu",
          "attack" -> Map(
            "name" -> "thunderbolt"
          )
        ))
      )
    }

    assert(str === "Pikachu attacks with thunderbolt")
  }

  it must "replace when a sift is used" in {
    val pikachu = Map(
      "name" -> "Pikachu",
      "attack" -> Map(
        "name" -> "thunderbolt"
      )
    )
    val bulbassaur = Map(
      "name" -> "Bulbassaur"
    )
    val charmander = Map(
      "name" -> "Charmander"
    )

    val str: String = {
      """${pokemons({"name":"Pikachu"})(0).name} attacks with ${pokemons({"name":"Pikachu"})(0).attack.name}""" jexxBy Map(
        "pokemons" -> List(pikachu, bulbassaur, charmander)
      )
    }

    assert(str === "Pikachu attacks with thunderbolt")
  }

  it must "replace when a sift is used as fold" in {
    val pikachu = Map(
      "name" -> "Pikachu",
      "attack" -> Map(
        "name" -> "thunderbolt"
      )
    )
    val bulbassaur = Map(
      "name" -> "Bulbassaur",
      "attack" -> Map(
        "name" -> "cipó"
      )
    )
    val charmander = Map(
      "name" -> "Charmander",
      "attack" -> Map(
        "name" -> "fire"
      )
    )

    val str: String = {
      """Pokemons ${pokemons({})(;).name} attacks with ${pokemons({})(;).attack.name}, respectively""" jexxBy Map(
        "pokemons" -> List(pikachu, bulbassaur, charmander)
      )
    }
    assert(str === "Pokemons Pikachu;Bulbassaur;Charmander attacks with thunderbolt;cipó;fire, respectively")
  }

  it must "replace when a sift is used and it has dot and special characters on value" in {
    val pikachu = Map(
      "name" -> "Pika.chú",
      "attack" -> Map(
        "name" -> "thunderbolt"
      )
    )
    val bulbassaur = Map(
      "name" -> "Bulbassaur"
    )
    val charmander = Map(
      "name" -> "Charmander"
    )

    val str: String = {
      """${pokemons({"name":"Pika.chú"})(0).name} attacks with ${pokemons({"name":"Pika.chú"})(0).attack.name}""" jexxBy Map(
        "pokemons" -> List(pikachu, bulbassaur, charmander)
      )
    }

    assert(str === "Pika.chú attacks with thunderbolt")
  }

  it must "replace with jexx" in {
    val str = """${pokemons({"name":"pikachu"}).name} attacks with ${pokemons({"name":"pikachu"}).attack.name}""" jexx {
      case """${pokemons({"name":"pikachu"}).name}""" => "Pikachu"
      case """${pokemons({"name":"pikachu"}).attack.name}""" => "thunderbolt"
    }
    assert(str === "Pikachu attacks with thunderbolt")
  }
  it must "replace with jexxp" in {
    val str = """${pokemons({"name":"pikachu"}).name} attacks with ${pokemons({"name":"pikachu"}).attack.name}""" jexxp {
      case (_, List("pokemons", """({"name":"pikachu"})""", "name")) => "Pikachu"
      case (_, List("pokemons", """({"name":"pikachu"})""", "attack", "name")) => "thunderbolt"
    }
    assert(str === "Pikachu attacks with thunderbolt")
  }
  it must "replace without brackets" in {
    val str = """$pokemon is cute""" jexxBy Map(
      "pokemon" -> "Pikachu"
    )
    assert(str === "Pikachu is cute")
  }
  it must "ignore dot notation without brackets" in {
    val str = """$pokemon.foo.bar.baz is cute""" jexxBy Map(
      "pokemon" -> "Pikachu"
    )
    assert(str === "Pikachu.foo.bar.baz is cute")
  }

  it must "list matches" in {
    val matches: List[String] = """${foo.bar.baz} on ${baz.bar.foo}""".jexxMatchesStr
    assert(matches.contains("${foo.bar.baz}"))
    assert(matches.contains("${baz.bar.foo}"))
  }

  it must "list dependencies" in {
    val matches: Set[String] = """${foo.bar.baz} on ${baz.bar.foo}""".jexxDeps
    assert(matches.contains("foo"))
    assert(matches.contains("baz"))
  }
}
