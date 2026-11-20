object Interpolate {
  inline def lerp(a: Double, b: Double, t: Double) = 
    a + ( Math.clamp(t, 0d, 1d) * (b - a) )

  inline def lerpGen( a: Double, 
                      b: Double, 
                      f: (Double => Double) = (t => t) ): (Double => Double) = 
    t => lerp(a, b, f(t))

  def smoothstep(t: Double): Double = {
    Math.signum(Math.floor(t)) match
      case -1 /* t <= 0      */ => 0
      case  0 /* 0 <  x <= 1 */ => 
        (3 * t * t) - (2 * t * t * t) /* 3(t^2) - 2(t^3) */
      case  1 /* 1 <  t      */ => 1
  }

  def invNormal(  scale: Double,
                  expectation: Double = 0d)(t: Double): Double =
    Math.exp(-Math.pow(scale * (t - (expectation + 0.5d)), 2d) / 
    (1d / Math.PI))

  def normal( scale: Double,
              expectation: Double = 0d)(t: Double): Double =
    1d - invNormal(scale, expectation)(t)
}