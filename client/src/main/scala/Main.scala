import org.scalajs.*
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.*
import scala.annotation.tailrec

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
  dom.window.location.hash = hash

  // Scroll to the given hash
  scrollToHash(hash)
}

@main 
def Main(args: String*): Unit = {
  val appContainer = dom.document.querySelector("#app")
  val app = Seq(
    Sidebar(
      Button("button-home",     "Home",     navButtonCallback("home")),
      Button("button-about",    "About",    navButtonCallback("about")),
      Button("button-works",    "Works",    navButtonCallback("works")),
      Button("button-contact",  "Contact",  navButtonCallback("contact")),
      Button("button-read",     "Read",     navButtonCallback("read")),
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
          "Lorem Ipsum!",
          idAttr := "read"
        ),
        idAttr := "body-content"
      )
    )
  )

  render(appContainer, app)
}