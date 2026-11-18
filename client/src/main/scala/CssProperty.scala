import org.scalajs.*
import scala.annotation.tailrec

trait StyleSheet {
  /**
   * @brief      Look up a CSS property by its alias
   *
   * @param      alias  The alias to search for
   * 
   * @return     An optional string
   */
  def lookup(alias: String): Option[String]
}

/**
 * @brief      This class describes the stylesheet included in the HTML source
 */
private object DefaultStyleSheet extends StyleSheet {
  def lookup(alias: String): Option[String] = {
    val property: String = 
      dom.window.getComputedStyle(dom.document.body).getPropertyValue(alias)

    if (property.isEmpty) None else Some(property) 
  }
}

/**
 * @brief      This class describes a css property lookup based on the alias
 *             argument in the initializer
 */
class CssProperty(alias: String, styleSheet: StyleSheet = DefaultStyleSheet) {
  // Get the property by looking it up in the specified stylesheet
  private val _property = styleSheet.lookup(alias)

  /**
   * @brief      Splits the given string at a specified delimiter which is
   *             unenclosed by parenthesis.
   *
   * @param      str      The string to be split
   * @param      delim    The delimiter
   * @param      output   The output array
   * @param      current  The current element of the array
   * @param      open     The amount of open parenthesis
   */
  @tailrec
  private def _SplitAtUnenclosed( str: String,
                                  delim: Char,
                                  output: Array[String] = Array(),
                                  current: String = String(),
                                  open: Long = 0  ): Option[Array[String]] = {
    // In the case that there are more closed parenthesis than open, return none
    if (open < 0) None
    else if (str.isEmpty)
      // In the case that the output is empty or the amount of open parenthesis
      // is not zero return none, otherwise return the output
      if (0 != open) None
      else if (!current.isEmpty) Some(output ++ Array(current))
      else if (output.isEmpty) None
      else Some(output)
    else {
      str.head match {
        case d if (d == delim) =>
          // In the case that the current string is empty or blank when we hit a
          // delimiter return none as we have hit invalid syntax: "...,,..."
          if (current.isEmpty || current.isBlank) None
          // Otherwise if the delimiter is unenclosed then append the current
          // string to the output array
          else if(0 == open) _SplitAtUnenclosed(  str.tail,
                                                  delim,
                                                  output ++ Array(current),
                                                  String(),
                                                  open  )
          // Otherwise continue down the string
          else _SplitAtUnenclosed(  str.tail,
                                    delim,
                                    output,
                                    current ++ s"${str.head}",
                                    open  )
        // Add or subtract one from the open count when a parenthesis is found
        case '('  => _SplitAtUnenclosed(  str.tail,
                                          delim,
                                          output,
                                          current ++ s"${str.head}",
                                          open + 1  )
        case ')'  => _SplitAtUnenclosed(  str.tail,
                                          delim,
                                          output,
                                          current ++ s"${str.head}",
                                          open - 1  )
        // Default case, continue down the string
        case _    => _SplitAtUnenclosed(  str.tail,
                                          delim,
                                          output,
                                          current ++ s"${str.head}",
                                          open  )
      }
    }
  }

  /**
   * @brief      Returns an optional string representation of the object.
   */
  inline def toStringOption(): Option[String] = _property

  /**
   * @brief      Returns an optional string array representation of the object.
   */
  def toStringArrayOption(): Option[Array[String]] = _property match {
    case None => None
    case Some(str) => {
      // Define a filter to remove all new lines and carriage returns 
      val fr: (Char => Boolean) = (c => c match {
        case '\n' => false
        case '\r' => false
        case _    => true
      })

      // Remove all new lines and carriage returns, and split at any unenclosed
      // commas
      _SplitAtUnenclosed(str.filter(fr), ',')
    }
  }
}


/**
 * @brief      // Implicit conversion to string option
 *
 * @param      p     The CssProperty to be converted to an optional string
 */
given Conversion[CssProperty, Option[String]] with
  def apply(p: CssProperty): Option[String] = p.toStringOption()