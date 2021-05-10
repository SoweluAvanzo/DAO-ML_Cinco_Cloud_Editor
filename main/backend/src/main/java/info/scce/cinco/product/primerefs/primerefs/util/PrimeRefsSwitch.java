package info.scce.cinco.product.primerefs.primerefs.util;

public class PrimeRefsSwitch<T> {
	
		protected T doSwitch(graphmodel.IdentifiableElement element) {
			T result = null;
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
				result = caseSourceNode((info.scce.cinco.product.primerefs.primerefs.SourceNode)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
				result = caseSourceContainer((info.scce.cinco.product.primerefs.primerefs.SourceContainer)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
				result = casePrimeToNode((info.scce.cinco.product.primerefs.primerefs.PrimeToNode)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
				result = casePrimeToEdge((info.scce.cinco.product.primerefs.primerefs.PrimeToEdge)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
				result = casePrimeToContainer((info.scce.cinco.product.primerefs.primerefs.PrimeToContainer)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
				result = casePrimeToGraphModel((info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
				result = casePrimeCToNode((info.scce.cinco.product.primerefs.primerefs.PrimeCToNode)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
				result = casePrimeCToEdge((info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
				result = casePrimeCToContainer((info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
				result = casePrimeCToGraphModel((info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
				result = casePrimeToNodeHierarchy((info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
				result = casePrimeToAbstractNodeHierarchy((info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
				result = casePrimeToNodeFlow((info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
				result = casePrimeToEdgeFlow((info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
				result = casePrimeToContainerFlow((info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
				result = casePrimeToGraphModelFlow((info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
				result = casePrimeCToNodeFlow((info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
				result = casePrimeCToEdgeFlow((info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
				result = casePrimeCToContainerFlow((info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
				result = casePrimeCToGraphModelFlow((info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
				result = caseSourceEdge((info.scce.cinco.product.primerefs.primerefs.SourceEdge)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
				result = casePrimeRefs((info.scce.cinco.product.primerefs.primerefs.PrimeRefs)element);
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
		
		protected T caseSourceNode(info.scce.cinco.product.primerefs.primerefs.SourceNode element) {
			return null;
		}
		protected T caseSourceContainer(info.scce.cinco.product.primerefs.primerefs.SourceContainer element) {
			return null;
		}
		protected T casePrimeToNode(info.scce.cinco.product.primerefs.primerefs.PrimeToNode element) {
			return null;
		}
		protected T casePrimeToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge element) {
			return null;
		}
		protected T casePrimeToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer element) {
			return null;
		}
		protected T casePrimeToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel element) {
			return null;
		}
		protected T casePrimeCToNode(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode element) {
			return null;
		}
		protected T casePrimeCToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge element) {
			return null;
		}
		protected T casePrimeCToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer element) {
			return null;
		}
		protected T casePrimeCToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel element) {
			return null;
		}
		protected T casePrimeToNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy element) {
			return null;
		}
		protected T casePrimeToAbstractNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy element) {
			return null;
		}
		protected T casePrimeToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow element) {
			return null;
		}
		protected T casePrimeToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow element) {
			return null;
		}
		protected T casePrimeToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow element) {
			return null;
		}
		protected T casePrimeToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow element) {
			return null;
		}
		protected T casePrimeCToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow element) {
			return null;
		}
		protected T casePrimeCToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow element) {
			return null;
		}
		protected T casePrimeCToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow element) {
			return null;
		}
		protected T casePrimeCToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow element) {
			return null;
		}
		protected T caseSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge element) {
			return null;
		}
		protected T casePrimeRefs(info.scce.cinco.product.primerefs.primerefs.PrimeRefs element) {
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
