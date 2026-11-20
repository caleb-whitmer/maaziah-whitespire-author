import scala.util.Random
import Interpolate.*
import scala.annotation.tailrec
import Hash.PseudoRandom
import scala.caps.internal

object Noise {

  def perlin1D( position: Double,
                pr: PseudoRandom = PseudoRandom(1)  ): Double = {

    // Get the interval
    val interval = (
      pr.at(Math.floor(position)).between(-1, 1),
      pr.at(Math.ceil(position)).between(-1, 1),
    )

    // Interpolate using the smoothstep function
    interval match
      case (a, b) => 
        lerp(a, b, smoothstep( position - Math.floor(position) ))
  }
}