class ImitationStyleSheet(properties: Map[String, String]) extends StyleSheet {
  inline def lookup(alias: String): Option[String] = properties.get(alias)
}

class CssPropertySuite extends munit.FunSuite {
  test( "toString() should return the desired property"){
    val ss = ImitationStyleSheet(Map(
      ("--prop-a" -> "rgb(10, 10, 10)"),
      ("--prop-b" -> "rgb(11, 11, 11)"),
      ("--prop-c" -> "rgb(12, 12, 12)"),
    ))

    assertEquals(CssProperty("--prop-b", ss).toStringOption(), Some("rgb(11, 11, 11)"))
  }

  test( "toStringOption() and toStringArrayOption() should both return None" ++ 
        " when the alias for the property does not exist"){
    val ss = ImitationStyleSheet(Map(
      ("--some-prop" -> "hsl(10deg, 5%, 15%)")
    ))

    assertEquals(CssProperty("--no-prop", ss).toStringOption(), None)
    assertEquals(CssProperty("--no-prop", ss).toStringArrayOption(), None)
  }

  test( "toStringArrayOption() should return None when there are a differing" ++ 
        " amounts of closing and opening parenthesis."){
    val ss = ImitationStyleSheet(Map(
      ("--more-close" -> "rbg(0), 255, 255)"),
      ("--more-open" -> "(rbg(0, 255, 255)"),
      ("--same-open-and-close" -> "rbg(0, calc(255), calc(255))"),
    ))

    assertEquals(CssProperty("--more-close", ss).toStringArrayOption(), None)
    assertEquals(CssProperty("--more-open", ss).toStringArrayOption(), None)
    assertNotEquals(CssProperty("--same-open-and-close", ss).toStringArrayOption(), None)
  }

  test( "toStringArrayOption() should split the property at all unenclosed" ++ 
        " commas"){
    val ss = ImitationStyleSheet(Map(
      ("--list" -> "rgb(1, 1, 1) 10%,\nrgb(2, 2, 2) 20%,\nrgb(3, 3, 3) 30%")
    ))

    CssProperty("--list", ss).toStringArrayOption() match {
      case None => assertEquals(true, false)
      case Some(a) => assertEquals(a.corresponds(Array[String](
        "rgb(1, 1, 1) 10%",
        "rgb(2, 2, 2) 20%",
        "rgb(3, 3, 3) 30%",
      )){_ == _}, true)
    }
  }
}