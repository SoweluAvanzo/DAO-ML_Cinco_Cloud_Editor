package de.jabc.cinco.meta.core.event.test

import de.jabc.cinco.meta.core.event.hub.impl.CompositeContext
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link CompositeContext}.
 * @author Fabian Storek
 */
class CompositeContextTest {
	
	@Test
	def void testConstructor() {
		
		val empty = new CompositeContext(null)
		var abc   = new CompositeContext('a.b.c')
		var ab_   = new CompositeContext('a.b.*')
		
		empty.doesEqualCon   (empty)
		empty.doesNotEqualCon(abc)
		abc  .doesEqualCon   (abc)
		abc  .doesNotEqualCon(ab_)
		
		empty.doesEqualStr(null)
		empty.doesEqualStr(''  )
		empty.doesEqualStr('.' )
		empty.doesEqualStr('..')
		
		empty.doesEqualStr(null, null)
		empty.doesEqualStr(null, ''  )
		empty.doesEqualStr(null, '.' )
		empty.doesEqualStr(null, '..')
		
		empty.doesEqualStr('',   null)
		empty.doesEqualStr('',   ''  )
		empty.doesEqualStr('',   '.' )
		empty.doesEqualStr('',   '..')

		empty.doesEqualStr('.',  null)
		empty.doesEqualStr('.',  ''  )
		empty.doesEqualStr('.',  '.' )
		empty.doesEqualStr('.',  '..')

		empty.doesEqualStr('..', null)
		empty.doesEqualStr('..', ''  )
		empty.doesEqualStr('..', '.' )
		empty.doesEqualStr('..', '..')
		
		abc.doesEqualStr('a', 'b', 'c')
		abc.doesEqualStr('a.b', 'c')
		abc.doesEqualStr('a.b.', 'c')
		abc.doesEqualStr('a..b', '.c')
		
		abc.doesEqualStr('a.b.', null, '.c')
		abc.doesEqualStr('a.b.', '', '.c')
		
	}
	
	@Test
	def void testMatches() {
		
		'a.b.c'.doesMatch   ('a.b.c')
		'a.b.c'.doesNotMatch('a.b.d')
		
		'a.b.*'.doesMatch   ('a.b.c')
		'a.b.c'.doesMatch   ('a.b.*')
		
		'a.*.c'.doesMatch   ('a.b.c')
		'a.b.c'.doesMatch   ('a.*.c')
		
		'a.*'  .doesNotMatch('a.b.c')
		'a.b.c'.doesNotMatch('a.*')
		
		'a.*.b'.doesNotMatch('a.b')
		'a.b'  .doesNotMatch('a.*.b')
		
		'a.b.c'.doesNotMatch('a.b')
		'a.b'  .doesNotMatch('a.b.c')
		
		'a.b.*'.doesNotMatch('a.b')
		'a.b'  .doesNotMatch('a.b.*')
		
		'a.b.c'.doesNotMatch('')
		''     .doesNotMatch('a.b.c')
		
		'*'    .doesNotMatch('')
		''     .doesNotMatch('*')
		
	}
	
	@Test
	def void testAppend() {
		
		val empty         = new CompositeContext(null)
		val abc           = new CompositeContext('a.b.c')
		val abcd          = abc.append('d')
		val abcdef        = abc.append('d.e', 'f')
		val abcabcd       = abc.append(abcd)
		val abcabcdabcdef = abc.append(abcd.identifier, abcdef.identifier)
		
		abc          .doesEqualStr('a.b.c')
		abcd         .doesEqualStr('a.b.c.d')
		abcdef       .doesEqualStr('a.b.c.d.e.f')
		abcabcd      .doesEqualStr('a.b.c.a.b.c.d')
		abcabcdabcdef.doesEqualStr('a.b.c.a.b.c.d.a.b.c.d.e.f')
		
		abcabcdabcdef.doesEqualCon(abc.append('a.b.c.d', null, 'a.b.c.d.e.f'))
		abcabcdabcdef.doesEqualCon(abc.append('a.b.c.d', '', 'a.b.c.d.e.f'))
		abcabcdabcdef.doesEqualCon(abc.append('a..b.c.d.', '', '.a.b.', null, 'c..d.e.f.'))
		abcabcdabcdef.doesEqualCon(abc.append(abcd.identifier, null, abcdef.identifier))
		abcabcdabcdef.doesEqualCon(abc.append(abcd.identifier, empty.identifier, abcdef.identifier))
		
	}
	
	@Test
	def void testIterator() {
		
		val abc = #['a', 'b', 'c']
		
		abc.doesEqualIte('a.b.c')
		abc.doesEqualIte('a', 'b', 'c')
		abc.doesEqualIte('a.b', 'c')
		abc.doesEqualIte('a.b.', 'c')
		abc.doesEqualIte('a..b', '.c')
		abc.doesEqualIte('a.b.', null, '.c')
		abc.doesEqualIte('a.b.', '', '.c')
		
		isEmpty   (null)
		isEmpty   ('')
		isNotEmpty('*')
		isNotEmpty('a.b.c')
		isNotEmpty('a.b.c', '*')
		isNotEmpty('a.b.c', null, '*')
		isNotEmpty('a..b', '', '.c.', null, '*.')
		
		0.isLength(null)
		0.isLength('')
		1.isLength('*')
		3.isLength('a.b.c')
		4.isLength('a.b.c', '*')
		4.isLength('a.b.c', null, '*')
		4.isLength('a..b', '', '.c.', null, '*.')
		
		val abc_ = new CompositeContext('a..b', '', '.c.', null, '*.')
		
		assertThrows(IndexOutOfBoundsException) [abc_.get(-1)]
		assertEquals('a', abc_.get(0))
		assertEquals('b', abc_.get(1))
		assertEquals('c', abc_.get(2))
		assertEquals('*', abc_.get(3))
		assertThrows(IndexOutOfBoundsException) [abc_.get(4)]
		
	}
	
	@Test
	def void testGetIdentifier() {
		
		''       .isString(null)
		''       .isString('')
		'a.b.c'  .isString('a', 'b', 'c')
		'a.b.c'  .isString('a.b.c')
		'a.b.c.*'.isString('a.b.c', '*')
		'a.b.c.*'.isString('a.b.c', null, '*')
		'a.b.c.*'.isString('a.b', '', '.c', null, '*')
		'a.b.c.*'.isString('.a..b.', '.', '.c', null, '.*')
		
	}
	
	
	
	def private void doesEqualStr(CompositeContext left, String ... right) {
		val r = new CompositeContext(right)
		assertTrue(left == r)
		assertTrue(left.equals(r))
		assertTrue(r == left)
		assertTrue(r.equals(left))
	}
		
	def private void doesEqualCon(CompositeContext left, CompositeContext right) {
		assertTrue(left == right)
		assertTrue(left.equals(right))
		assertTrue(right == left)
		assertTrue(right.equals(left))
	}
	
	def private void doesNotEqualCon(CompositeContext left, CompositeContext right) {
		assertFalse(left == right)
		assertFalse(left.equals(right))
		assertFalse(right == left)
		assertFalse(right.equals(left))
	}
	
	def private void doesEqualIte(Iterable<String> left, String ... right) {
		assertIterableEquals(left, new CompositeContext(right))
	}
	
	def private void isEmpty(String ... context) {
		assertTrue(new CompositeContext(context).empty)
	}
	
	def private void isNotEmpty(String ... context) {
		assertFalse(new CompositeContext(context).empty)
	}
	
	def private void isLength(int length, String ... context) {
		assertEquals(length, new CompositeContext(context).length)
	}
	
	def private void isString(String str, String ... context) {
		assertEquals(str, new CompositeContext(context).identifier)
	}
	
		
	def private void doesMatch(String left, String right) {
		val l = new CompositeContext(left)
		val r = new CompositeContext(right)
		assertTrue(l.matches(r))
		assertTrue(r.matches(l))
	}
	
	def private void doesNotMatch(String left, String right) {
		val l = new CompositeContext(left)
		val r = new CompositeContext(right)
		assertFalse(l.matches(r))
		assertFalse(r.matches(l))
	}
	
}
