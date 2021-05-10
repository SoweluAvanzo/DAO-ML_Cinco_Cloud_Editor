package info.scce.pyro.interpreter.empty;

/**
 * Author zweihoff
 */
public abstract class EmptyInterpreter extends info.scce.pyro.api.PyroControl {

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
    
    public final void runInterpreter(info.scce.cinco.product.empty.empty.Empty g) {
    	context = new java.util.HashMap<>();
    	java.util.List<graphmodel.ModelElement> waitingList = getInitialElements(g);
    	while(!waitingList.isEmpty()) {
			graphmodel.ModelElement current = waitingList.get(0);
			waitingList.remove(0);
    	}
    }
    
    public abstract <T extends graphmodel.ModelElement> java.util.List<T> getInitialElements(info.scce.cinco.product.empty.empty.Empty g);
    
}

