import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.api.features.unitArrows

/**
 * @brief      The body of the app, a DIV with the id #body
 *
 * @param      content  The content of the body
 */
def Body(content: Modifier[ReactiveHtmlElement[HTMLDivElement]]*) = div(
  content,
  idAttr := "body"
)