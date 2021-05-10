package info.scce.pyro.interpreter.flowgraph;

/**
 * Author zweihoff
 */
public abstract class FlowGraphInterpreter extends info.scce.pyro.api.PyroControl {

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
    
    public final void runInterpreter(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
    	context = new java.util.HashMap<>();
    	java.util.List<graphmodel.ModelElement> waitingList = getInitialElements(g);
    	while(!waitingList.isEmpty()) {
			graphmodel.ModelElement current = waitingList.get(0);
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				info.scce.cinco.product.flowgraph.flowgraph.Start e = (info.scce.cinco.product.flowgraph.flowgraph.Start) current;
				if(canExecuteStart(e,g)) {
					executeStart(e,g);
					waitingList.addAll(nextElementsAfterStart(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				info.scce.cinco.product.flowgraph.flowgraph.End e = (info.scce.cinco.product.flowgraph.flowgraph.End) current;
				if(canExecuteEnd(e,g)) {
					executeEnd(e,g);
					waitingList.addAll(nextElementsAfterEnd(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				info.scce.cinco.product.flowgraph.flowgraph.Activity e = (info.scce.cinco.product.flowgraph.flowgraph.Activity) current;
				if(canExecuteActivity(e,g)) {
					executeActivity(e,g);
					waitingList.addAll(nextElementsAfterActivity(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				info.scce.cinco.product.flowgraph.flowgraph.EActivityA e = (info.scce.cinco.product.flowgraph.flowgraph.EActivityA) current;
				if(canExecuteEActivityA(e,g)) {
					executeEActivityA(e,g);
					waitingList.addAll(nextElementsAfterEActivityA(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				info.scce.cinco.product.flowgraph.flowgraph.EActivityB e = (info.scce.cinco.product.flowgraph.flowgraph.EActivityB) current;
				if(canExecuteEActivityB(e,g)) {
					executeEActivityB(e,g);
					waitingList.addAll(nextElementsAfterEActivityB(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				info.scce.cinco.product.flowgraph.flowgraph.ELibrary e = (info.scce.cinco.product.flowgraph.flowgraph.ELibrary) current;
				if(canExecuteELibrary(e,g)) {
					executeELibrary(e,g);
					waitingList.addAll(nextElementsAfterELibrary(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph e = (info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) current;
				if(canExecuteSubFlowGraph(e,g)) {
					executeSubFlowGraph(e,g);
					waitingList.addAll(nextElementsAfterSubFlowGraph(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				info.scce.cinco.product.flowgraph.flowgraph.Swimlane e = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) current;
				if(canExecuteSwimlane(e,g)) {
					executeSwimlane(e,g);
					waitingList.addAll(nextElementsAfterSwimlane(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				info.scce.cinco.product.flowgraph.flowgraph.Transition e = (info.scce.cinco.product.flowgraph.flowgraph.Transition) current;
				if(canExecuteTransition(e,g)) {
					executeTransition(e,g);
					waitingList.addAll(nextElementsAfterTransition(e,g));	    				
				}
			}
			if(current instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition e = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) current;
				if(canExecuteLabeledTransition(e,g)) {
					executeLabeledTransition(e,g);
					waitingList.addAll(nextElementsAfterLabeledTransition(e,g));	    				
				}
			}
			waitingList.remove(0);
    	}
    }
    
    public abstract <T extends graphmodel.ModelElement> java.util.List<T> getInitialElements(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g);
    
	
	public void executeStart(info.scce.cinco.product.flowgraph.flowgraph.Start element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteStart(info.scce.cinco.product.flowgraph.flowgraph.Start e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterStart(info.scce.cinco.product.flowgraph.flowgraph.Start element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEnd(info.scce.cinco.product.flowgraph.flowgraph.End element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteEnd(info.scce.cinco.product.flowgraph.flowgraph.End e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEnd(info.scce.cinco.product.flowgraph.flowgraph.End element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeActivity(info.scce.cinco.product.flowgraph.flowgraph.Activity element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteActivity(info.scce.cinco.product.flowgraph.flowgraph.Activity e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterActivity(info.scce.cinco.product.flowgraph.flowgraph.Activity element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEActivityA(info.scce.cinco.product.flowgraph.flowgraph.EActivityA element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteEActivityA(info.scce.cinco.product.flowgraph.flowgraph.EActivityA e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEActivityA(info.scce.cinco.product.flowgraph.flowgraph.EActivityA element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeEActivityB(info.scce.cinco.product.flowgraph.flowgraph.EActivityB element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteEActivityB(info.scce.cinco.product.flowgraph.flowgraph.EActivityB e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterEActivityB(info.scce.cinco.product.flowgraph.flowgraph.EActivityB element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeELibrary(info.scce.cinco.product.flowgraph.flowgraph.ELibrary element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteELibrary(info.scce.cinco.product.flowgraph.flowgraph.ELibrary e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterELibrary(info.scce.cinco.product.flowgraph.flowgraph.ELibrary element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeSubFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteSubFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterSubFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeSwimlane(info.scce.cinco.product.flowgraph.flowgraph.Swimlane element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteSwimlane(info.scce.cinco.product.flowgraph.flowgraph.Swimlane e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterSwimlane(info.scce.cinco.product.flowgraph.flowgraph.Swimlane element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
	
	public void executeLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {}
	
	public boolean canExecuteLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition e,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph g) {
		return true;
	}
	
	public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfterLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition element,info.scce.cinco.product.flowgraph.flowgraph.FlowGraph graph) {
		return java.util.Collections.emptyList();
	}
}

