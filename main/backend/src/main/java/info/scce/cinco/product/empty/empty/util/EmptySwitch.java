package info.scce.cinco.product.empty.empty.util;

public class EmptySwitch<T> {
	
		protected T doSwitch(graphmodel.IdentifiableElement element) {
			T result = null;
			if(element instanceof info.scce.cinco.product.empty.empty.Empty) {
				result = caseEmpty((info.scce.cinco.product.empty.empty.Empty)element);
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
		
		protected T caseEmpty(info.scce.cinco.product.empty.empty.Empty element) {
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
