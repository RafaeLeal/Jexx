import com.ploomes.util.ReplacerEnvironment
import org.json4s.DefaultFormats
import org.scalatest.FlatSpec

/**
  * Created by rafaeleal on 3/1/17.
  */
class ReplacerSpec extends FlatSpec {
  implicit val formats = DefaultFormats
  "ReplacerEnvironment" should "replace primitive types" in {
    val replace = ReplacerEnvironment(Map("name" -> "João", "age" -> 32, "hero" -> true))
      .replace("$name has \"$age\" years old and its a \"$hero\" hero")
    assert(replace === "João has 32 years old and its a true hero")
  }
  it should "replace primitive types with brackets" in {
    val replace = ReplacerEnvironment(Map("name" -> "João", "age" -> 32, "hero" -> true))
      .replace("${name} has ${age} years old and its a ${hero} hero")
    assert(replace === "João has 32 years old and its a true hero")
  }

  it should "replace while navigating though Map" in {
    val replace = ReplacerEnvironment(Map("contact" -> Map("name" -> "João", "age" -> 32, "hero" -> true)))
      .replace("$contact.name has $contact.age years old and its a $contact.hero hero")
    assert(replace === "João has 32 years old and its a true hero")
  }
  it should "replace while navigating through Map with brackets" in {
    val replace = ReplacerEnvironment(Map("contact" -> Map("name" -> "João", "age" -> 32, "hero" -> true)))
      .replace("${contact.name} has ${contact.age} years old and its a ${contact.hero} hero")
    assert(replace === "João has 32 years old and its a true hero")
  }
  it should "replace while navigating through Map 2 levels" in {
    val replace = ReplacerEnvironment(Map("foo" -> Map("contact" -> Map("name" -> "João", "age" -> 32, "hero" -> true))))
      .replace("$foo.contact.name has $foo.contact.age years old and its a $foo.contact.hero hero")
    assert(replace === "João has 32 years old and its a true hero")
  }
  it should "replace while navigating through Map 2 levels with brackets" in {
    val replace = ReplacerEnvironment(Map("foo" -> Map("contact" -> Map("name" -> "João", "age" -> 32, "hero" -> true))))
      .replace("${foo.contact.name} has ${foo.contact.age} years old and its a ${foo.contact.hero} hero")
    assert(replace === "João has 32 years old and its a true hero")
  }

  it should "replace while navigating through List of primitives" in {
    val replace = ReplacerEnvironment(Map("foo" -> Map("contact" -> List("João", 32, true))))
      .replace("$foo.contact(0) has ${foo.contact(1)} years old and its a $foo.contact(2) hero")
    assert(replace === "João has 32 years old and its a true hero")
  }
  it should "replace an object with value containing quotes" in {
    val list = List[Map[String, Any]](
      Map[String, Any](
        "Foo" -> "<i class=\"paunoseucu\">"
      )
    )
  }
  it should "replace using sift" in {
    val replace = ReplacerEnvironment(Map("foo" -> Map("x" -> List(Map("bar" -> true, "baz" -> "BarTrue"), Map("bar" -> false, "baz" -> "BarFalse")))))
      .replace("""$foo.x({"bar": true}).baz is BarTrue""")
    assert(replace === "BarTrue is BarTrue")
  }
  it should "replace with $ in the replacer" in {
    val replace = ReplacerEnvironment(Map("foo" -> "${29382}")).replace("$foo")
  }
  it should "replace using sift with references" in {
    val variables = Map(
      "a" -> Map(
        "value" -> 42,
        "options" -> List(Map("id" -> 42, "value" -> true),
                          Map("id" -> 44, "value" -> false))
      )
    )
    val replace = ReplacerEnvironment(variables).replace("""$a.options({"id":{"Ref":["a","value"],"Type":"integer"}}).value""")
  }
  it should "work with API v1 Campos" in {
    val variables = Map(
      "action0" -> List(
        Map(
          "ID_Venda" -> 32,
          "Campos" -> List(
            Map(
              "Id" -> 1232,
              "Campo" -> "Testação",
              "Valor" -> 12,
              "Opcoes" -> List(
                Map(
                  "Valor" -> 12,
                  "Descricao" -> "Opt1"
                ),
                Map(
                  "Valor" -> 11,
                  "Descricao" -> "Opt2"
                ),
                Map(
                  "Valor" -> 13,
                  "Descricao" -> "Opt3"
                )
              )
            )
          )
        )
      )
    )
    val str = "randomstring $action0(0).Campos({\\\"Campo\\\": \\\"Testação\\\"}).Opcoes({\\\"Valor\\\": {\\\"Ref\\\":[\\\"action0(0)\\\", \\\"Campos({\\\\\"Campo\\\\\": \\\\\"Testação\\\\\"})\\\", \\\"Valor\\\"]}}).Valor"
    val str2 = """randomstring $action0(0).Campos({"Campo": "Testação"}).Opcoes({"Valor": {"Ref":["action0(0)", "Campos({\"Campo\": \"Testação\"})", "Valor"]}}).Valor"""
    val replace = ReplacerEnvironment(variables).replace(str)
    //val replace2 = ReplacerEnvironment(variables).replace(str2)
    assert(replace === "randomstring 12")
    //assert(replace2 === "randomstring 12")
  }
}
