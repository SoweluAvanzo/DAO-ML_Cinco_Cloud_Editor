// Unit tests: de.jabc.cinco.meta.core.event.test.PriorityTest

package de.jabc.cinco.meta.core.event.hub

import java.util.concurrent.ThreadLocalRandom
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * Depicts a priority.
 * <p>
 * Consists of two numbers, {@link Priority#major major} (set via the
 * {@linkplain Priority#Priority(int) constructor}) and {@link Priority#minor
 * minor} (assigned randomly). When ordering/comparing two Priorities,
 * {@code major} decides their order. If {@code major} of both Priorities is
 * equal, minor is used instead.
 * 
 * @author Fabian Storek
 */
@Accessors(PUBLIC_GETTER)
class Priority implements Comparable<Priority> {
	
	/**
	 * The major priority (set via the {@linkplain Priority#Priority(int)
	 * constructor}).
	 */
	val int major
	
	/**
	 * The minor priority (assigned randomly).
	 */
	var long minor
	
	/**
	 * Priority constructor.
	 * 
	 * @param priority will be assigned to the {@link Priority#major major}
	 *                 priority.
	 */
	new (int priority) {
		this.major = priority
		this.minor = ThreadLocalRandom.current.nextLong
	}
	
	/**
	 * Compares this Priority with the {@code other} Priority for order.
	 * 
	 * @param other The <i>other</i> Priority this Priority will be compared to.
	 * 
	 * @return A negative integer, zero, or a positive integer as this Priority
	 *         is less than, equal to, or greater than the {@code other}
	 *         Priority.
	 */
	override compareTo(Priority other) {
		val diff = this.major.compareTo(other.major)
		if (diff == 0) {
			return this.minor.compareTo(other.minor)
		}
		else {
			return diff
		}
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param other the reference object with which to compare.
	 * 
	 * @return {@code true}, iff the {@code other} object is of type Priority
	 *         and they have the same {@link Priority#major major} and {@link
	 *         Priority#minor minor} priority values.
	 */
	override equals(Object other) {
		if (other instanceof Priority) {
			return this.compareTo(other) == 0
		}
		else {
			return false
		}
	}
	
	/**
	 * Syntactic sugar for {@link Priority#compareTo(Priority) compareTo(Priority)}.
	 * 
	 * @see Priority#compareTo(Priority) compareTo(Priority)
	 */
	def boolean == (Priority other) {
		this.compareTo(other) == 0
	}
	
	/**
	 * Syntactic sugar for {@link Priority#compareTo(Priority) compareTo(Priority)}.
	 * 
	 * @see Priority#compareTo(Priority) compareTo(Priority)
	 */
	def boolean != (Priority other) {
		this.compareTo(other) != 0
	}
	
	/**
	 * Syntactic sugar for {@link Priority#compareTo(Priority) compareTo(Priority)}.
	 * 
	 * @see Priority#compareTo(Priority) compareTo(Priority)
	 */
	def boolean < (Priority other) {
		this.compareTo(other) < 0
	}
	
	/**
	 * Syntactic sugar for {@link Priority#compareTo(Priority) compareTo(Priority)}.
	 * 
	 * @see Priority#compareTo(Priority) compareTo(Priority)
	 */
	def boolean <= (Priority other) {
		this.compareTo(other) <= 0
	}
	
	/**
	 * Syntactic sugar for {@link Priority#compareTo(Priority) compareTo(Priority)}.
	 * 
	 * @see Priority#compareTo(Priority) compareTo(Priority)
	 */
	def boolean > (Priority other) {
		this.compareTo(other) > 0
	}
	
	/**
	 * Syntactic sugar for {@link Priority#compareTo(Priority) compareTo(Priority)}.
	 * 
	 * @see Priority#compareTo(Priority) compareTo(Priority)
	 */
	def boolean >= (Priority other) {
		this.compareTo(other) >= 0
	}
	
	/**
	 * Returns a string representation of the Priority.
	 */
	override toString() {
		'''«class.simpleName»(«major», «minor»)'''
	}
	
	/**
	 * Shuffles the {@link Priority#minor minor} priority value.
	 * (Assigns a new random value to the minor priority value.)
	 */
	def package void shuffle() {
		minor = ThreadLocalRandom.current.nextLong
	}
		
}
