import org.scalajs.*
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.*
import scala.annotation.tailrec
import org.scalajs.dom.UIEvent
import scala.language.implicitConversions

/**
 * @brief      Renders any level of nested sequences of html elements
 *
 * @param      container  The container to render to
 * @param      rootNodes  The root nodes to be rendered
 */
def render[ T <: ReactiveElement.Base, 
            S <: Seq[T | S]
          ](container: dom.Element, rootNodes: Seq[T | S]): Unit = {
  for e <- rootNodes do
    e match {
      case g: Seq[_] => render(container, g.asInstanceOf[S])
      case v => com.raquo.laminar.api.L.render(container, v.asInstanceOf[T])
    }
}

/**
 * @brief      Navigates the app to the given hash
 *
 * @param      hash  The hash to be navigated to
 */
def navButtonCallback(hash: String)() = {
  // Update the hash of the current location
  removeHash()

  // Scroll to the given hash
  scrollToHash(hash)
}

/**
 * @brief      Make the logo appear when scrolled to the top of the page
 *
 * @param      threshold  The number of pixels from the top before the logo
 *                        appear animation triggers
 * @param      e          The scroll event
 */
def logoAppearOnScroll(threshold: Double = 5)
                      (e: UIEvent) = // If an element with the id "logo" exists
  Option(dom.document.getElementById("logo")).foreach(logo => {
    // Get the scroll top property of the event target
    val scrollTop 
      = e.target.asInstanceOf[org.scalajs.dom.Element].scrollTop

    // Attempt to get the appear and disappear
    val appear = CssProperty("--a-logo-appear")
    val disappear = CssProperty("--a-logo-disappear")

    // If both animation properties exist apply the appropriate one based on the
    // value of scroll top
    if (appear.isDefined && disappear.isDefined)
      logo.asInstanceOf[org.scalajs.dom.HTMLElement].style.animation
        = if (scrollTop > threshold) disappear.get else appear.get
  })

@main 
def Main(args: String*): Unit = {
  val appContainer = dom.document.querySelector("#app")
  val app = Seq(
    Sidebar(
      Button("button-home",     "Home",     navButtonCallback("home")),
      Button("button-about",    "About",    navButtonCallback("about")),
      Button("button-works",    "Works",    navButtonCallback("works")),
      Button("button-contact",  "Contact",  navButtonCallback("contact")),
      Button("button-read",     "Read",     () => { println("read!") }),
    ),
    Body(
      div(idAttr := "home"),
      Landing,
      div(
        p(
          "Hello World!",
          marginBottom := "800px",
          idAttr := "about"
        ),
        p(
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
          idAttr := "contact"
        ),
        idAttr := "body-content"
      ),
      onScroll.passive(_.throttle(50)) --> logoAppearOnScroll()
    )
  )

  render(appContainer, app)
}