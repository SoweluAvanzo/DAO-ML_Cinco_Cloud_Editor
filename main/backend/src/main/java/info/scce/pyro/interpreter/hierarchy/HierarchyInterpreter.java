package info.scce.pyro.interpreter.hierarchy;

/**
 * Author zweihoff
 */
public abstract class HierarchyInterpreter extends info.scce.pyro.api.PyroControl {

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
    
    public final void runInterpreter(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
    	context = new java.util.HashMap<>();
    	java.util.List<graphmodel.ModelElement> waitingList = getInitialElements(g);
    	while(!waitingList.isEmpty()) {
			graphmodel.ModelElement current = waitingList.get(0);
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
				info.scce.cinco.product.hierarchy.hierarchy.ContA e = (info.scce.cinco.product.hierarchy.hierarchy.ContA) current;
				if(canExecuteContA(e,g)) {
					executeContA(e,g);
					waitingList.addAll(nextElementsAfterContA(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.ContC) {
				info.scce.cinco.product.hierarchy.hierarchy.ContC e = (info.scce.cinco.product.hierarchy.hierarchy.ContC) current;
				if(canExecuteContC(e,g)) {
					executeContC(e,g);
					waitingList.addAll(nextElementsAfterContC(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.B) {
				info.scce.cinco.product.hierarchy.hierarchy.B e = (info.scce.cinco.product.hierarchy.hierarchy.B) current;
				if(canExecuteB(e,g)) {
					executeB(e,g);
					waitingList.addAll(nextElementsAfterB(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
				info.scce.cinco.product.hierarchy.hierarchy.D e = (info.scce.cinco.product.hierarchy.hierarchy.D) current;
				if(canExecuteD(e,g)) {
					executeD(e,g);
					waitingList.addAll(nextElementsAfterD(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
				info.scce.cinco.product.hierarchy.hierarchy.Cont e = (info.scce.cinco.product.hierarchy.hierarchy.Cont) current;
				if(canExecuteCont(e,g)) {
					executeCont(e,g);
					waitingList.addAll(nextElementsAfterCont(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.ContB) {
				info.scce.cinco.product.hierarchy.hierarchy.ContB e = (info.scce.cinco.product.hierarchy.hierarchy.ContB) current;
				if(canExecuteContB(e,g)) {
					executeContB(e,g);
					waitingList.addAll(nextElementsAfterContB(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
				info.scce.cinco.product.hierarchy.hierarchy.ContD e = (info.scce.cinco.product.hierarchy.hierarchy.ContD) current;
				if(canExecuteContD(e,g)) {
					executeContD(e,g);
					waitingList.addAll(nextElementsAfterContD(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
				info.scce.cinco.product.hierarchy.hierarchy.A e = (info.scce.cinco.product.hierarchy.hierarchy.A) current;
				if(canExecuteA(e,g)) {
					executeA(e,g);
					waitingList.addAll(nextElementsAfterA(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.C) {
				info.scce.cinco.product.hierarchy.hierarchy.C e = (info.scce.cinco.product.hierarchy.hierarchy.C) current;
				if(canExecuteC(e,g)) {
					executeC(e,g);
					waitingList.addAll(nextElementsAfterC(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeA e = (info.scce.cinco.product.hierarchy.hierarchy.EdgeA) current;
				if(canExecuteEdgeA(e,g)) {
					executeEdgeA(e,g);
					waitingList.addAll(nextElementsAfterEdgeA(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeC) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeC e = (info.scce.cinco.product.hierarchy.hierarchy.EdgeC) current;
				if(canExecuteEdgeC(e,g)) {
					executeEdgeC(e,g);
					waitingList.addAll(nextElementsAfterEdgeC(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeB) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeB e = (info.scce.cinco.product.hierarchy.hierarchy.EdgeB) current;
				if(canExecuteEdgeB(e,g)) {
					executeEdgeB(e,g);
					waitingList.addAll(nextElementsAfterEdgeB(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeD e = (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) current;
				if(canExecuteEdgeD(e,g)) {
					executeEdgeD(e,g);
					waitingList.addAll(nextElementsAfterEdgeD(e,g));	    				
				}
			}
			waitingList.remove(0);
    	}
    }
    
    public abstract <T extends graphmodel.ModelElement> java.util.List<T> getInitialElements(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g);
    
	
	public void executeContA(info.scce.cinco.product.hierarchy.hierarchy.ContA element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteContA(info.scce.cinco.product.hierarchy.hierarchy.ContA e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteContB(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterContA(info.scce.cinco.product.hierarchy.hierarchy.ContA element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeContC(info.scce.cinco.product.hierarchy.hierarchy.ContC element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteContC(info.scce.cinco.product.hierarchy.hierarchy.ContC e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteContD(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterContC(info.scce.cinco.product.hierarchy.hierarchy.ContC element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteEdgeB(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEdgeC(info.scce.cinco.product.hierarchy.hierarchy.EdgeC element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteEdgeC(info.scce.cinco.product.hierarchy.hierarchy.EdgeC e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteEdgeD(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEdgeC(info.scce.cinco.product.hierarchy.hierarchy.EdgeC element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeB(info.scce.cinco.product.hierarchy.hierarchy.B element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteB(info.scce.cinco.product.hierarchy.hierarchy.B e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteC(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterB(info.scce.cinco.product.hierarchy.hierarchy.B element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeD(info.scce.cinco.product.hierarchy.hierarchy.D element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteD(info.scce.cinco.product.hierarchy.hierarchy.D e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterD(info.scce.cinco.product.hierarchy.hierarchy.D element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeCont(info.scce.cinco.product.hierarchy.hierarchy.Cont element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteCont(info.scce.cinco.product.hierarchy.hierarchy.Cont e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteContA(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterCont(info.scce.cinco.product.hierarchy.hierarchy.Cont element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeContB(info.scce.cinco.product.hierarchy.hierarchy.ContB element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteContB(info.scce.cinco.product.hierarchy.hierarchy.ContB e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteContC(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterContB(info.scce.cinco.product.hierarchy.hierarchy.ContB element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeContD(info.scce.cinco.product.hierarchy.hierarchy.ContD element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteContD(info.scce.cinco.product.hierarchy.hierarchy.ContD e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterContD(info.scce.cinco.product.hierarchy.hierarchy.ContD element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEdgeB(info.scce.cinco.product.hierarchy.hierarchy.EdgeB element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteEdgeB(info.scce.cinco.product.hierarchy.hierarchy.EdgeB e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteEdgeC(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEdgeB(info.scce.cinco.product.hierarchy.hierarchy.EdgeB element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeA(info.scce.cinco.product.hierarchy.hierarchy.A element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteA(info.scce.cinco.product.hierarchy.hierarchy.A e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteB(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterA(info.scce.cinco.product.hierarchy.hierarchy.A element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeC(info.scce.cinco.product.hierarchy.hierarchy.C element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {}
	
	public boolean canExecuteC(info.scce.cinco.product.hierarchy.hierarchy.C e,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy g) {
		return canExecuteD(e,g);
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterC(info.scce.cinco.product.hierarchy.hierarchy.C element,info.scce.cinco.product.hierarchy.hierarchy.Hierarchy graph) {
		return java.util.Collections.emptyList();
	}
}

