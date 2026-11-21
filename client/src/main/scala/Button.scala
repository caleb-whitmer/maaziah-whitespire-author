import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows

/**
 * @brief      A custom button type which supports icons and text
 *
 * @param      id        The identifier of the button
 * @param      text      The text displayed on the button
 * @param      callback  The callback function when the button is pressed
 */
def Button( id: String, 
            text: String,
            callback: (() => Unit) = (() => ())
          ) = div(
    div(cls := "icon"),
    div(
      text,
      cls := "text"
    ),
    onClick --> callback(),
    cls := "button",
    idAttr := id
)