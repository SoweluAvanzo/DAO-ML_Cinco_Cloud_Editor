// Unit tests: de.jabc.cinco.meta.plugin.event.api.test.FqnTest

package de.jabc.cinco.meta.plugin.event.api.util

import java.lang.reflect.Type
import java.util.regex.Pattern
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * A class representing a fully qualified name (FQN) in Java.
 * <p>
 * Accepts Strings and Class objects as input and provides access to all major
 * building blocks of the FQN.
 * <p>
 * <b>Example:</b><br>
 * <pre>
 * val fqn = new Fqn("java.util.Map&lt;foo.Foo, ? extends Bar&gt;")
 * fqn.fullyQualifiedName == "java.util.Map"
 * fqn.packageName        == "java.util"
 * fqn.className          == "Map"
 * fqn.generics           == #[ new Fqn("foo.Foo"),
 *                              new Fqn("? extends Bar") ]
 * fqn.extendsConstraint  == null
 * fqn.superConstraint    == null
 * </pre> 
 * 
 * @author Fabian Storek
 */
@Accessors(PUBLIC_GETTER)
class Fqn implements Cloneable {
	
	
	
	/*** Statics ***/
	
	/**
	 * The regex pattern for identifying the primary FQN, the constraint
	 * ({@code extends} or {@code super}) and the secondary FQN.
	 * <p>
	 * <b>Example 1:</b><br>
	 * {@code "T extends foo.Foo<bar.Bar>"} splits into
	 * <pre>
	 * primary:    T
	 * constraint: extends
	 * secondary:  foo.Foo&lt;bar.Bar&gt;
	 * </pre>
	 * <p>
	 * <b>Example 2:</b><br>
	 * {@code "foo.Foo<bar.Bar>"} splits into
	 * <pre>
	 * primary:    "foo.Foo&lt;bar.Bar&gt;"
	 * constraint: null
	 * secondary:  null
	 * </pre>
	 */
	val static Pattern CONSTRAINT_PATTERN = Pattern.compile('''^ ?(?<primary>\?|[a-zA-Z0-9_.]+( ?<.*?>)?)( (?<constraint>extends|super) (?<secondary>[a-zA-Z0-9_.]+( ?<.*?>)?))? ?$''')
	
	/**
	 * The regex pattern for identifying the FQN and generics.
	 * <p>
	 * <b>Example 1:</b><br>
	 * {@code "foo.Foo<bar.Bar, bar.Bar>"} splits into
	 * <pre>
	 * fqn:         "foo.Foo"
	 * generics:    "bar.Bar, bar.Bar"
	 * </pre>
	 * <p>
	 * <b>Example 2:</b><br>
	 * {@code "foo.Foo<>"} splits into
	 * <pre>
	 * fqn:         "foo.Foo"
	 * generics:    ""
	 * </pre>
	 * <p>
	 * <b>Example 3:</b><br>
	 * {@code "foo.Foo"} splits into
	 * <pre>
	 * fqn:         "foo.Foo"
	 * generics:    null
	 * </pre>
	 */
	val static Pattern GENERICS_PATTERN = Pattern.compile('''^(?<fqn>\?|[a-zA-Z0-9_.]+)( ?< ?(?<generics>.*?) ?>)?$''')
	
	/**
	 * The regex pattern for identifying the package and class.
	 * <p>
	 * <b>Example 1:</b><br>
	 * {@code "foo.bar.Foo"} splits into
	 * <pre>
	 * package: "foo.bar"
	 * class:   "Foo"
	 * </pre>
	 * <p>
	 * <b>Example 2:</b><br>
	 * {@code "Foo"} splits into
	 * <pre>
	 * package: null
	 * class:   "Foo"
	 * </pre>
	 */
	val static Pattern FQN_PATTERN = Pattern.compile('''^((?<package>[a-zA-Z_][a-zA-Z0-9_]*(\.[a-zA-Z_][a-zA-Z0-9_]*)*)\.)?(?<class>\?|[a-zA-Z_][a-zA-Z0-9_]*)$''')
	
	
	
	/*** Attributes ***/
	
	/**
	 * The fully qualified name without any generics nor constraints.
	 */
	var String fullyQualifiedName
	
	/**
	 * The full name of package (without tailing '.'). May be {@code null}.
	 */
	var String packageName
	
	/**
	 * The class name.
	 */
	var String className
	
	/**
	 * An array of {@link Fqn} objects, that are the generics of this FQN.
	 * <p>
	 * Is empty, if the FQN uses the diamond operator (e. g. {@code
	 * java.util.List<>}).
	 * <p>
	 * Is {@code null}, if the FQN has no generics (e. g. {@code
	 * java.util.List}).
	 */
	var Fqn[] generics
	
	/**
	 * An {@link Fqn} object that indicates the {@code extends} constraint.
	 * <p>
	 * Is {@code null}, if this FQN has no {@code extends} constraint.
	 * <p>
	 * <b>Example 1:</b><br>
	 * The {@code extendsConstraint} of {@code "T extends List<Integer>"} is
	 * {@code "List<Integer>"}.
	 * <p>
	 * <b>Example 2:</b><br>
	 * The {@code extendsConstraint} of {@code "List<Integer>"} is {@code
	 * null}.
	 */
	var Fqn extendsConstraint
	
	/**
	 * An {@link Fqn} object that indicates the {@code super} constraint.
	 * <p>
	 * Is {@code null}, if this FQN has no {@code super} constraint.
	 * <p>
	 * <b>Example 1:</b><br>
	 * The {@code superConstraint} of {@code "T super List<Integer>"} is
	 * {@code "List<Integer>"}.
	 * <p>
	 * <b>Example 2:</b><br>
	 * The {@code superConstraint} of {@code "List<Integer>"} is {@code
	 * null}.
	 */
	var Fqn superConstraint
	
	
	
	/*** Constructors ***/
	
	/**
	 * Fqn constructor.
	 * 
	 * @param fqn The FQN that will be parsed into an {@link Fqn} object.
	 * 
	 * @throws NullPointerException if parameter {@code fqn} is {@code null}
	 * @throws FqnFormatException   if the FQN could not be parsed or is not a
	 *                              valid FQN.
	 * 
	 * @see Fqn#Fqn(Type) Fqn(Type)
	 */
	new (CharSequence fqn) {
		
		if (fqn === null) {
			throw new NullPointerException('The parameter "fqn" may not be null!')
		}
		
		try {
		
			// Replace consecutive whitespace with a single space and "$" with "."
			val cleanFqn = fqn
				.toString
				.replaceAll('''\s+''', ' ')
				.replaceAll('''\$''', '.')
			
			// Split clean FQN into primary FQN, constraint and secondary FQN
			val constraintMatcher = CONSTRAINT_PATTERN.matcher(cleanFqn)
			if (!constraintMatcher.matches) {
				throw new FqnFormatException('''Constraint regex did not match FQN "«fqn»".''')
			}
			val primaryFqn   = constraintMatcher.group('primary')
			val constraint   = constraintMatcher.group('constraint')
			val secondaryFqn = constraintMatcher.group('secondary')
			
			// Split primary FQN into fully qualified name and generics
			val genericsMatcher = GENERICS_PATTERN.matcher(primaryFqn)
			if (!genericsMatcher.matches) {
				throw new FqnFormatException('''Generics regex did not match FQN "«fqn»".''')
			}
			this.fullyQualifiedName = genericsMatcher.group('fqn')
			val generics = genericsMatcher.group('generics')
			
			// Split fully qualified name into package and class
			if (fullyQualifiedName == '?') {
				this.packageName = null
				this.className   = '?'
			}
			else {
				val fqnMatcher = FQN_PATTERN.matcher(fullyQualifiedName)
				if (!fqnMatcher.matches) {
					throw new FqnFormatException('''FQN regex did not match FQN "«fqn»".''')
				}
				this.packageName = fqnMatcher.group('package')
				this.className   = fqnMatcher.group('class')
			}
		
			// Create FQNs for each generic (if present)
			this.generics = switch it: generics {
				case null:  null
				case empty: #[]
				default:    splitGenerics.map[new Fqn(it)]
			}
			
			// Create FQN for "extends" or "super" constraint (if present)
			switch constraint {
				case 'extends': {
					this.extendsConstraint = new Fqn(secondaryFqn)
					this.superConstraint   = null
				}
				case 'super': {
					this.extendsConstraint = null
					this.superConstraint   = new Fqn(secondaryFqn)
				}
				default: {
					this.extendsConstraint = null
					this.superConstraint   = null
				}
			}
		
			// Check for inconsistencies
			if (hasGenerics && hasConstraint) {
				throw new FqnFormatException('''FQN "«fqn»" must not have generics and a constraint at the same time.''')
			}
			if (isWildcard && hasGenerics) {
				throw new FqnFormatException('''FQN "«fqn»" must not be a wildcard and have generics at the same time.''')
			}
			
		}
		catch (Exception e) {
			throw new FqnFormatException(e, fqn)
		}
		
	}
	
	/**
	 * Fqn constructor.
	 * 
	 * @param type The type or class that will be parsed into an {@link Fqn}
	 * object.
	 * 
	 * @throws NullPointerException if parameter {@code type} is {@code null}
	 * @throws FqnFormatException   if the FQN could not be parsed or is not a
	 *                              valid FQN.
	 * 
	 * @see Fqn#Fqn(CharSequence) Fqn(CharSequence)
	 */
	new (Type type) {
		
		if (type === null) {
			throw new NullPointerException('The parameter "type" may not be null.')
		}
		
		val fqn                 = new Fqn(type.typeName)
		this.fullyQualifiedName = fqn.fullyQualifiedName
		this.packageName        = fqn.packageName
		this.className          = fqn.className
		this.generics           = fqn.generics
		this.extendsConstraint  = fqn.extendsConstraint
		this.superConstraint    = fqn.superConstraint
		
	}
	
	/**
	 * A private help method for splitting up a String of comma-separated FQNs.
	 * <p>
	 * Will only split the top-most level of FQNs.
	 * <p>
	 * <b>Example:</b><br>
	 * {@code "FooBar<Foo, Bar<?>>, Foo, FooFooBar<Foo, Foo, Bar<?>>"} will split into
	 * <pre>
	 * #[
	 *     "FooBar&lt;Foo, Bar&lt;?&gt;&gt;",
	 *     " Foo",
	 *     " FooFooBar&lt;Foo, Foo, Bar&lt;?&gt;&gt;"
	 * ]
	 * </pre>
	 */
	def private String[] splitGenerics(String generics) {
		val separatorIndices = newArrayList(-1)
		var openBrackets = 0
		for (pair: generics.toCharArray.indexed) {
			switch pair.value.toString {
				case '<': openBrackets += 1
				case '>': openBrackets -= 1
				case ',': if (openBrackets == 0) separatorIndices.add(pair.key)
			}
		}
		separatorIndices.add(generics.length)
		val result = newArrayOfSize(separatorIndices.size - 1)
		for (i: 0 ..< separatorIndices.size - 1) {
			val start = separatorIndices.get(i) + 1
			val end   = separatorIndices.get(i + 1)
			val substring = generics.substring(start, end)
			result.set(i, substring)
		}
		return result
	}
	
	/**
	 * Creates and returns a copy of this FQN.
	 */
	override Fqn clone() {
		new Fqn(fullyQualifiedNameWithSuffix)
	}
	
	
	
	/*** Mutating methods ***/
	
	/**
	 * Sets the {@link Fqn#fullyQualifiedName fullyQualifiedName}.
	 * <p>
	 * Changing the {@code fullyQualifiedName} might also change {@link
	 * Fqn#packageName packageName}, {@link Fqn#className className} and {@link
	 * Fqn#generics generics}.
	 * 
	 * @param newValue The new value for the {@code fullyQualifiedName}.
	 * 
	 * @throws NullPointerException if {@code newValue} is {@code null}.
	 * @throws FqnFormatException   if {@code newValue} empty or an invalid
	 *                              FQN.
	 */
	def void setFullyQualifiedName(String newValue) {
		if (newValue === null) {
			throw new NullPointerException('The fully qualified name may not be null.')
		}
		else if (newValue.empty) {
			throw new FqnFormatException('The fully qualified name may not be empty.')
		}
		else if (newValue == '?') {
			fullyQualifiedName = '?'
			packageName        = null
			className          = '?'
			generics           = null
		}
		else {
			val fqnMatcher = FQN_PATTERN.matcher(newValue)
			if (!fqnMatcher.matches) {
				throw new FqnFormatException('''FQN regex did not match FQN "«newValue»".''')
			}
			fullyQualifiedName = newValue
			packageName        = fqnMatcher.group('package')
			className          = fqnMatcher.group('class')
		}
	}
	
	/**
	 * Sets the {@link Fqn#packageName packageName}.
	 * <p>
	 * Changing the {@code packageName} might also change {@link
	 * Fqn#fullyQualifiedName fullyQualifiedName}.
	 * 
	 * @param newValue The new value for the {@code packageName}.
	 * 
	 * @throws FqnFormatException if {@code newValue} not a valid package name
	 *                            or this FQN {@linkplain Fqn#isWildcard() is a wildcard}.
	 */
	def void setPackageName(String newValue) {
		if (newValue.nullOrEmpty) {
			fullyQualifiedName = className
			packageName        = null
		}
		else if (isWildcard) {
			throw new FqnFormatException('The package name must be null, because this FQN is a wildcard ("?").')
		}
		else {
			setFullyQualifiedName('''«newValue».«className»''')
		}
	}
	
	/**
	 * Sets the {@link Fqn#className className}.
	 * <p>
	 * Changing the {@code className} might also change {@link
	 * Fqn#fullyQualifiedName fullyQualifiedName}, {@link Fqn#packageName
	 * packageName} and {@link Fqn#generics generics}.
	 * 
	 * @param newValue The new value for the {@code className}.
	 * 
	 * @throws NullPointerException if {@code newValue} is {@code null}.
	 * @throws FqnFormatException   if {@code newValue} is empty or an invalid
	 *                              class name.
	 */
	def void setClassName(String newValue) {
		if (newValue === null) {
			throw new NullPointerException('The class name may not be null.')
		}
		else if (newValue.empty) {
			throw new FqnFormatException('The class name may not be empty.')
		}
		else if (newValue == '?') {
			setFullyQualifiedName('?')
		}
		else {
			setFullyQualifiedName('''«packageName».«newValue»''')
		}
	}
	
	/**
	 * Sets the {@link Fqn#generics generics}.
	 * <p>
	 * Changing the {@code generics} might also change {@link
	 * Fqn#extendsConstraint extendsConstraint} and {@link Fqn#superConstraint
	 * superConstraint}.
	 * 
	 * @param newValue The new value for the {@code generics}.
	 * 
	 * @throws FqnFormatException if this FQN is a {@linkplain Fqn#isWildcard()
	 *                            wildcard} or {@code newValue} contains
	 *                            {@linkplain Fqn#isPrimitive() primitive types}.
	 */
	def void setGenerics(Fqn[] newValue) {
		if (newValue === null) {
			generics = null
		}
		else if (isWildcard) {
			throw new FqnFormatException('The generics must be null, because this FQN is a wildcard ("?").')
		}
		else if (newValue.exists[isPrimitive]) {
			throw new FqnFormatException('The generics must not contain primitive types.')
		}
		else {
			generics          = newValue
			extendsConstraint = null
			superConstraint   = null
		}
	}
	
	/**
	 * Sets the {@link Fqn#extendsConstraint extendsConstraint}.
	 * <p>
	 * Changing the {@code extendsConstraint} might also change {@link
	 * Fqn#generics generics} and {@link Fqn#superConstraint superConstraint}.
	 * 
	 * @param newValue The new value for the {@code extendsConstraint}.
	 * 
	 * @throws FqnFormatException if this FQN is a {@linkplain Fqn#isWildcard()
	 *                            wildcard} or a {@linkplain Fqn#isPrimitive()
	 *                            primitive type}.
	 */
	def void setExtendsConstraint(Fqn newValue) {
		if (newValue === null) {
			extendsConstraint = null
		}
		else if (newValue.isWildcard) {
			throw new FqnFormatException('The constraint must not be a wildcard ("?").')
		}
		else if (newValue.isPrimitive) {
			throw new FqnFormatException('The constraint must not be a primitive type.')
		}
		else if (newValue.hasConstraint) {
			throw new FqnFormatException('The constraint must not have a constraint itself.')
		}
		else {
			generics          = null
			extendsConstraint = newValue
			superConstraint   = null
		}
	}
	
	/**
	 * Sets the {@link Fqn#superConstraint superConstraint}.
	 * <p>
	 * Changing the {@code superConstraint} might also change {@link
	 * Fqn#generics generics} and {@link Fqn#extendsConstraint
	 * extendsConstraint}.
	 * 
	 * @param newValue The new value for the {@code superConstraint}.
	 * 
	 * @throws FqnFormatException if this FQN is a {@linkplain Fqn#isWildcard()
	 *                            wildcard} or a {@linkplain Fqn#isPrimitive()
	 *                            primitive type}.
	 */
	def void setSuperConstraint(Fqn newValue) {
		if (newValue === null) {
			superConstraint = null
		}
		else if (newValue.isWildcard) {
			throw new FqnFormatException('The constraint must not be a wildcard ("?").')
		}
		else if (newValue.isPrimitive) {
			throw new FqnFormatException('The constraint must not be a primitive type.')
		}
		else if (newValue.hasConstraint) {
			throw new FqnFormatException('The constraint must not have a constraint itself.')
		}
		else {
			generics          = null
			extendsConstraint = null
			superConstraint   = newValue
		}
	}
	
	/**
	 * Replaces all occurrences of the {@link Fqn#fullyQualifiedName
	 * fullyQualifiedName} equal to {@code search} with {@code replacement}.
	 * This includes references to other FQN objects as in {@link
	 * Fqn#generics generics}, {@link Fqn#extendsConstraint extendsConstraint}
	 * and {@link Fqn#superConstraint superConstraint}.
	 * 
	 * @param search      the fully qualified name, that will be replaced.
	 * @param replacement the new value for the fully qualified name of
	 *                    matching FQNs.
	 * 
	 * @throws NullPointerException if {@code replacement} is {@code null}.
	 * @throws FqnFormatException   if {@code replacement} empty or an invalid
	 *                              FQN.
	 */
	def void replaceAll(String search, String replacement) {
		if (fullyQualifiedName == search) {
			setFullyQualifiedName(replacement)
		}
		generics?.forEach [ replaceAll(search, replacement) ]
		extendsConstraint?.replaceAll(search, replacement)
		superConstraint?.replaceAll(search, replacement)
	}
	
	
	
	/*** Computed values ***/
	
	/**
	 * Returns the fully qualified name as a String, including its {@link
	 * Fqn#generics generics}, {@link Fqn#extendsConstraint extends} or {@link
	 * Fqn#superConstraint super} constraint.
	 */
	def String getFullyQualifiedNameWithSuffix() {
		'''«fullyQualifiedName»«genericsSuffix»«constraintSuffix»'''
	}
	
	/**
	 * Returns the fully qualified name as a String, including its {@link
	 * Fqn#generics generics}, but omitting its {@link Fqn#extendsConstraint
	 * extends} / {@link Fqn#superConstraint super} constraint.
	 */
	def String getFullyQualifiedNameWithGenerics() {
		'''«fullyQualifiedName»«genericsSuffix»'''
	}
	
	/**
	 * Returns the fully qualified name as a String, including its {@link
	 * Fqn#extendsConstraint extends} / {@link Fqn#superConstraint super}
	 * constraint, but omitting its {@link Fqn#generics generics}.
	 */
	def String getFullyQualifiedNameWithConstraint() {
		'''«fullyQualifiedName»«constraintSuffix»'''
	}
	
	/**
	 * Returns the {@link Fqn#generics generics} as a String.
	 * <p>
	 * They are separated by a comma and space ({@code ", "}) and they are
	 * surrounded by angle brackets ({@code "<...>"}).
	 * <p>
	 * If this FQN {@linkplain Fqn#hasGenerics() has no generics}, the result
	 * will be an empty String.
	 * <p>
	 * If this FQN {@linkplain Fqn#hasDiamond() utilizes the diamond operator},
	 * the diamond operator ({@code "<>"}) will be returned.
	 */
	def String getGenericsSuffix() {
		if (hasGenerics) {
			'''<«generics.join(', ') [ fullyQualifiedNameWithSuffix ]»>'''
		}
		else {
			''
		}
	}
	
	/**
	 * Returns the {@link Fqn#extendsConstraint extends} / {@link
	 * Fqn#superConstraint super} constraint as a String.
	 * <p>
	 * It is prefixed by a space and the corresponding keyword ({@code
	 * " extends ConstraintFqn"} or {@code " super ConstraintFqn"})
	 * <p>
	 * If this FQN {@linkplain Fqn#hasConstraint() has no constraint}, the
	 * result will be an empty String.
	 */
	def String getConstraintSuffix() {
		if (hasExtendsConstraint) {
			''' extends «extendsConstraint.fullyQualifiedNameWithSuffix»'''
		}
		else if (hasSuperConstraint) {
			''' super «superConstraint.fullyQualifiedNameWithSuffix»'''
		}
		else {
			''
		}
	}
	
	/**
	 * Checks if the FQN has a {@link Fqn#packageName packageName}.
	 */
	def boolean hasPackageName() {
		!packageName.nullOrEmpty
	}
	
	/**
	 * Checks if the FQN is a type wildcard. ("{@code ?}" in "{@code ? extends Foo}")
	 */
	def boolean isWildcard() {
		className == '?'
	}
	
	/**
	 * Checks if the FQN has {@link Fqn#generics generics}.
	 */
	def boolean hasGenerics() {
		generics !== null
	}
	
	/**
	 * Checks if the FQN utilizes the diamond operator ({@code "<>"}).
	 */
	def boolean hasDiamond() {
		hasGenerics && generics.empty
	}
	
	/**
	 * Checks if the FQN has an {@link Fqn#extendsConstraint extends} or {@link
	 * Fqn#superConstraint super} constraint.
	 */
	def boolean hasConstraint() {
		hasExtendsConstraint || hasSuperConstraint
	}
	
	/**
	 * Checks if the FQN has an {@link Fqn#extendsConstraint extends}
	 * constraint.
	 */
	def boolean hasExtendsConstraint() {
		extendsConstraint !== null
	}
	
	/**
	 * Checks if the FQN has a {@link Fqn#superConstraint super} constraint.
	 */
	def boolean hasSuperConstraint() {
		superConstraint !== null
	}
	
	/**
	 * Checks if the FQN is the {@code java.lang} package.
	 */
	def boolean isJavaLangPackage() {
		packageName == 'java.lang'
	}
	
	/**
	 * Checks if the FQN is a Java primitive.
	 */
	def boolean isPrimitive() {
		val primitives = #[
			'byte',  'short',  'int',  'long',
			'float', 'double', 'char', 'boolean',
			'void'
		]
		primitives.contains(fullyQualifiedName)
	}
	
	/**
	 * Checks if the FQN is a wrapper of a Java primitive.
	 */
	def boolean isPrimitiveWrapper() {
		val primitiveWrappers = #[
			'Byte',  'Short',  'Integer',   'Long',
			'Float', 'Double', 'Character', 'Boolean',
			'Void'
		]
		primitiveWrappers.contains(className) &&
		(packageName.nullOrEmpty || isJavaLangPackage)
	}
	
	/**
	 * Returns the wrapper of primitive types as an FQN. Returns a clone of
	 * this FQN, if this FQN is not a primitive type.
	 */
	def Fqn wrapped() {
		if (isPrimitive) {
			switch className {
				case 'byte':    new Fqn(Byte)
				case 'short':   new Fqn(Short)
				case 'int':     new Fqn(Integer)
				case 'long':    new Fqn(Long)
				case 'float':   new Fqn(Float)
				case 'double':  new Fqn(Double)
				case 'char':    new Fqn(Character)
				case 'boolean': new Fqn(Boolean)
				case 'void':    new Fqn(Void)
			}
		}
		else {
			clone
		}
	}
	
	/**
	 * Returns the unwrapped primitive type of wrapper classes as an FQN.
	 * Returns a clone of this FQN, if this FQN is not a primitive wrapper.
	 */
	def Fqn unwrapped() {
		if (isPrimitiveWrapper) {
			switch className {
				case 'Byte':      new Fqn(byte)
				case 'Short':     new Fqn(short)
				case 'Integer':   new Fqn(int)
				case 'Long':      new Fqn(long)
				case 'Float':     new Fqn(float)
				case 'Double':    new Fqn(double)
				case 'Character': new Fqn(char)
				case 'Boolean':   new Fqn(boolean)
				case 'Void':      new Fqn(void)
			}
		}
		else {
			clone
		}
	}
	
	/**
	 * Checks for inconsistencies in this FQN.
	 * 
	 * @return {@code true} if the check was successful, thus no
	 *         inconsistencies.
	 */
	def boolean check() {
		isWildcard => (
			!hasPackageName &&
			!hasGenerics
		) &&
		(isPrimitive || isPrimitiveWrapper) => (
			!hasGenerics &&
			!hasConstraint
		) &&
		hasGenerics => (
			!isWildcard                      &&
			!hasConstraint                   &&
			generics.forall [ !isPrimitive ] &&
			generics.forall [ check ]
		) &&
		hasExtendsConstraint => (
			!hasGenerics                     &&
			!hasSuperConstraint              &&
			!extendsConstraint.isWildcard    &&
			!extendsConstraint.isPrimitive   &&
			!extendsConstraint.hasConstraint &&
			extendsConstraint.check
		) &&
		hasSuperConstraint => (
			!hasGenerics                   &&
			!hasExtendsConstraint          &&
			!superConstraint.isWildcard    &&
			!superConstraint.isPrimitive   &&
			!superConstraint.hasConstraint &&
			superConstraint.check
		)
	}
	
	/**
	 * Logical consequence / entailment.
	 * <p>
	 * Precedence from lowest to highest:
	 * <pre>
	 * ||
	 * &&
	 * ==, !=, ===, !==
	 * <, >, <=, >=
	 * =>
	 * +, -
	 * *, /, %, **
	 * !, +, - (unary)
	 * </pre>
	 */
	def private boolean => (boolean left, boolean right) {
		(!left) || right
	}
	
	
	
	/*** Exception class ***/
	
	/**
	 * FQN is not formatted correctly.
	 */
	@Accessors
	static class FqnFormatException extends Exception {
		
		val Exception originalException
		
		new (String message) {
			super(message)
			this.originalException = this
		}
		
		new (Exception originalException, CharSequence fqn) {
			super('''
				Could not parse FQN "«fqn»" because of a subseqent exception:
					«if (originalException instanceof FqnFormatException) {
						originalException.originalException.message
					}
					else {
						originalException.message
					}»
			''')
			if (originalException instanceof FqnFormatException) {
				this.originalException = originalException.originalException
			}
			else {
				this.originalException = originalException
			}
		}
		
	}
	
}
