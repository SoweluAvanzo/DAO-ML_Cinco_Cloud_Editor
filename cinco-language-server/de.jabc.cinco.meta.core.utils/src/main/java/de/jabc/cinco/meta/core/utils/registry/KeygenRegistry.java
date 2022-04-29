package de.jabc.cinco.meta.core.utils.registry;

import java.util.Map;
import java.util.function.Function;

/**
 * 
 * @author Steve Bosselmann
 */
public class KeygenRegistry<K,V> extends Registry<K,V> {
	
	// generated
	private static final long serialVersionUID = 8500359033721595920L;

	private Function<V,K> keySupplier;
	
	public KeygenRegistry(Function<V,K> keySupplier) {
		super();
		this.keySupplier = keySupplier;
	}
	
	protected KeygenRegistry(Function<V,K> keySupplier, Map<K, V> map) {
		super(map);
		this.keySupplier = keySupplier;
	}
	
	public K add(V value) {
		K key = keySupplier.apply(value);
		put(key, value);
		return key;
	}
}
