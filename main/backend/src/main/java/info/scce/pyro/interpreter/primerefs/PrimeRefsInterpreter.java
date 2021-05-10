package info.scce.pyro.interpreter.primerefs;

/**
 * Author zweihoff
 */
public abstract class PrimeRefsInterpreter extends info.scce.pyro.api.PyroControl {

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
    
    public final void runInterpreter(info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
    	context = new java.util.HashMap<>();
    	java.util.List<graphmodel.ModelElement> waitingList = getInitialElements(g);
    	while(!waitingList.isEmpty()) {
			graphmodel.ModelElement current = waitingList.get(0);
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
				info.scce.cinco.product.primerefs.primerefs.SourceNode e = (info.scce.cinco.product.primerefs.primerefs.SourceNode) current;
				if(canExecuteSourceNode(e,g)) {
					executeSourceNode(e,g);
					waitingList.addAll(nextElementsAfterSourceNode(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
				info.scce.cinco.product.primerefs.primerefs.SourceContainer e = (info.scce.cinco.product.primerefs.primerefs.SourceContainer) current;
				if(canExecuteSourceContainer(e,g)) {
					executeSourceContainer(e,g);
					waitingList.addAll(nextElementsAfterSourceContainer(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNode e = (info.scce.cinco.product.primerefs.primerefs.PrimeToNode) current;
				if(canExecutePrimeToNode(e,g)) {
					executePrimeToNode(e,g);
					waitingList.addAll(nextElementsAfterPrimeToNode(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToEdge e = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) current;
				if(canExecutePrimeToEdge(e,g)) {
					executePrimeToEdge(e,g);
					waitingList.addAll(nextElementsAfterPrimeToEdge(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToContainer e = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) current;
				if(canExecutePrimeToContainer(e,g)) {
					executePrimeToContainer(e,g);
					waitingList.addAll(nextElementsAfterPrimeToContainer(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel e = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) current;
				if(canExecutePrimeToGraphModel(e,g)) {
					executePrimeToGraphModel(e,g);
					waitingList.addAll(nextElementsAfterPrimeToGraphModel(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToNode e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) current;
				if(canExecutePrimeCToNode(e,g)) {
					executePrimeCToNode(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToNode(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) current;
				if(canExecutePrimeCToEdge(e,g)) {
					executePrimeCToEdge(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToEdge(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) current;
				if(canExecutePrimeCToContainer(e,g)) {
					executePrimeCToContainer(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToContainer(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) current;
				if(canExecutePrimeCToGraphModel(e,g)) {
					executePrimeCToGraphModel(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToGraphModel(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy e = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) current;
				if(canExecutePrimeToNodeHierarchy(e,g)) {
					executePrimeToNodeHierarchy(e,g);
					waitingList.addAll(nextElementsAfterPrimeToNodeHierarchy(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy e = (info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) current;
				if(canExecutePrimeToAbstractNodeHierarchy(e,g)) {
					executePrimeToAbstractNodeHierarchy(e,g);
					waitingList.addAll(nextElementsAfterPrimeToAbstractNodeHierarchy(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) current;
				if(canExecutePrimeToNodeFlow(e,g)) {
					executePrimeToNodeFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeToNodeFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) current;
				if(canExecutePrimeToEdgeFlow(e,g)) {
					executePrimeToEdgeFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeToEdgeFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) current;
				if(canExecutePrimeToContainerFlow(e,g)) {
					executePrimeToContainerFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeToContainerFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) current;
				if(canExecutePrimeToGraphModelFlow(e,g)) {
					executePrimeToGraphModelFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeToGraphModelFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) current;
				if(canExecutePrimeCToNodeFlow(e,g)) {
					executePrimeCToNodeFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToNodeFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) current;
				if(canExecutePrimeCToEdgeFlow(e,g)) {
					executePrimeCToEdgeFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToEdgeFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) current;
				if(canExecutePrimeCToContainerFlow(e,g)) {
					executePrimeCToContainerFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToContainerFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow e = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) current;
				if(canExecutePrimeCToGraphModelFlow(e,g)) {
					executePrimeCToGraphModelFlow(e,g);
					waitingList.addAll(nextElementsAfterPrimeCToGraphModelFlow(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
				info.scce.cinco.product.primerefs.primerefs.SourceEdge e = (info.scce.cinco.product.primerefs.primerefs.SourceEdge) current;
				if(canExecuteSourceEdge(e,g)) {
					executeSourceEdge(e,g);
					waitingList.addAll(nextElementsAfterSourceEdge(e,g));	    				
				}
			}
			waitingList.remove(0);
    	}
    }
    
    public abstract <T extends graphmodel.ModelElement> java.util.List<T> getInitialElements(info.scce.cinco.product.primerefs.primerefs.PrimeRefs g);
    
	
	public void executeSourceNode(info.scce.cinco.product.primerefs.primerefs.SourceNode element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecuteSourceNode(info.scce.cinco.product.primerefs.primerefs.SourceNode e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterSourceNode(info.scce.cinco.product.primerefs.primerefs.SourceNode element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeSourceContainer(info.scce.cinco.product.primerefs.primerefs.SourceContainer element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecuteSourceContainer(info.scce.cinco.product.primerefs.primerefs.SourceContainer e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterSourceContainer(info.scce.cinco.product.primerefs.primerefs.SourceContainer element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToNode(info.scce.cinco.product.primerefs.primerefs.PrimeToNode element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToNode(info.scce.cinco.product.primerefs.primerefs.PrimeToNode e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToNode(info.scce.cinco.product.primerefs.primerefs.PrimeToNode element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToNode(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToNode(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToNode(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToAbstractNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToAbstractNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToAbstractNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executePrimeCToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecutePrimeCToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterPrimeCToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {}
	
	public boolean canExecuteSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge e,info.scce.cinco.product.primerefs.primerefs.PrimeRefs g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge element,info.scce.cinco.product.primerefs.primerefs.PrimeRefs graph) {
		return java.util.Collections.emptyList();
	}
}

