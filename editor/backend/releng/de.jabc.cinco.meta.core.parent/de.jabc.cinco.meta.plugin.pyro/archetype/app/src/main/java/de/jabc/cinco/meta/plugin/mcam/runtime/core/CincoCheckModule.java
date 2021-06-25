package de.jabc.cinco.meta.plugin.mcam.runtime.core;

import graphmodel.GraphModel;
import graphmodel.IdentifiableElement;

import java.util.HashMap;
import java.util.Map;


public abstract class CincoCheckModule<
			ID extends _CincoId,
			Model extends GraphModel,
			Adapter extends _CincoAdapter<ID,Model>
		> {
	
    protected Adapter adapter = null;
    protected Map<IdentifiableElement,ID> cache = new HashMap<IdentifiableElement, ID>();
    
	public Map<IdentifiableElement,ID> execute(Adapter adapter) {
		this.adapter = adapter;
		adapter.getEntityIds().forEach((n)->cache.put(n.getElement(),n));
		try {
			check(adapter.model);
		} catch(Exception e) {
			addError(adapter.model,"Check execution failed ("+e.getMessage()+")");
			e.printStackTrace();
		}
		return cache;
	}

	public void init() {/* default: do nothing */}
	
    public abstract void check(Model model);

     
    public void addError(IdentifiableElement element, String msg) {
		cache.get(element).addErrorMessage(msg);
	}

	public void addWarning(IdentifiableElement element, String msg) {
		cache.get(element).addWarningMessage(msg);
	}

	public void addInfo(IdentifiableElement element, String msg) {
		cache.get(element).addInfoMessage(msg);
	}
    

}