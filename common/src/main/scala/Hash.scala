import scala.annotation.tailrec
import java.lang.Integer.toBinaryString

object Hash {

  /**
   * @brief      FNV-1a (64 bit) hashing algorithm
   *
   * @param      octets  The octets to hash
   *
   * @return     The 64 bit hash
   */
  def fnv1a64(octets: Seq[Byte]): Long = {
    // Constants for use in hashing
    // See: https://www.isthe.com/chongo/tech/comp/fnv/
    val offsetBasis: Long  = -3750763034362895579L
    val fnvPrime: Long     = 1099511628211L

    @tailrec
    def fnv1aRec(subOctets: Seq[Byte], hash: Long = offsetBasis): Long = {
      // If there are no more octets to hash then return the final hash
      if (subOctets.isEmpty) 
        hash 
      // Otherwise, hash the head and combine with the recursively hashed tail
      else 
        fnv1aRec(subOctets.tail, (hash ^ subOctets.head) * fnvPrime)
    }

    // Call recursive sub-algorithm
    fnv1aRec(octets)
  }

  def xorshift64(state: Long): Long = {
    val x = state ^ (state << 13)
    val y = x ^ (x >> 7)
    y ^ (y >> 17)
  }

  class PseudoRandom(seed: Long) {
    class PseudoRandomUnit(value: Long) {
      /**
       * @brief      Returns some fractional that is 0 <= x <= 1
       *
       * @return     A positive fractional
       */
      def frac = Math.abs(value.asInstanceOf[Double]) / 9.223372036854776E18

      /**
       * @brief      Returns some value between a and b
       *
       * @param      a     limit a
       * @param      b     limit b
       */
      def between(a: Double, b: Double) = {
        // Sort by min and max
        val max = Math.max(a, b)
        val min = Math.min(a, b)
        min + (frac * (max - min))
      }

      /* Common converters */
      def long    = value
      def int     = (value >> 32).asInstanceOf[Int]
      def char    = (value >> 48).asInstanceOf[Char]
      def byte    = (value >> 56).asInstanceOf[Byte]
    }


    /**
     * @brief      Gets a specific pseudo random value based on the seed and
     *             some time value `t`.
     *
     * @param      t     The time value
     *
     * @return     The pseudo random unit.
     */
    def at(t: Double): PseudoRandomUnit = {
      // Convert the time to a raw long and combine it with the seed
      val seeded: Long = xorshift64(java.lang.Double.doubleToRawLongBits(t) ^ seed)
      // Get the seeded time as a sequence of octets for passing to fnv1a64
      val seededOctets = Seq[Byte](
        (seeded >>  0).asInstanceOf[Byte],
        (seeded >>  8).asInstanceOf[Byte],
        (seeded >> 16).asInstanceOf[Byte],
        (seeded >> 24).asInstanceOf[Byte],
        (seeded >> 32).asInstanceOf[Byte],
        (seeded >> 40).asInstanceOf[Byte],
        (seeded >> 48).asInstanceOf[Byte],
        (seeded >> 56).asInstanceOf[Byte],
      )

      // Return a pseudorandom unit built from xorshifted fnv1a
      PseudoRandomUnit( fnv1a64(seededOctets) )
    }
  }
}