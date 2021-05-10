package entity.flowgraph;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_flowgraph_swimlane")
public class SwimlaneDB extends PanacheEntity {
	
	public long x;
	
	public long y;
	
	public long width;
	
	public long height;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.SwimlaneDB container_Swimlane;
	
	@javax.persistence.ManyToOne
	public entity.flowgraph.FlowGraphDB container_FlowGraph;
	
	public String actor;
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.StartDB> modelElements_Start = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.ActivityDB> modelElements_Activity = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.EndDB> modelElements_End = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.EActivityADB> modelElements_EActivityA = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.EActivityBDB> modelElements_EActivityB = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.SubFlowGraphDB> modelElements_SubFlowGraph = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_Swimlane")
	public java.util.Collection<entity.flowgraph.SwimlaneDB> modelElements_Swimlane = new java.util.ArrayList<>();
	
	public PanacheEntity getContainer() {
		if(container_Swimlane != null) {
			return container_Swimlane;
		} else if(container_FlowGraph != null) {
			return container_FlowGraph;
		}
		return null;
	}
	
	public void setContainer(PanacheEntity e) {
		setContainer(e, false);
	}
	
	public void setContainer(PanacheEntity e, boolean deleteOld) {
		// potencially delete all old elements
		if(deleteOld) {
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(container_Swimlane != null && !container_Swimlane.equals(e)) {
				container_Swimlane.delete();
			}
			// if no element to delete or the element to delete is
			// same that will be set then dont delete
			if(container_FlowGraph != null && !container_FlowGraph.equals(e)) {
				container_FlowGraph.delete();
			}
		}
		
		// set new and null others
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			// null all other types
			container_FlowGraph = null;
			// set element
			container_Swimlane = (entity.flowgraph.SwimlaneDB) e;
			return;
		} else if(e instanceof entity.flowgraph.FlowGraphDB) {
			// null all other types
			container_Swimlane = null;
			// set element
			container_FlowGraph = (entity.flowgraph.FlowGraphDB) e;
			return;
		}
		
		// default-case
		// null all types
		container_Swimlane = null;
		container_FlowGraph = null;
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
	
	@Override
	public void delete() {
		// clear and delete all contained modelElements
		this.clearModelElements(true);
		
		// decouple from container
		PanacheEntity c = this.getContainer();
		if(c instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB container = (entity.flowgraph.SwimlaneDB) c;
			container.removeModelElements(this);
			container.persist();
			this.setContainer(null);
		}
		if(c instanceof entity.flowgraph.FlowGraphDB) {
			entity.flowgraph.FlowGraphDB container = (entity.flowgraph.FlowGraphDB) c;
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
		modelElements.addAll(modelElements_EActivityA);
		modelElements.addAll(modelElements_EActivityB);
		modelElements.addAll(modelElements_SubFlowGraph);
		modelElements.addAll(modelElements_Swimlane);
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
			java.util.Iterator<entity.flowgraph.EActivityADB> iter_modelElements_EActivityA = modelElements_EActivityA.iterator();
			while(iter_modelElements_EActivityA.hasNext()) {
				entity.flowgraph.EActivityADB e = iter_modelElements_EActivityA.next();
				if(e != null) {
					e.delete();
					modelElements_EActivityA.remove(e);
				}
				iter_modelElements_EActivityA = modelElements_EActivityA.iterator();
			}
			java.util.Iterator<entity.flowgraph.EActivityBDB> iter_modelElements_EActivityB = modelElements_EActivityB.iterator();
			while(iter_modelElements_EActivityB.hasNext()) {
				entity.flowgraph.EActivityBDB e = iter_modelElements_EActivityB.next();
				if(e != null) {
					e.delete();
					modelElements_EActivityB.remove(e);
				}
				iter_modelElements_EActivityB = modelElements_EActivityB.iterator();
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
			java.util.Iterator<entity.flowgraph.SwimlaneDB> iter_modelElements_Swimlane = modelElements_Swimlane.iterator();
			while(iter_modelElements_Swimlane.hasNext()) {
				entity.flowgraph.SwimlaneDB e = iter_modelElements_Swimlane.next();
				if(e != null) {
					e.delete();
					modelElements_Swimlane.remove(e);
				}
				iter_modelElements_Swimlane = modelElements_Swimlane.iterator();
			}
		} else {
			// clear all collections
			modelElements_Start.clear();
			modelElements_Activity.clear();
			modelElements_End.clear();
			modelElements_EActivityA.clear();
			modelElements_EActivityB.clear();
			modelElements_SubFlowGraph.clear();
			modelElements_Swimlane.clear();
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
		if(e instanceof entity.flowgraph.EActivityADB) {
			modelElements_EActivityA.add((entity.flowgraph.EActivityADB) e);
		} else 
		if(e instanceof entity.flowgraph.EActivityBDB) {
			modelElements_EActivityB.add((entity.flowgraph.EActivityBDB) e);
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			modelElements_SubFlowGraph.add((entity.flowgraph.SubFlowGraphDB) e);
		} else 
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			modelElements_Swimlane.add((entity.flowgraph.SwimlaneDB) e);
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
		if(e instanceof entity.flowgraph.EActivityADB) {
			entity.flowgraph.EActivityADB definitiveEntity = (entity.flowgraph.EActivityADB) e;
			if(modelElements_EActivityA.contains(definitiveEntity)) {
				boolean result = modelElements_EActivityA.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.flowgraph.EActivityBDB) {
			entity.flowgraph.EActivityBDB definitiveEntity = (entity.flowgraph.EActivityBDB) e;
			if(modelElements_EActivityB.contains(definitiveEntity)) {
				boolean result = modelElements_EActivityB.remove(definitiveEntity);
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
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB definitiveEntity = (entity.flowgraph.SwimlaneDB) e;
			if(modelElements_Swimlane.contains(definitiveEntity)) {
				boolean result = modelElements_Swimlane.remove(definitiveEntity);
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
		if(e instanceof entity.flowgraph.EActivityADB) {
			entity.flowgraph.EActivityADB definitiveEntity = (entity.flowgraph.EActivityADB) e;
			return modelElements_EActivityA.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.EActivityBDB) {
			entity.flowgraph.EActivityBDB definitiveEntity = (entity.flowgraph.EActivityBDB) e;
			return modelElements_EActivityB.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.SubFlowGraphDB) {
			entity.flowgraph.SubFlowGraphDB definitiveEntity = (entity.flowgraph.SubFlowGraphDB) e;
			return modelElements_SubFlowGraph.contains(definitiveEntity);
		} else 
		if(e instanceof entity.flowgraph.SwimlaneDB) {
			entity.flowgraph.SwimlaneDB definitiveEntity = (entity.flowgraph.SwimlaneDB) e;
			return modelElements_Swimlane.contains(definitiveEntity);
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
