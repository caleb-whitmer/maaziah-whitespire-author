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

@main 
def Main(args: String*): Unit = {
  val appContainer = dom.document.querySelector("#app")
  val app = Seq(
    Landing,
    div(
      p("Hello World!"),
      display := "flow-root",
      minHeight := "2000px",
      backgroundColor := "white"
    )
  )

  render(appContainer, app)
}