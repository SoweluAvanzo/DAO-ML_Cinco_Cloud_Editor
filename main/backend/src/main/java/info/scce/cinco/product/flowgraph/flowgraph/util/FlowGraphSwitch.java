package info.scce.cinco.product.flowgraph.flowgraph.util;

public class FlowGraphSwitch<T> {
	
		protected T doSwitch(graphmodel.IdentifiableElement element) {
			T result = null;
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				result = caseStart((info.scce.cinco.product.flowgraph.flowgraph.Start)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				result = caseEnd((info.scce.cinco.product.flowgraph.flowgraph.End)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				result = caseActivity((info.scce.cinco.product.flowgraph.flowgraph.Activity)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				result = caseEActivityA((info.scce.cinco.product.flowgraph.flowgraph.EActivityA)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				result = caseEActivityB((info.scce.cinco.product.flowgraph.flowgraph.EActivityB)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				result = caseELibrary((info.scce.cinco.product.flowgraph.flowgraph.ELibrary)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				result = caseSubFlowGraph((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				result = caseSwimlane((info.scce.cinco.product.flowgraph.flowgraph.Swimlane)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				result = caseTransition((info.scce.cinco.product.flowgraph.flowgraph.Transition)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				result = caseLabeledTransition((info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) {
				result = caseFlowGraph((info.scce.cinco.product.flowgraph.flowgraph.FlowGraph)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.GraphModel) {
				result = caseGraphModel((graphmodel.GraphModel)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.Container) {
				result = caseContainer((graphmodel.Container)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.Node) {
				result = caseNode((graphmodel.Node)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.Edge) {
				result = caseEdge((graphmodel.Edge)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.ModelElementContainer) {
				result = caseModelElementContainer((graphmodel.ModelElementContainer)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.ModelElement) {
				result = caseModelElement((graphmodel.ModelElement)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof graphmodel.IdentifiableElement) {
				result = caseIdentifiableElement((graphmodel.IdentifiableElement)element);
				if(result != null) {
					return result;
				}
			}
			return defaultCase(element);
		}
		
		protected T caseStart(info.scce.cinco.product.flowgraph.flowgraph.Start element) {
			return null;
		}
		protected T caseEnd(info.scce.cinco.product.flowgraph.flowgraph.End element) {
			return null;
		}
		protected T caseActivity(info.scce.cinco.product.flowgraph.flowgraph.Activity element) {
			return null;
		}
		protected T caseEActivityA(info.scce.cinco.product.flowgraph.flowgraph.EActivityA element) {
			return null;
		}
		protected T caseEActivityB(info.scce.cinco.product.flowgraph.flowgraph.EActivityB element) {
			return null;
		}
		protected T caseELibrary(info.scce.cinco.product.flowgraph.flowgraph.ELibrary element) {
			return null;
		}
		protected T caseSubFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph element) {
			return null;
		}
		protected T caseSwimlane(info.scce.cinco.product.flowgraph.flowgraph.Swimlane element) {
			return null;
		}
		protected T caseTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition element) {
			return null;
		}
		protected T caseLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition element) {
			return null;
		}
		protected T caseFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph element) {
			return null;
		}
		
		protected T caseGraphModel(graphmodel.GraphModel element) {
			return null;
		}
		protected T caseContainer(graphmodel.Container element) {
			return null;
		}
		protected T caseNode(graphmodel.Node element) {
			return null;
		}
		protected T caseEdge(graphmodel.Edge element) {
			return null;
		}
		protected T caseModelElementContainer(graphmodel.ModelElementContainer element) {
			return null;
		}
		protected T caseModelElement(graphmodel.ModelElement element) {
			return null;
		}
		protected T caseIdentifiableElement(graphmodel.IdentifiableElement element) {
			return null;
		}

		protected T defaultCase(graphmodel.IdentifiableElement object) {
			return null;
		}
}
