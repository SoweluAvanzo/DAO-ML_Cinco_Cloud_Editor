package info.scce.pyro.interpreter.hooksandactions;

/**
 * Author zweihoff
 */
public abstract class HooksAndActionsInterpreter extends info.scce.pyro.api.PyroControl {

	private java.util.Map<String,Object> context;
	
	protected void write(graphmodel.IdentifiableElement e,Object obj) {
			context.put(e.getId(),obj);
	}
	protected Object read(graphmodel.IdentifiableElement e) {
		return context.get(e.getId());
	}
	protected Byte readByte(graphmodel.IdentifiableElement e) {
		return (Byte) context.get(e.getId());
	}
	protected Short readShort(graphmodel.IdentifiableElement e) {
		return (Short) context.get(e.getId());
	}
	protected Integer readInteger(graphmodel.IdentifiableElement e) {
		return (Integer) context.get(e.getId());
	}
	protected Long readLong(graphmodel.IdentifiableElement e) {
		return (Long) context.get(e.getId());
	}
	protected Float readFloat(graphmodel.IdentifiableElement e) {
		return (Float) context.get(e.getId());
	}
	protected Double readDouble(graphmodel.IdentifiableElement e) {
		return (Double) context.get(e.getId());
	}
	protected Character readCharacter(graphmodel.IdentifiableElement e) {
		return (Character) context.get(e.getId());
	}
	protected Boolean readBoolean(graphmodel.IdentifiableElement e) {
		return (Boolean) context.get(e.getId());
	}
	protected boolean isWritten(graphmodel.IdentifiableElement e) { return context.containsKey(e.getId()); }
    
    public final void runInterpreter(info.scce.cinco.product.ha.hooksandactions.HooksAndActions g) {
    	context = new java.util.HashMap<>();
    	java.util.List<graphmodel.ModelElement> waitingList = getInitialElements(g);
    	while(!waitingList.isEmpty()) {
			graphmodel.ModelElement current = waitingList.get(0);
			if(current instanceof info.scce.cinco.product.ha.hooksandactions.AbstractHookANode) {
				info.scce.cinco.product.ha.hooksandactions.AbstractHookANode e = (info.scce.cinco.product.ha.hooksandactions.AbstractHookANode) current;
				if(canExecuteAbstractHookANode(e,g)) {
					executeAbstractHookANode(e,g);
					waitingList.addAll(nextElementsAfterAbstractHookANode(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
				info.scce.cinco.product.ha.hooksandactions.HookAContainer e = (info.scce.cinco.product.ha.hooksandactions.HookAContainer) current;
				if(canExecuteHookAContainer(e,g)) {
					executeHookAContainer(e,g);
					waitingList.addAll(nextElementsAfterHookAContainer(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
				info.scce.cinco.product.ha.hooksandactions.HookANode e = (info.scce.cinco.product.ha.hooksandactions.HookANode) current;
				if(canExecuteHookANode(e,g)) {
					executeHookANode(e,g);
					waitingList.addAll(nextElementsAfterHookANode(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
				info.scce.cinco.product.ha.hooksandactions.HookAnEdge e = (info.scce.cinco.product.ha.hooksandactions.HookAnEdge) current;
				if(canExecuteHookAnEdge(e,g)) {
					executeHookAnEdge(e,g);
					waitingList.addAll(nextElementsAfterHookAnEdge(e,g));	    				
				}
			}
			waitingList.remove(0);
    	}
    }
    
    public abstract <T extends graphmodel.ModelElement> java.util.List<T> getInitialElements(info.scce.cinco.product.ha.hooksandactions.HooksAndActions g);
    
	
	public void executeAbstractHookANode(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {}
	
	public boolean canExecuteAbstractHookANode(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode e,info.scce.cinco.product.ha.hooksandactions.HooksAndActions g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterAbstractHookANode(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeHookAContainer(info.scce.cinco.product.ha.hooksandactions.HookAContainer element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {}
	
	public boolean canExecuteHookAContainer(info.scce.cinco.product.ha.hooksandactions.HookAContainer e,info.scce.cinco.product.ha.hooksandactions.HooksAndActions g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterHookAContainer(info.scce.cinco.product.ha.hooksandactions.HookAContainer element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {}
	
	public boolean canExecuteHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge e,info.scce.cinco.product.ha.hooksandactions.HooksAndActions g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeHookANode(info.scce.cinco.product.ha.hooksandactions.HookANode element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {}
	
	public boolean canExecuteHookANode(info.scce.cinco.product.ha.hooksandactions.HookANode e,info.scce.cinco.product.ha.hooksandactions.HooksAndActions g) {
		return canExecuteAbstractHookANode(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterHookANode(info.scce.cinco.product.ha.hooksandactions.HookANode element,info.scce.cinco.product.ha.hooksandactions.HooksAndActions graph) {
		return java.util.Collections.emptyList();
	}
}

