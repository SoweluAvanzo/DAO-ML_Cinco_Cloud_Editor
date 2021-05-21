package de.jabc.cinco.meta.core.utils.registry;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 
 * @author Steve Bosselmann
 */
public class InstanceRegistry<V> extends KeygenRegistry<String,V> {

	// generated
	private static final long serialVersionUID = 2098354111588724338L;
	
	private Supplier<V> supplier;
	private V instance;
	
	public InstanceRegistry() {
		super(v -> UUID.randomUUID().toString());
	}
	
	public InstanceRegistry(Supplier<V> instanceSupplier) {
		this();
		this.supplier = instanceSupplier;
	}
	
	public boolean canCreate() {
		return supplier != null;
	}
	
	public V create() {
		return supplier.get();
	}
	
	public V get() {
		if (instance == null && canCreate()) {
			instance = create();
			add(instance);
		}
		return instance;
	}
	
	public InstanceRegistry<V> set(V instance) {
		this.instance = instance;
		return this;
	}
}
