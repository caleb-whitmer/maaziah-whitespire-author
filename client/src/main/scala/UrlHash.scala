import org.scalajs.*

/**
 * @brief      Removes the hash from the url
 */
def removeHash() = dom.window.history.replaceState(
  null, 
  "", 
  dom.window.location.hash.r.replaceAllIn(dom.window.location.href, "")
)

/**
 * @brief      Scrolls the app to the given hash if it exists
 *
 * @param      hash  The hash to be scrolled to
 */
def scrollToHash(hash: String) = dom.document.getElementById(hash) match {
  case null => ()
  case e => e.scrollIntoView()
}