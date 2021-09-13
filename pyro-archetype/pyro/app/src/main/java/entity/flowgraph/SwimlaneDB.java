package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_swimlane")
public class SwimlaneDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.SwimlaneDB container_Swimlane;
	
	@javax.persistence.ManyToOne(cascade=javax.persistence.CascadeType.ALL)
	@javax.persistence.JoinColumn(
		nullable=true)
	public entity.flowgraph.FlowGraphDiagramDB container_FlowGraphDiagram;
	
	public String actor;
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.StartDB> modelElements_Start = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.ActivityDB> modelElements_Activity = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.EndDB> modelElements_End = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.SubFlowGraphDB> modelElements_SubFlowGraph = new java.util.ArrayList<>();
	
	public PanacheEntity getContainer() {
		if(container_Swimlane != null) {
			return container_Swimlane;
		} else if(container_FlowGraphDiagram != null) {
			return container_FlowGraphDiagram;
		}
		return null;
	}
	
	public void setContainer(PanacheEntity e) {
		setContainer(e, false);
	}
	
	public void setContainer(PanacheEntity e, boolean deleteOld) {
		// guard
		PanacheEntity old = this.getContainer();
		// if no element to delete or the element to delete is
		// same that will be set then dont delete
		if(old != null && old.equals(e)
			|| e == null && old == null
		) {
			// nothing changed
			return;
		}
		
		// potencially delete all old elements
		if(deleteOld) {
			if(old != null) {
				old.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			// null all other types
			container_FlowGraphDiagram = null;
			// set element
			container_Swimlane = (entity.flowgraph.SwimlaneDB) e;
			return;
		} else if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
			// null all other types
			container_Swimlane = null;
			// set element
			container_FlowGraphDiagram = (entity.flowgraph.FlowGraphDiagramDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_Swimlane = null;
		container_FlowGraphDiagram = null;
	}
	
	public java.util.Collection<PanacheEntity> getIncoming() {
		java.util.Collection<PanacheEntity> incoming = new java.util.ArrayList<>();	
		// no attributes
		return incoming;
	}
	
	public void clearIncoming() {
		clearIncoming(false);
	}
	
	public void clearIncoming(boolean delete) {
		// no attributes
	}
	
	public void setIncoming(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearIncoming();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addIncoming(e);
		}
	}
	
	public void addAllIncoming(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addIncoming(e);
		}
	}
	
	public void addIncoming(PanacheEntity e) {
		// add the entity into it's type-specific list
		// no attributes
	}
	
	public boolean removeIncoming(PanacheEntity e) {
		return removeIncoming(e, false);
	}
	
	public boolean removeIncoming(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		// no attributes
		return false;
	}
	
	public boolean containsIncoming(PanacheEntity e) {
		// containment-check of the entities type-specific list
		// no attributes
		return false;
	}
	
	public boolean isEmptyIncoming() {
		return getIncoming().isEmpty();
	}
	
	public int sizeIncoming() {
		return getIncoming().size();
	}
	
	public java.util.Collection<PanacheEntity> getOutgoing() {
		java.util.Collection<PanacheEntity> outgoing = new java.util.ArrayList<>();	
		// no attributes
		return outgoing;
	}
	
	public void clearOutgoing() {
		clearOutgoing(false);
	}
	
	public void clearOutgoing(boolean delete) {
		// no attributes
	}
	
	public void setOutgoing(java.util.Collection<PanacheEntity> eList) {
		// clear all attribute-type-lists
		clearOutgoing();
		// add e to type-specific collections
		for(PanacheEntity e : eList) {
			addOutgoing(e);
		}
	}
	
	public void addAllOutgoing(java.util.Collection<PanacheEntity> eList) {
		for(PanacheEntity e : eList) {
			addOutgoing(e);
		}
	}
	
	public void addOutgoing(PanacheEntity e) {
		// add the entity into it's type-specific list
		// no attributes
	}
	
	public boolean removeOutgoing(PanacheEntity e) {
		return removeOutgoing(e, false);
	}
	
	public boolean removeOutgoing(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		// no attributes
		return false;
	}
	
	public boolean containsOutgoing(PanacheEntity e) {
		// containment-check of the entities type-specific list
		// no attributes
		return false;
	}
	
	public boolean isEmptyOutgoing() {
		return getOutgoing().isEmpty();
	}
	
	public int sizeOutgoing() {
		return getOutgoing().size();
	}
	
	// add referencing element
	public void addReference(PanacheEntity e) {
		if(e == null)
			return;
	}
	
	// remove referencing element
	public void removeReference(PanacheEntity e) {
		if(e == null)
			return;
	}
	
	@Override
	public void delete() {
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.flowgraph.FlowGraphDiagramDB) {
			entity.flowgraph.FlowGraphDiagramDB container = (entity.flowgraph.FlowGraphDiagramDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		
		// delete entity
		super.delete();
	}
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		modelElements.addAll(modelElements_Start);
		modelElements.addAll(modelElements_Activity);
		modelElements.addAll(modelElements_End);
		modelElements.addAll(modelElements_SubFlowGraph);
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
			java.util.Iterator<entity.flowgraph.ActivityDB> iter_modelElements_Activity = modelElements_Activity.iterator();
			while(iter_modelElements_Activity.hasNext()) {
				entity.flowgraph.ActivityDB e = iter_modelElements_Activity.next();
				if(e != null) {
					e.delete();
					modelElements_Activity.remove(e);
				}
				iter_modelElements_Activity = modelElements_Activity.iterator();
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
			java.util.Iterator<entity.flowgraph.SubFlowGraphDB> iter_modelElements_SubFlowGraph = modelElements_SubFlowGraph.iterator();
			while(iter_modelElements_SubFlowGraph.hasNext()) {
				entity.flowgraph.SubFlowGraphDB e = iter_modelElements_SubFlowGraph.next();
				if(e != null) {
					e.delete();
					modelElements_SubFlowGraph.remove(e);
				}
				iter_modelElements_SubFlowGraph = modelElements_SubFlowGraph.iterator();
			}
		} else {
			// clear all collections
			modelElements_Start.clear();
			modelElements_Activity.clear();
			modelElements_End.clear();
			modelElements_SubFlowGraph.clear();
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
		if(e instanceof entity.flowgraph.ActivityDB) {
			modelElements_Activity.add((entity.flowgraph.ActivityDB) e);
		} else 
		if(e instanceof entity.flowgraph.EndDB) {
			modelElements_End.add((entity.flowgraph.EndDB) e);
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			modelElements_SubFlowGraph.add((entity.flowgraph.SubFlowGraphDB) e);
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
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB definitiveEntity = (entity.flowgraph.SubFlowGraphDB) e;
			if(modelElements_SubFlowGraph.contains(definitiveEntity)) {
				boolean result = modelElements_SubFlowGraph.remove(definitiveEntity);
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
		if(e instanceof entity.flowgraph.ActivityDB) {
			entity.flowgraph.ActivityDB definitiveEntity = (entity.flowgraph.ActivityDB) e;
			return modelElements_Activity.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.EndDB) {
			entity.flowgraph.EndDB definitiveEntity = (entity.flowgraph.EndDB) e;
			return modelElements_End.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB definitiveEntity = (entity.flowgraph.SubFlowGraphDB) e;
			return modelElements_SubFlowGraph.contains(definitiveEntity);
		}
		return false;
	}
	
	public boolean isEmptyModelElements() {
		return getModelElements().isEmpty();
	}
	
	public int sizeModelElements() {
		return getModelElements().size();
	}
}
