package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_flowgraphdiagram")
public class FlowGraphDiagramDB extends PanacheEntity {
	
	public String router;
	
	public String connector;
	
	public long width;
	
	public long height;
	
	public double scale;
	
	public boolean isPublic;
	
	public String filename;
	
	public String extension;
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.StartDB> modelElements_Start = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.EndDB> modelElements_End = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.ActivityDB> modelElements_Activity = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.SwimlaneDB> modelElements_Swimlane = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.SubFlowGraphDB> modelElements_SubFlowGraph = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.TransitionDB> modelElements_Transition = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.LabeledTransitionDB> modelElements_LabeledTransition = new java.util.ArrayList<>();
	
	public String modelName;
	
	@javax.persistence.OneToMany(mappedBy="subFlowGraph_FlowGraphDiagram")
	public java.util.Collection<entity.flowgraph.SubFlowGraphDB> ref_subFlowGraph_SubFlowGraph = new java.util.ArrayList<>();
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		modelElements.addAll(modelElements_Start);
		modelElements.addAll(modelElements_End);
		modelElements.addAll(modelElements_Activity);
		modelElements.addAll(modelElements_Swimlane);
		modelElements.addAll(modelElements_SubFlowGraph);
		modelElements.addAll(modelElements_Transition);
		modelElements.addAll(modelElements_LabeledTransition);
		return modelElements;
	}
	
	public void clearModelElements() {
		clearModelElements(false);
	}
	
	public void clearModelElements(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.flowgraph.StartDB> iter_modelElements_Start = modelElements_Start.iterator();
			while(iter_modelElements_Start.hasNext()) {
				entity.flowgraph.StartDB e = iter_modelElements_Start.next();
				if(e != null) {
					e.delete();
					modelElements_Start.remove(e);
				}
				iter_modelElements_Start = modelElements_Start.iterator();
			}
			java.util.Iterator<entity.flowgraph.EndDB> iter_modelElements_End = modelElements_End.iterator();
			while(iter_modelElements_End.hasNext()) {
				entity.flowgraph.EndDB e = iter_modelElements_End.next();
				if(e != null) {
					e.delete();
					modelElements_End.remove(e);
				}
				iter_modelElements_End = modelElements_End.iterator();
			}
			java.util.Iterator<entity.flowgraph.ActivityDB> iter_modelElements_Activity = modelElements_Activity.iterator();
			while(iter_modelElements_Activity.hasNext()) {
				entity.flowgraph.ActivityDB e = iter_modelElements_Activity.next();
				if(e != null) {
					e.delete();
					modelElements_Activity.remove(e);
				}
				iter_modelElements_Activity = modelElements_Activity.iterator();
			}
			java.util.Iterator<entity.flowgraph.SwimlaneDB> iter_modelElements_Swimlane = modelElements_Swimlane.iterator();
			while(iter_modelElements_Swimlane.hasNext()) {
				entity.flowgraph.SwimlaneDB e = iter_modelElements_Swimlane.next();
				if(e != null) {
					e.delete();
					modelElements_Swimlane.remove(e);
				}
				iter_modelElements_Swimlane = modelElements_Swimlane.iterator();
			}
			java.util.Iterator<entity.flowgraph.SubFlowGraphDB> iter_modelElements_SubFlowGraph = modelElements_SubFlowGraph.iterator();
			while(iter_modelElements_SubFlowGraph.hasNext()) {
				entity.flowgraph.SubFlowGraphDB e = iter_modelElements_SubFlowGraph.next();
				if(e != null) {
					e.delete();
					modelElements_SubFlowGraph.remove(e);
				}
				iter_modelElements_SubFlowGraph = modelElements_SubFlowGraph.iterator();
			}
			java.util.Iterator<entity.flowgraph.TransitionDB> iter_modelElements_Transition = modelElements_Transition.iterator();
			while(iter_modelElements_Transition.hasNext()) {
				entity.flowgraph.TransitionDB e = iter_modelElements_Transition.next();
				if(e != null) {
					e.delete();
					modelElements_Transition.remove(e);
				}
				iter_modelElements_Transition = modelElements_Transition.iterator();
			}
			java.util.Iterator<entity.flowgraph.LabeledTransitionDB> iter_modelElements_LabeledTransition = modelElements_LabeledTransition.iterator();
			while(iter_modelElements_LabeledTransition.hasNext()) {
				entity.flowgraph.LabeledTransitionDB e = iter_modelElements_LabeledTransition.next();
				if(e != null) {
					e.delete();
					modelElements_LabeledTransition.remove(e);
				}
				iter_modelElements_LabeledTransition = modelElements_LabeledTransition.iterator();
			}
		} else {
			// clear all collections
			modelElements_Start.clear();
			modelElements_End.clear();
			modelElements_Activity.clear();
			modelElements_Swimlane.clear();
			modelElements_SubFlowGraph.clear();
			modelElements_Transition.clear();
			modelElements_LabeledTransition.clear();
		}
	}
	
	public void setModelElements(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearModelElements();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addModelElements(e);
		}
	}
	
	public void addAllModelElements(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addModelElements(e);
		}
	}
	
	public void addModelElements(PanacheEntity e) {
		// add the entity into it's type-specific list
		if(e instanceof entity.flowgraph.StartDB) {
			modelElements_Start.add((entity.flowgraph.StartDB) e);
		} else 
		if(e instanceof entity.flowgraph.EndDB) {
			modelElements_End.add((entity.flowgraph.EndDB) e);
		} else 
		if(e instanceof entity.flowgraph.ActivityDB) {
			modelElements_Activity.add((entity.flowgraph.ActivityDB) e);
		} else 
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			modelElements_Swimlane.add((entity.flowgraph.SwimlaneDB) e);
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			modelElements_SubFlowGraph.add((entity.flowgraph.SubFlowGraphDB) e);
		} else 
		if(e instanceof entity.flowgraph.TransitionDB) {
			modelElements_Transition.add((entity.flowgraph.TransitionDB) e);
		} else 
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			modelElements_LabeledTransition.add((entity.flowgraph.LabeledTransitionDB) e);
		}
	}
	
	public boolean removeModelElements(PanacheEntity e) {
		return removeModelElements(e, false);
	}
	
	public boolean removeModelElements(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.flowgraph.StartDB) {
			entity.flowgraph.StartDB definitiveEntity = (entity.flowgraph.StartDB) e;
			if(modelElements_Start.contains(definitiveEntity)) {
				boolean result = modelElements_Start.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.EndDB) {
			entity.flowgraph.EndDB definitiveEntity = (entity.flowgraph.EndDB) e;
			if(modelElements_End.contains(definitiveEntity)) {
				boolean result = modelElements_End.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB definitiveEntity = (entity.flowgraph.ActivityDB) e;
			if(modelElements_Activity.contains(definitiveEntity)) {
				boolean result = modelElements_Activity.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB definitiveEntity = (entity.flowgraph.SwimlaneDB) e;
			if(modelElements_Swimlane.contains(definitiveEntity)) {
				boolean result = modelElements_Swimlane.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB definitiveEntity = (entity.flowgraph.SubFlowGraphDB) e;
			if(modelElements_SubFlowGraph.contains(definitiveEntity)) {
				boolean result = modelElements_SubFlowGraph.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB definitiveEntity = (entity.flowgraph.TransitionDB) e;
			if(modelElements_Transition.contains(definitiveEntity)) {
				boolean result = modelElements_Transition.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB definitiveEntity = (entity.flowgraph.LabeledTransitionDB) e;
			if(modelElements_LabeledTransition.contains(definitiveEntity)) {
				boolean result = modelElements_LabeledTransition.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		}
		return false;
	}
	
	public boolean containsModelElements(PanacheEntity e) {
		// containment-check of the entities type-specific list
		if(e instanceof entity.flowgraph.StartDB) {
			entity.flowgraph.StartDB definitiveEntity = (entity.flowgraph.StartDB) e;
			return modelElements_Start.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.EndDB) {
			entity.flowgraph.EndDB definitiveEntity = (entity.flowgraph.EndDB) e;
			return modelElements_End.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB definitiveEntity = (entity.flowgraph.ActivityDB) e;
			return modelElements_Activity.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB definitiveEntity = (entity.flowgraph.SwimlaneDB) e;
			return modelElements_Swimlane.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB definitiveEntity = (entity.flowgraph.SubFlowGraphDB) e;
			return modelElements_SubFlowGraph.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.TransitionDB) {
			entity.flowgraph.TransitionDB definitiveEntity = (entity.flowgraph.TransitionDB) e;
			return modelElements_Transition.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.LabeledTransitionDB) {
			entity.flowgraph.LabeledTransitionDB definitiveEntity = (entity.flowgraph.LabeledTransitionDB) e;
			return modelElements_LabeledTransition.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyModelElements() {
		return getModelElements().isEmpty();
	}
	
	public int sizeModelElements() {
		return getModelElements().size();
	}
	
	@Override
	public void delete() {
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// decouple from referencing elements
		{
			java.util.Iterator<entity.flowgraph.SubFlowGraphDB> iterator = this.ref_subFlowGraph_SubFlowGraph.iterator();
			while(iterator.hasNext()) {
				entity.flowgraph.SubFlowGraphDB next = iterator.next();
				next.setSubFlowGraph(null);
				iterator = this.ref_subFlowGraph_SubFlowGraph.iterator();
			}
		}
		
		// delete entity
		super.delete();
	}
	
	// add referencing element
	public void addReference(PanacheEntity e) {
		if(e == null)
			return;
		if (e instanceof entity.flowgraph.SubFlowGraphDB) {
			this.ref_subFlowGraph_SubFlowGraph.add((entity.flowgraph.SubFlowGraphDB) e);
		}
	}
	
	// remove referencing element
	public void removeReference(PanacheEntity e) {
		if(e == null)
			return;
		if (e instanceof entity.flowgraph.SubFlowGraphDB) {
			if(this.ref_subFlowGraph_SubFlowGraph.contains((entity.flowgraph.SubFlowGraphDB) e)) {
				this.ref_subFlowGraph_SubFlowGraph.remove((entity.flowgraph.SubFlowGraphDB) e);
			}
		}
	}
}
