import com.raquo.laminar.api.L.*
import org.scalajs.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.api.features.unitArrows

/**
 * @brief      The sidebar of the app
 */
def Sidebar(content: Modifier[ReactiveHtmlElement[HTMLDivElement]]*) = div(
  content,
  idAttr := "sidebar"
)