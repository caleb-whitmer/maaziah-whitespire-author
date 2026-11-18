import org.scalajs.*
import com.raquo.laminar.api.L.*
import scala.annotation.tailrec

/**
 * @brief      Renders a sequence of html elements
 *
 * @param      container  The container to render to
 * @param      rootNodes  The root nodes to be rendered
 */
@tailrec
def render(container: dom.Element, rootNodes: Seq[HtmlElement]): Unit = {
  // Render until there are no more nodes to be rendered
  if (!rootNodes.isEmpty) {
    com.raquo.laminar.api.L.render(container, rootNodes.head)
    render(container, rootNodes.tail)
  }
}

@main def Main(args: String*): Unit = {
  val appContainer = dom.document.querySelector("#app")
  val app = Seq(
    p("Hello World!")
  )

  render(appContainer, app)
}