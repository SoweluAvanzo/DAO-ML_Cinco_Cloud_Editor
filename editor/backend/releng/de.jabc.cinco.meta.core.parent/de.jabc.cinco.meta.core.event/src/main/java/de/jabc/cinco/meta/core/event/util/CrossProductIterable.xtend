package de.jabc.cinco.meta.core.event.util

import de.jabc.cinco.meta.core.event.util.CrossProductIterable.Pair
import java.util.Iterator
import org.eclipse.xtend.lib.annotations.Data
import java.util.NoSuchElementException

/**
 * An Iterable, that combines two other Iterables in a cross product fashion.
 * <p>
 * <b>Example:</b>
 * <pre>
 * new CrossProductIterable(
 *     #[4, 3, 2, 1, 0],
 *     #[a, b, c]
 * )
 * </pre>
 * results in
 * <pre>
 * #[
 *     (4, a), (4, b), (4, c),
 *     (3, a), (3, b), (3, c),
 *     (2, a), (2, b), (2, c),
 *     (1, a), (1, b), (1, c),
 *     (0, a), (0, b), (0, c)
 * ]
 * </pre>
 * 
 * @author Fabian Storek
 */
class CrossProductIterable<L, R> implements Iterable<Pair<L, R>> {
	
	val Iterable<L> left
	val Iterable<R> right
	
	new(Iterable<L> left, Iterable<R> right) {
		this.left  = left
		this.right = right
	}
	
	override iterator() {
		new CrossProductIterator(this)
	}
	
	static class CrossProductIterator<L, R> implements Iterator<Pair<L, R>> {
		
		val Iterable<L> leftIterable
		val Iterable<R> rightIterable
		var Iterator<L> leftIterator
		var Iterator<R> rightIterator
		var int index
		var int leftIndex
		var int rightIndex
		var L leftItem
		var R rightItem
		val boolean isEmpty
		
		new(CrossProductIterable<L, R> iterable) {
			this.leftIterable  = iterable.left
			this.rightIterable = iterable.right
			this.leftIterator  = leftIterable.iterator
			this.rightIterator = rightIterable.iterator
			this.index         = -1
			this.leftIndex     = -1
			this.rightIndex    = -1
			this.leftItem      = null
			this.rightItem     = null
			this.isEmpty       = !leftIterator.hasNext || !rightIterator.hasNext
		}
		
		override hasNext() {
			!isEmpty && (leftIterator.hasNext || rightIterator.hasNext)
		}
		
		override next() {
			if (!hasNext) {
				throw new NoSuchElementException
			}
			else if (index == -1) {
				index      += 1
				leftIndex  += 1
				rightIndex += 1
				leftItem    = leftIterator.next
				rightItem   = rightIterator.next
			}
			else {
				if (!rightIterator.hasNext) {
					leftItem      = leftIterator.next
					leftIndex    += 1
					rightIterator = rightIterable.iterator
					rightIndex    = -1
				}
				rightItem   = rightIterator.next
				rightIndex += 1
				index      += 1
			}
			new Pair(leftItem, rightItem, index, leftIndex, rightIndex)
		}
		
	}
	
	/**
	 * The element type of a {@link CrossProductIterable}. 
	 * <p>
	 * Contains the {@linkplain CrossProductIterable.Pair#getLeft() left} and
	 * {@linkplain CrossProductIterable.Pair#getRight() right} side of the
	 * element, as well several indices:
	 * <ul>
	 * <li>{@linkplain CrossProductIterable.Pair#getIndex() index}:
	 *     The overall index in this CrossProductIterable.</li>
	 * <li>{@linkplain CrossProductIterable.Pair#getLeftIndex() leftIndex}:
	 *     The index of the left element.</li>
	 * <li>{@linkplain CrossProductIterable.Pair#getRightIndex() rightIndex}:
	 *     The index of the right element.</li>
	 * </ul>
	 */
	@Data
	static class Pair<L, R> {
		
		val L left
		val R right
		val int index
		val int leftIndex
		val int rightIndex
		
	}
	
}
