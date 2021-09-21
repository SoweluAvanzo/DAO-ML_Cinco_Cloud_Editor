package de.jabc.cinco.meta.core.utils.registry;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Steve Bosselmann
 */
public class IdentityRegistry<K,V> extends IdentityHashMap<K,V> implements IRegistry<K,V> {

	// generated
	private static final long serialVersionUID = 8544515508435697225L;

	public IdentityRegistry() {
		super();
	}
	
	protected IdentityRegistry(Map<K, V> map) {
		super(map);
	}
	
	@Override
	public List<K> keys() {
		return new ArrayList<>(keySet());
	}

	@Override
	public List<V> values() {
		return new ArrayList<>(super.values());
	}
	
	public V put(Entry<K,V> entry) {
		return put(entry.getKey(), entry.getValue());
	}
	
	@Override
	public V lookup(K key) {
		return get(key);
	}
	
	@Override
	public <U extends V> Map<K, U> lookupByType(Class<U> type) {
		 return lookupByType(type, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends V> Map<K, U> lookupByType(Class<U> type, boolean includeSubTypes) {
		return entrySet().stream()
				.filter(entry -> includeSubTypes
		 			? type.isInstance(entry.getValue())
		 			: type.equals(entry.getValue().getClass()))
			 	.collect(Collectors.toMap(x -> x.getKey(), x -> (U) x.getValue()));
	}

}
