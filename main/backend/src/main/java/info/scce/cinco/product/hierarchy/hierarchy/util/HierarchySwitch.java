package info.scce.cinco.product.hierarchy.hierarchy.util;

public class HierarchySwitch<T> {
	
		protected T doSwitch(graphmodel.IdentifiableElement element) {
			T result = null;
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
				result = caseContA((info.scce.cinco.product.hierarchy.hierarchy.ContA)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.ContC) {
				result = caseContC((info.scce.cinco.product.hierarchy.hierarchy.ContC)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
				result = caseEdgeA((info.scce.cinco.product.hierarchy.hierarchy.EdgeA)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeC) {
				result = caseEdgeC((info.scce.cinco.product.hierarchy.hierarchy.EdgeC)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.B) {
				result = caseB((info.scce.cinco.product.hierarchy.hierarchy.B)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
				result = caseD((info.scce.cinco.product.hierarchy.hierarchy.D)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
				result = caseCont((info.scce.cinco.product.hierarchy.hierarchy.Cont)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.ContB) {
				result = caseContB((info.scce.cinco.product.hierarchy.hierarchy.ContB)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
				result = caseContD((info.scce.cinco.product.hierarchy.hierarchy.ContD)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeB) {
				result = caseEdgeB((info.scce.cinco.product.hierarchy.hierarchy.EdgeB)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
				result = caseEdgeD((info.scce.cinco.product.hierarchy.hierarchy.EdgeD)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
				result = caseA((info.scce.cinco.product.hierarchy.hierarchy.A)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.C) {
				result = caseC((info.scce.cinco.product.hierarchy.hierarchy.C)element);
				if(result != null) {
					return result;
				}
			}
			if(element instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
				result = caseHierarchy((info.scce.cinco.product.hierarchy.hierarchy.Hierarchy)element);
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
		
		protected T caseContA(info.scce.cinco.product.hierarchy.hierarchy.ContA element) {
			return null;
		}
		protected T caseContC(info.scce.cinco.product.hierarchy.hierarchy.ContC element) {
			return null;
		}
		protected T caseEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA element) {
			return null;
		}
		protected T caseEdgeC(info.scce.cinco.product.hierarchy.hierarchy.EdgeC element) {
			return null;
		}
		protected T caseB(info.scce.cinco.product.hierarchy.hierarchy.B element) {
			return null;
		}
		protected T caseD(info.scce.cinco.product.hierarchy.hierarchy.D element) {
			return null;
		}
		protected T caseCont(info.scce.cinco.product.hierarchy.hierarchy.Cont element) {
			return null;
		}
		protected T caseContB(info.scce.cinco.product.hierarchy.hierarchy.ContB element) {
			return null;
		}
		protected T caseContD(info.scce.cinco.product.hierarchy.hierarchy.ContD element) {
			return null;
		}
		protected T caseEdgeB(info.scce.cinco.product.hierarchy.hierarchy.EdgeB element) {
			return null;
		}
		protected T caseEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD element) {
			return null;
		}
		protected T caseA(info.scce.cinco.product.hierarchy.hierarchy.A element) {
			return null;
		}
		protected T caseC(info.scce.cinco.product.hierarchy.hierarchy.C element) {
			return null;
		}
		protected T caseHierarchy(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy element) {
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
