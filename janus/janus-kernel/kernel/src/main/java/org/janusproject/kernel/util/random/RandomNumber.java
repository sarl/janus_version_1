/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.kernel.util.random;

import java.util.Random;

/**
 * Utility class which is providing a convenient random number generator
 * by automatically creating a random number suite.
 * This utility class provides similar features than the {@link Random}
 * class but with a static access to the functions.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RandomNumber {

	/** Global random number generator.
	 */
	public static final Random RANDOM = new Random();

	/**
	 * Generates random bytes and places them into a user-supplied
	 * byte array.  The number of random bytes produced is equal to
	 * the length of the byte array.
	 *
	 * <p>The method {@code nextBytes} is implemented by class {@code Random}
	 * as if by:
	 *  <pre> <code>
	 * public void nextBytes(byte[] bytes) {
	 *   for (int i = 0; i < bytes.length; )
	 *     for (int rnd = nextInt(), n = Math.min(bytes.length - i, 4);
	 *          n-- > 0; rnd >>= 8)
	 *       bytes[i++] = (byte)rnd;
	 * }</code></pre>
	 *
	 * @param  bytes the byte array to fill with random bytes
	 * @throws NullPointerException if the byte array is null
	 */
	public static void nextBytes(byte[] bytes) {
		RANDOM.nextBytes(bytes);
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed {@code int}
	 * value from this random number generator's sequence. The general
	 * contract of {@code nextInt} is that one {@code int} value is
	 * pseudorandomly generated and returned. All 2<font size="-1"><sup>32
	 * </sup></font> possible {@code int} values are produced with
	 * (approximately) equal probability.
	 *
	 * <p>The method {@code nextInt} is implemented by class {@code Random}
	 * as if by:
	 *  <pre> <code>
	 * public int nextInt() {
	 *   return next(32);
	 * }</code></pre>
	 *
	 * @return the next pseudorandom, uniformly distributed {@code int}
	 *         value from this random number generator's sequence
	 */
	public static int nextInt() {
		return RANDOM.nextInt();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value
	 * between 0 (inclusive) and the specified value (exclusive), drawn from
	 * this random number generator's sequence.  The general contract of
	 * {@code nextInt} is that one {@code int} value in the specified range
	 * is pseudorandomly generated and returned.  All {@code n} possible
	 * {@code int} values are produced with (approximately) equal
	 * probability.  The method {@code nextInt(int n)} is implemented by
	 * class {@code Random} as if by:
	 *  <pre> <code>
	 * public int nextInt(int n) {
	 *   if (n <= 0)
	 *     throw new IllegalArgumentException("n must be positive");
	 *
	 *   if ((n & -n) == n)  // i.e., n is a power of 2
	 *     return (int)((n * (long)next(31)) >> 31);
	 *
	 *   int bits, val;
	 *   do {
	 *       bits = next(31);
	 *       val = bits % n;
	 *   } while (bits - val + (n-1) < 0);
	 *   return val;
	 * }</code></pre>
	 *
	 * <p>The hedge "approximately" is used in the foregoing description only
	 * because the next method is only approximately an unbiased source of
	 * independently chosen bits.  If it were a perfect source of randomly
	 * chosen bits, then the algorithm shown would choose {@code int}
	 * values from the stated range with perfect uniformity.
	 * <p>
	 * The algorithm is slightly tricky.  It rejects values that would result
	 * in an uneven distribution (due to the fact that 2^31 is not divisible
	 * by n). The probability of a value being rejected depends on n.  The
	 * worst case is n=2^30+1, for which the probability of a reject is 1/2,
	 * and the expected number of iterations before the loop terminates is 2.
	 * <p>
	 * The algorithm treats the case where n is a power of two specially: it
	 * returns the correct number of high-order bits from the underlying
	 * pseudo-random number generator.  In the absence of special treatment,
	 * the correct number of <i>low-order</i> bits would be returned.  Linear
	 * congruential pseudo-random number generators such as the one
	 * implemented by this class are known to have short periods in the
	 * sequence of values of their low-order bits.  Thus, this special case
	 * greatly increases the length of the sequence of values returned by
	 * successive calls to this method if n is a small power of two.
	 *
	 * @param n the bound on the random number to be returned.  Must be
	 *	      positive.
	 * @return the next pseudorandom, uniformly distributed {@code int}
	 *         value between {@code 0} (inclusive) and {@code n} (exclusive)
	 *         from this random number generator's sequence
	 * @exception IllegalArgumentException if n is not positive
	 */

	public static int nextInt(int n) {
		return RANDOM.nextInt(n);
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed {@code long}
	 * value from this random number generator's sequence. The general
	 * contract of {@code nextLong} is that one {@code long} value is
	 * pseudorandomly generated and returned.
	 *
	 * <p>The method {@code nextLong} is implemented by class {@code Random}
	 * as if by:
	 *  <pre> <code>
	 * public long nextLong() {
	 *   return ((long)next(32) << 32) + next(32);
	 * }</code></pre>
	 *
	 * Because class {@code Random} uses a seed with only 48 bits,
	 * this algorithm will not return all possible {@code long} values.
	 *
	 * @return the next pseudorandom, uniformly distributed {@code long}
	 *         value from this random number generator's sequence
	 */
	public static long nextLong() {
		return RANDOM.nextLong();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed
	 * {@code boolean} value from this random number generator's
	 * sequence. The general contract of {@code nextBoolean} is that one
	 * {@code boolean} value is pseudorandomly generated and returned.  The
	 * values {@code true} and {@code false} are produced with
	 * (approximately) equal probability.
	 *
	 * <p>The method {@code nextBoolean} is implemented by class {@code Random}
	 * as if by:
	 *  <pre> <code>
	 * public boolean nextBoolean() {
	 *   return next(1) != 0;
	 * }</code></pre>
	 *
	 * @return the next pseudorandom, uniformly distributed
	 *         {@code boolean} value from this random number generator's
	 *	       sequence
	 */
	public static boolean nextBoolean() {
		return RANDOM.nextBoolean();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed {@code float}
	 * value between {@code 0.0} and {@code 1.0} from this random
	 * number generator's sequence.
	 *
	 * <p>The general contract of {@code nextFloat} is that one
	 * {@code float} value, chosen (approximately) uniformly from the
	 * range {@code 0.0f} (inclusive) to {@code 1.0f} (exclusive), is
	 * pseudorandomly generated and returned. All 2<font
	 * size="-1"><sup>24</sup></font> possible {@code float} values
	 * of the form <i>m&nbsp;x&nbsp</i>2<font
	 * size="-1"><sup>-24</sup></font>, where <i>m</i> is a positive
	 * integer less than 2<font size="-1"><sup>24</sup> </font>, are
	 * produced with (approximately) equal probability.
	 *
	 * <p>The method {@code nextFloat} is implemented by class {@code Random}
	 * as if by:
	 *  <pre> <code>
	 * public float nextFloat() {
	 *   return next(24) / ((float)(1 << 24));
	 * }</code></pre>
	 *
	 * <p>The hedge "approximately" is used in the foregoing description only
	 * because the next method is only approximately an unbiased source of
	 * independently chosen bits. If it were a perfect source of randomly
	 * chosen bits, then the algorithm shown would choose {@code float}
	 * values from the stated range with perfect uniformity.<p>
	 * [In early versions of Java, the result was incorrectly calculated as:
	 *  <pre> {@code
	 *   return next(30) / ((float)(1 << 30));}</pre>
	 * This might seem to be equivalent, if not better, but in fact it
	 * introduced a slight nonuniformity because of the bias in the rounding
	 * of floating-point numbers: it was slightly more likely that the
	 * low-order bit of the significand would be 0 than that it would be 1.]
	 *
	 * @return the next pseudorandom, uniformly distributed {@code float}
	 *         value between {@code 0.0} and {@code 1.0} from this
	 *         random number generator's sequence
	 */
	public static float nextFloat() {
		return RANDOM.nextFloat();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed
	 * {@code double} value between {@code 0.0} and
	 * {@code 1.0} from this random number generator's sequence.
	 *
	 * <p>The general contract of {@code nextDouble} is that one
	 * {@code double} value, chosen (approximately) uniformly from the
	 * range {@code 0.0d} (inclusive) to {@code 1.0d} (exclusive), is
	 * pseudorandomly generated and returned.
	 *
	 * <p>The method {@code nextDouble} is implemented by class {@code Random}
	 * as if by:
	 *  <pre><code>
	 * public double nextDouble() {
	 *   return (((long)next(26) << 27) + next(27))
	 *     / (double)(1L << 53);
	 * }<code></pre>
	 *
	 * <p>The hedge "approximately" is used in the foregoing description only
	 * because the {@code next} method is only approximately an unbiased
	 * source of independently chosen bits. If it were a perfect source of
	 * randomly chosen bits, then the algorithm shown would choose
	 * {@code double} values from the stated range with perfect uniformity.
	 * <p>[In early versions of Java, the result was incorrectly calculated as:
	 *  <pre> <code>
	 *   return (((long)next(27) << 27) + next(27))
	 *     / (double)(1L << 54);<code></pre>
	 * This might seem to be equivalent, if not better, but in fact it
	 * introduced a large nonuniformity because of the bias in the rounding
	 * of floating-point numbers: it was three times as likely that the
	 * low-order bit of the significand would be 0 than that it would be 1!
	 * This nonuniformity probably doesn't matter much in practice, but we
	 * strive for perfection.]
	 *
	 * @return the next pseudorandom, uniformly distributed {@code double}
	 *         value between {@code 0.0} and {@code 1.0} from this
	 *         random number generator's sequence
	 * @see Math#random
	 */
	public static double nextDouble() {
		return RANDOM.nextDouble();
	}

	/**
	 * Returns the next pseudorandom, Gaussian ("normally") distributed
	 * {@code double} value with mean {@code 0.0} and standard
	 * deviation {@code 1.0} from this random number generator's sequence.
	 * <p>
	 * The general contract of {@code nextGaussian} is that one
	 * {@code double} value, chosen from (approximately) the usual
	 * normal distribution with mean {@code 0.0} and standard deviation
	 * {@code 1.0}, is pseudorandomly generated and returned.
	 *
	 * <p>The method {@code nextGaussian} is implemented by class
	 * {@code Random} as if by a threadsafe version of the following:
	 *  <pre> <code>
	 * private double nextNextGaussian;
	 * private boolean haveNextNextGaussian = false;
	 *
	 * public double nextGaussian() {
	 *   if (haveNextNextGaussian) {
	 *     haveNextNextGaussian = false;
	 *     return nextNextGaussian;
	 *   } else {
	 *     double v1, v2, s;
	 *     do {
	 *       v1 = 2 * nextDouble() - 1;   // between -1.0 and 1.0
	 *       v2 = 2 * nextDouble() - 1;   // between -1.0 and 1.0
	 *       s = v1 * v1 + v2 * v2;
	 *     } while (s >= 1 || s == 0);
	 *     double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
	 *     nextNextGaussian = v2 * multiplier;
	 *     haveNextNextGaussian = true;
	 *     return v1 * multiplier;
	 *   }
	 * }</code></pre>
	 * This uses the <i>polar method</i> of G. E. P. Box, M. E. Muller, and
	 * G. Marsaglia, as described by Donald E. Knuth in <i>The Art of
	 * Computer Programming</i>, Volume 3: <i>Seminumerical Algorithms</i>,
	 * section 3.4.1, subsection C, algorithm P. Note that it generates two
	 * independent values at the cost of only one call to {@code StrictMath.log}
	 * and one call to {@code StrictMath.sqrt}.
	 *
	 * @return the next pseudorandom, Gaussian ("normally") distributed
	 *         {@code double} value with mean {@code 0.0} and
	 *         standard deviation {@code 1.0} from this random number
	 *         generator's sequence
	 */
	public static double nextGaussian() {
		return RANDOM.nextGaussian();
	}

}
