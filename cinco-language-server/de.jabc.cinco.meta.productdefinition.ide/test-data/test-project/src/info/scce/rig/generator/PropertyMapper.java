package info.scce.rig.generator;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import graphmodel.ModelElementContainer;
import info.scce.rig.pipeline.BooleanAssignment;
import info.scce.rig.pipeline.BooleanParameter;
import info.scce.rig.pipeline.BooleanProperty;
import info.scce.rig.pipeline.BooleanVariable;
import info.scce.rig.pipeline.IntAssignment;
import info.scce.rig.pipeline.IntParameter;
import info.scce.rig.pipeline.IntProperty;
import info.scce.rig.pipeline.IntVariable;
import info.scce.rig.pipeline.Property;
import info.scce.rig.pipeline.StringAssignment;
import info.scce.rig.pipeline.StringParameter;
import info.scce.rig.pipeline.StringProperty;
import info.scce.rig.pipeline.StringVariable;
import info.scce.rig.pipeline.Target;

public class PropertyMapper<C extends ModelElementContainer> {
	
	private final C subject;
	private final Target target;
	
	PropertyMapper (C container, Target target) {
		this.subject = container;
		this.target = target;
	}
	
	public <P extends StringProperty> Optional<String> 
	stringValue(Class<P> property) {
		return _stringValues(property).findFirst();
	}
	
	public <P extends StringProperty> List<String> 
	stringValues(Class<P> property) {
		return _stringValues(property).collect(Collectors.toList());
	}
	
	private <P extends StringProperty> Stream<String> 
	_stringValues(Class<P> property) {
		
		Stream<String> variables = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(StringAssignment.class).stream())
				.map(StringAssignment::getSourceElement)
				.filter(node -> node instanceof StringVariable)
				.map(StringVariable.class::cast)
				.map(StringVariable::getValue);
		
		Stream<String> parameters = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(StringAssignment.class).stream())
				.map(StringAssignment::getSourceElement)
				.filter(node -> node instanceof StringParameter)
				.map(StringParameter.class::cast)
				.filter(parameter -> parameter.getContainer() == target)
				.map(StringParameter::getValue);
		
		Stream<String> values = subject.getNodes(property).stream()
				.filter(p -> p.getIncoming().size() == 0)
				.map(StringProperty::getValue);
		
		return Stream.of(parameters, variables, values)
				.reduce(Stream::concat)
				.orElseGet(Stream::empty);
	}
	
	public <P extends BooleanProperty> Optional<Boolean> 
	booleanValue(Class<P> property) {
		return _booleanValues(property).findFirst();
	}
	
	public <P extends BooleanProperty> List<Boolean> 
	booleanValues(Class<P> property) {
		return _booleanValues(property).collect(Collectors.toList());
	}
	
	private <P extends BooleanProperty> Stream<Boolean> 
	_booleanValues(Class<P> property) {
		
		Stream<Boolean> variables = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(BooleanAssignment.class).stream())
				.map(BooleanAssignment::getSourceElement)
				.filter(node -> node instanceof BooleanVariable)
				.map(BooleanVariable.class::cast)
				.map(BooleanVariable::isValue);
		
		Stream<Boolean> parameters = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(BooleanAssignment.class).stream())
				.map(BooleanAssignment::getSourceElement)
				.filter(node -> node instanceof BooleanParameter)
				.map(BooleanParameter.class::cast)
				.filter(parameter -> parameter.getContainer() == target)
				.map(BooleanParameter::isValue);
		
		Stream<Boolean> values = subject.getNodes(property).stream()
				.filter(p -> p.getIncoming().size() == 0)
				.map(BooleanProperty::isValue);
		
		return Stream.of(parameters, variables, values)
				.reduce(Stream::concat)
				.orElseGet(Stream::empty);
	}
	
	public <P extends IntProperty> OptionalInt
	intValue(Class<P> property) {
		return _intValues(property).findFirst();
	}
	
	public <P extends IntProperty> List<Integer>
	intValues (Class<P> property) {
		return _intValues(property)
			.mapToObj(i -> i)
			.collect(Collectors.toList());
	}
	
	private <P extends IntProperty> IntStream 
	_intValues(Class<P> property) {
		
		IntStream variables = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(IntAssignment.class).stream())
				.map(IntAssignment::getSourceElement)
				.filter(node -> node instanceof IntVariable)
				.map(IntVariable.class::cast)
				.mapToInt(IntVariable::getValue);
		
		IntStream parameters = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(IntAssignment.class).stream())
				.map(IntAssignment::getSourceElement)
				.filter(node -> node instanceof IntParameter)
				.map(IntParameter.class::cast)
				.filter(parameter -> parameter.getContainer() == target)
				.mapToInt(IntParameter::getValue);
		
		IntStream values = subject.getNodes(property).stream()
				.filter(p -> p.getIncoming().size() == 0)
				.mapToInt(IntProperty::getValue);
		
		return Stream.of(parameters, variables, values)
				.reduce(IntStream::concat)
				.orElseGet(IntStream::empty);
	}

	public <P extends Property, V> Optional<V> 
	value(Class<P> property, Class<V> clazz, Function<P, V> extractor) {
		return subject.getNodes(property).stream()
			.map(extractor)
			.findFirst();
	}
	
	public <P extends Property> List<String> 
	values(Class<P> property, Function<P, String> extractor) {
		return subject.getNodes(property).stream()
			.map(extractor)
			.collect(Collectors.toList());
	}
	
	
	public <P extends Property> List<String> 
	mappedStringValues(Class<P> property, Function<P, String> extractor) {
		return _mappedStringValues(property, extractor).collect(Collectors.toList());
	}
	
	private <P extends Property> Stream<String> 
	_mappedStringValues(Class<P> property, Function<P, String> extractor) {
		
		Stream<String> variables = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(StringAssignment.class).stream())
				.map(StringAssignment::getSourceElement)
				.filter(node -> node instanceof StringVariable)
				.map(StringVariable.class::cast)
				.map(StringVariable::getValue);
		
		Stream<String> parameters = subject.getNodes(property).stream()
				.flatMap(p -> p.getIncoming(StringAssignment.class).stream())
				.map(StringAssignment::getSourceElement)
				.filter(node -> node instanceof StringParameter)
				.map(StringParameter.class::cast)
				.filter(parameter -> parameter.getContainer() == target)
				.map(StringParameter::getValue);
		
		Stream<String> values = subject.getNodes(property).stream()
				.filter(p -> p.getIncoming().size() == 0)
				.map(extractor);
		
		return Stream.of(parameters, variables, values)
				.reduce(Stream::concat)
				.orElseGet(Stream::empty);
	}
}
