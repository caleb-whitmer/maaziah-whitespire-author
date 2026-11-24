import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.*
import org.scalajs.dom.SVGSVGElement
import org.scalajs.dom.SVGPolygonElement
import org.scalajs.dom.SVGRadialGradientElement
import org.scalajs.dom.SVGStopElement
import org.scalajs.dom.SVGCircleElement
import scala.language.implicitConversions

import Hash.PseudoRandom
import Noise.perlin1D
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.nodes.ReactiveHtmlElement

/**
 * @brief      Generates the landing which is a dynamic sunset in the mountains
 *
 * @return     A sequences of DIVs of class "landing" which are at differing
 *             layers for a dynamic scrolling effect
 */
def Landing = {
  /**
   * @brief      Create an SVG gradient from a custom CSS property
   *
   * @param      idAttr  The identifier of the SVG gradient
   * @param      values  The values of the CSS property
   */
  def svgRadialGradient ( idAttr: String,
                          values: CssProperty
                        ): ReactiveSvgElement[SVGRadialGradientElement] = {
    def processStops( stops: Array[String]
                    ): Seq[ReactiveSvgElement[SVGStopElement]] = {
      if (stops.isEmpty) then Seq()
      else {
        val offsetReg = "\\d*\\%$".r
        // Get the offset percentage of the current array element
        val offset = offsetReg.findFirstIn(stops.head).getOrElse("0%")
        // Get the rest of the string as the color
        val color = stops.head.dropRight(offset.size)

        // Build the current sequence element based on the offset and color
        Seq(svg.stop(
          svg.stopColor := color,
          svg.offsetAttr := offset
        )) ++ processStops(stops.tail)
      }
    }

    // Build the radial gradient from the array of options
    svg.radialGradient(
      processStops(values.toStringArrayOption().getOrElse(Array())),
      svg.idAttr := idAttr
    )
  }

  /**
   * @brief      Wrap an inline SVG element in a DIV and give it the "landing"
   *             class identifier as well as some defined layer for scrolling
   *             effects
   *
   * @param      elem   The SVG element
   * @param      layer  The layer number to place it on
   *
   * @return     A DIV containing the SVG element with placed at the specified
   *             layer number and given the "landing" class identifier
   */
  def LandingSvgWrapper [ T <: dom.svg.Element
                        ] ( elem: ReactiveSvgElement[T],
                            layer: Int = 0
                          ): HtmlElement = div(
    svg.svg(
      svg.viewBox := "0 0 1 1",
      svg.preserveAspectRatio := "xMidYMid slice",
      // svg.preserveAspectRatio := "xMinYMin slice",
      svg.defs(
        svgRadialGradient("sun", CssProperty("--landing-sun-gradient"))
      ),
      elem,
    ),
    cls := "landing",
    transform := s"translateZ(-${layer}px) scale(${layer + 1})"
  )

  def LandingWrapper[ T <: dom.html.Element
                    ] ( elem: ReactiveHtmlElement[T], 
                        layer: Int = 0
                      ): HtmlElement = div(
    elem,
    cls := "landing",
    transform := s"translateZ(-${layer}px) scale(${layer + 1})"
  )

  // Various noise profiles for mountain creation
  inline def Flat(seed: Long)(t: Double): Double = 0.0d
  inline def Noise3(seed: Long)(t: Double): Double =
    ( perlin1D(t *  80.0f, PseudoRandom(seed)) *   0.005d   )
  inline def Noise2(seed: Long)(t: Double): Double =
    ( perlin1D(t *  15.0f, PseudoRandom(seed)) *   0.05d    ) -
    ( perlin1D(t *  60.0f, PseudoRandom(seed)) *   0.01d    ) +
    ( perlin1D(t * 200.0f, PseudoRandom(seed)) *   0.0025d  ) -
    ( perlin1D(t * 400.0f, PseudoRandom(seed)) *   0.00125d )
  inline def Noise1(seed: Long)(t: Double): Double =
    ( perlin1D(t *  20.0f, PseudoRandom(seed)) *   0.01d    ) -
    ( perlin1D(t * 200.0f, PseudoRandom(seed)) *   0.0025d  ) +
    ( perlin1D(t * 400.0f, PseudoRandom(seed)) *   0.00125d )

  
  /**
   * @brief      Generate a mountain in the form of an SVG which is wrapped in a
   *             "landing" DIV
   *
   * @param      layer       The layer number for the mountain to be placed at
   *                         for scrolling effects
   * @param      noise       The noise profile to generate the mountain with
   * @param      ascension   The ascension profile to generate the mountain with
   * @param      pointCount  The resolution of the mountain
   */
  def Mountain( layer: Int = 0,
                noise: (Double => Double) = Flat(0),
                ascension: (Double => Double) = (t => 0.5d),
                pointCount: Int = 500
              ): HtmlElement = {

    def GetPoints(size: Long, t: Long = 0): String = {
      val tnorm = t.asInstanceOf[Double]/size.asInstanceOf[Double]
      if (t > size) "" else {
        val pos = (
          tnorm,
          ascension(tnorm) + noise(tnorm)
        )

        pos match
          case (x, y) => s"$x,$y " ++ GetPoints(size, t + 1)
      }
    }

    LandingSvgWrapper(
      svg.polygon(
        svg.points := GetPoints(pointCount) ++ "1,1 0,1",
        svg.fill := CssProperty("--landing-mountain-color").getOrElse("magenta")
      ),
      layer
    )
  }

  /**
   * @brief      Generate a sun in the form of a DIV
   *
   * @param      layer    The layer number for the sun to be placed at for
   *                      scrolling effects
   * @param      opacity  The opacity of the sun and its light
   *
   * @return     A DIV placed at a specified layer and with the classes
   *             "landing" and "sun"
   */
  def Sun(  layer: Int = 0,
            opacity: Double = 1d 
          ): HtmlElement = LandingWrapper(
    div(
      styleProp("opacity") := s"$opacity",
      cls := Seq("landing", "sun")
    ),
    layer
  )

  val sunLayer = 25

  Seq(
    Sun(sunLayer, opacity = 1d),
    Mountain( layer = 16,  
              Noise3(seed = 4), 
              Interpolate.lerpGen(0.45d, 1d, Interpolate.invNormal(2d, 0d))
            ),
    Sun(sunLayer, opacity = 0.1d),
    Mountain( layer = 8,  
              Noise3(seed = 3), 
              Interpolate.lerpGen(0.5d, 1d, Interpolate.normal(1.4d, -0.35d))
            ),
    Sun(sunLayer, opacity = 0.1d),
    Mountain( layer = 4,  
              Noise2(seed = 2), 
              Interpolate.lerpGen(0.4d, 1d, Interpolate.normal(1d, 0.2d))
            ),
    Sun(sunLayer, opacity = 0.25d),
    Mountain( layer = 2, 
              Noise1(seed = -15), 
              Interpolate.lerpGen(0.5d, 1d, Interpolate.smoothstep)
            ),
    LandingWrapper(
      div(
        p("Maaziah Whitespire"),
        // p("MAAZIAH WHITESPIRE"),
        idAttr := "logo"
      ),
      layer = sunLayer
    )
  )
}