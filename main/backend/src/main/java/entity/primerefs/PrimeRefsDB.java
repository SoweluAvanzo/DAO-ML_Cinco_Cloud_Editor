package entity.primerefs;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_primerefs_primerefs")
public class PrimeRefsDB extends PanacheEntity {
	
	public String router;
	
	public String connector;
	
	public long width;
	
	public long height;
	
	public double scale;
	
	public boolean isPublic;
	
	public String filename;
	
	public String extension;
	
	@javax.persistence.ManyToOne
	public entity.core.PyroFileContainerDB parent;
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.SourceNodeDB> modelElements_SourceNode = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.SourceContainerDB> modelElements_SourceContainer = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToNodeDB> modelElements_PrimeToNode = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToEdgeDB> modelElements_PrimeToEdge = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToContainerDB> modelElements_PrimeToContainer = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToGraphModelDB> modelElements_PrimeToGraphModel = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToNodeDB> modelElements_PrimeCToNode = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToEdgeDB> modelElements_PrimeCToEdge = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToContainerDB> modelElements_PrimeCToContainer = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToGraphModelDB> modelElements_PrimeCToGraphModel = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToNodeHierarchyDB> modelElements_PrimeToNodeHierarchy = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToAbstractNodeHierarchyDB> modelElements_PrimeToAbstractNodeHierarchy = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToNodeFlowDB> modelElements_PrimeToNodeFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToEdgeFlowDB> modelElements_PrimeToEdgeFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToContainerFlowDB> modelElements_PrimeToContainerFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeToGraphModelFlowDB> modelElements_PrimeToGraphModelFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToNodeFlowDB> modelElements_PrimeCToNodeFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToEdgeFlowDB> modelElements_PrimeCToEdgeFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToContainerFlowDB> modelElements_PrimeCToContainerFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.PrimeCToGraphModelFlowDB> modelElements_PrimeCToGraphModelFlow = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="container_PrimeRefs")
	public java.util.Collection<entity.primerefs.SourceEdgeDB> modelElements_SourceEdge = new java.util.ArrayList<>();
	
	public java.util.Collection<PanacheEntity> getModelElements() {
		java.util.Collection<PanacheEntity> modelElements = new java.util.ArrayList<>();	
		modelElements.addAll(modelElements_SourceNode);
		modelElements.addAll(modelElements_SourceContainer);
		modelElements.addAll(modelElements_PrimeToNode);
		modelElements.addAll(modelElements_PrimeToEdge);
		modelElements.addAll(modelElements_PrimeToContainer);
		modelElements.addAll(modelElements_PrimeToGraphModel);
		modelElements.addAll(modelElements_PrimeCToNode);
		modelElements.addAll(modelElements_PrimeCToEdge);
		modelElements.addAll(modelElements_PrimeCToContainer);
		modelElements.addAll(modelElements_PrimeCToGraphModel);
		modelElements.addAll(modelElements_PrimeToNodeHierarchy);
		modelElements.addAll(modelElements_PrimeToAbstractNodeHierarchy);
		modelElements.addAll(modelElements_PrimeToNodeFlow);
		modelElements.addAll(modelElements_PrimeToEdgeFlow);
		modelElements.addAll(modelElements_PrimeToContainerFlow);
		modelElements.addAll(modelElements_PrimeToGraphModelFlow);
		modelElements.addAll(modelElements_PrimeCToNodeFlow);
		modelElements.addAll(modelElements_PrimeCToEdgeFlow);
		modelElements.addAll(modelElements_PrimeCToContainerFlow);
		modelElements.addAll(modelElements_PrimeCToGraphModelFlow);
		modelElements.addAll(modelElements_SourceEdge);
		return modelElements;
	}
	
	public void clearModelElements() {
		clearModelElements(false);
	}
	
	public void clearModelElements(boolean delete) {
		if(delete) {
			// delete all entries
			java.util.Iterator<entity.primerefs.SourceNodeDB> iter_modelElements_SourceNode = modelElements_SourceNode.iterator();
			while(iter_modelElements_SourceNode.hasNext()) {
				entity.primerefs.SourceNodeDB e = iter_modelElements_SourceNode.next();
				if(e != null) {
					e.delete();
					modelElements_SourceNode.remove(e);
				}
				iter_modelElements_SourceNode = modelElements_SourceNode.iterator();
			}
			java.util.Iterator<entity.primerefs.SourceContainerDB> iter_modelElements_SourceContainer = modelElements_SourceContainer.iterator();
			while(iter_modelElements_SourceContainer.hasNext()) {
				entity.primerefs.SourceContainerDB e = iter_modelElements_SourceContainer.next();
				if(e != null) {
					e.delete();
					modelElements_SourceContainer.remove(e);
				}
				iter_modelElements_SourceContainer = modelElements_SourceContainer.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToNodeDB> iter_modelElements_PrimeToNode = modelElements_PrimeToNode.iterator();
			while(iter_modelElements_PrimeToNode.hasNext()) {
				entity.primerefs.PrimeToNodeDB e = iter_modelElements_PrimeToNode.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToNode.remove(e);
				}
				iter_modelElements_PrimeToNode = modelElements_PrimeToNode.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToEdgeDB> iter_modelElements_PrimeToEdge = modelElements_PrimeToEdge.iterator();
			while(iter_modelElements_PrimeToEdge.hasNext()) {
				entity.primerefs.PrimeToEdgeDB e = iter_modelElements_PrimeToEdge.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToEdge.remove(e);
				}
				iter_modelElements_PrimeToEdge = modelElements_PrimeToEdge.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToContainerDB> iter_modelElements_PrimeToContainer = modelElements_PrimeToContainer.iterator();
			while(iter_modelElements_PrimeToContainer.hasNext()) {
				entity.primerefs.PrimeToContainerDB e = iter_modelElements_PrimeToContainer.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToContainer.remove(e);
				}
				iter_modelElements_PrimeToContainer = modelElements_PrimeToContainer.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToGraphModelDB> iter_modelElements_PrimeToGraphModel = modelElements_PrimeToGraphModel.iterator();
			while(iter_modelElements_PrimeToGraphModel.hasNext()) {
				entity.primerefs.PrimeToGraphModelDB e = iter_modelElements_PrimeToGraphModel.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToGraphModel.remove(e);
				}
				iter_modelElements_PrimeToGraphModel = modelElements_PrimeToGraphModel.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToNodeDB> iter_modelElements_PrimeCToNode = modelElements_PrimeCToNode.iterator();
			while(iter_modelElements_PrimeCToNode.hasNext()) {
				entity.primerefs.PrimeCToNodeDB e = iter_modelElements_PrimeCToNode.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToNode.remove(e);
				}
				iter_modelElements_PrimeCToNode = modelElements_PrimeCToNode.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToEdgeDB> iter_modelElements_PrimeCToEdge = modelElements_PrimeCToEdge.iterator();
			while(iter_modelElements_PrimeCToEdge.hasNext()) {
				entity.primerefs.PrimeCToEdgeDB e = iter_modelElements_PrimeCToEdge.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToEdge.remove(e);
				}
				iter_modelElements_PrimeCToEdge = modelElements_PrimeCToEdge.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToContainerDB> iter_modelElements_PrimeCToContainer = modelElements_PrimeCToContainer.iterator();
			while(iter_modelElements_PrimeCToContainer.hasNext()) {
				entity.primerefs.PrimeCToContainerDB e = iter_modelElements_PrimeCToContainer.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToContainer.remove(e);
				}
				iter_modelElements_PrimeCToContainer = modelElements_PrimeCToContainer.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToGraphModelDB> iter_modelElements_PrimeCToGraphModel = modelElements_PrimeCToGraphModel.iterator();
			while(iter_modelElements_PrimeCToGraphModel.hasNext()) {
				entity.primerefs.PrimeCToGraphModelDB e = iter_modelElements_PrimeCToGraphModel.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToGraphModel.remove(e);
				}
				iter_modelElements_PrimeCToGraphModel = modelElements_PrimeCToGraphModel.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToNodeHierarchyDB> iter_modelElements_PrimeToNodeHierarchy = modelElements_PrimeToNodeHierarchy.iterator();
			while(iter_modelElements_PrimeToNodeHierarchy.hasNext()) {
				entity.primerefs.PrimeToNodeHierarchyDB e = iter_modelElements_PrimeToNodeHierarchy.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToNodeHierarchy.remove(e);
				}
				iter_modelElements_PrimeToNodeHierarchy = modelElements_PrimeToNodeHierarchy.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToAbstractNodeHierarchyDB> iter_modelElements_PrimeToAbstractNodeHierarchy = modelElements_PrimeToAbstractNodeHierarchy.iterator();
			while(iter_modelElements_PrimeToAbstractNodeHierarchy.hasNext()) {
				entity.primerefs.PrimeToAbstractNodeHierarchyDB e = iter_modelElements_PrimeToAbstractNodeHierarchy.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToAbstractNodeHierarchy.remove(e);
				}
				iter_modelElements_PrimeToAbstractNodeHierarchy = modelElements_PrimeToAbstractNodeHierarchy.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToNodeFlowDB> iter_modelElements_PrimeToNodeFlow = modelElements_PrimeToNodeFlow.iterator();
			while(iter_modelElements_PrimeToNodeFlow.hasNext()) {
				entity.primerefs.PrimeToNodeFlowDB e = iter_modelElements_PrimeToNodeFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToNodeFlow.remove(e);
				}
				iter_modelElements_PrimeToNodeFlow = modelElements_PrimeToNodeFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToEdgeFlowDB> iter_modelElements_PrimeToEdgeFlow = modelElements_PrimeToEdgeFlow.iterator();
			while(iter_modelElements_PrimeToEdgeFlow.hasNext()) {
				entity.primerefs.PrimeToEdgeFlowDB e = iter_modelElements_PrimeToEdgeFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToEdgeFlow.remove(e);
				}
				iter_modelElements_PrimeToEdgeFlow = modelElements_PrimeToEdgeFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToContainerFlowDB> iter_modelElements_PrimeToContainerFlow = modelElements_PrimeToContainerFlow.iterator();
			while(iter_modelElements_PrimeToContainerFlow.hasNext()) {
				entity.primerefs.PrimeToContainerFlowDB e = iter_modelElements_PrimeToContainerFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToContainerFlow.remove(e);
				}
				iter_modelElements_PrimeToContainerFlow = modelElements_PrimeToContainerFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeToGraphModelFlowDB> iter_modelElements_PrimeToGraphModelFlow = modelElements_PrimeToGraphModelFlow.iterator();
			while(iter_modelElements_PrimeToGraphModelFlow.hasNext()) {
				entity.primerefs.PrimeToGraphModelFlowDB e = iter_modelElements_PrimeToGraphModelFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeToGraphModelFlow.remove(e);
				}
				iter_modelElements_PrimeToGraphModelFlow = modelElements_PrimeToGraphModelFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToNodeFlowDB> iter_modelElements_PrimeCToNodeFlow = modelElements_PrimeCToNodeFlow.iterator();
			while(iter_modelElements_PrimeCToNodeFlow.hasNext()) {
				entity.primerefs.PrimeCToNodeFlowDB e = iter_modelElements_PrimeCToNodeFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToNodeFlow.remove(e);
				}
				iter_modelElements_PrimeCToNodeFlow = modelElements_PrimeCToNodeFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToEdgeFlowDB> iter_modelElements_PrimeCToEdgeFlow = modelElements_PrimeCToEdgeFlow.iterator();
			while(iter_modelElements_PrimeCToEdgeFlow.hasNext()) {
				entity.primerefs.PrimeCToEdgeFlowDB e = iter_modelElements_PrimeCToEdgeFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToEdgeFlow.remove(e);
				}
				iter_modelElements_PrimeCToEdgeFlow = modelElements_PrimeCToEdgeFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToContainerFlowDB> iter_modelElements_PrimeCToContainerFlow = modelElements_PrimeCToContainerFlow.iterator();
			while(iter_modelElements_PrimeCToContainerFlow.hasNext()) {
				entity.primerefs.PrimeCToContainerFlowDB e = iter_modelElements_PrimeCToContainerFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToContainerFlow.remove(e);
				}
				iter_modelElements_PrimeCToContainerFlow = modelElements_PrimeCToContainerFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.PrimeCToGraphModelFlowDB> iter_modelElements_PrimeCToGraphModelFlow = modelElements_PrimeCToGraphModelFlow.iterator();
			while(iter_modelElements_PrimeCToGraphModelFlow.hasNext()) {
				entity.primerefs.PrimeCToGraphModelFlowDB e = iter_modelElements_PrimeCToGraphModelFlow.next();
				if(e != null) {
					e.delete();
					modelElements_PrimeCToGraphModelFlow.remove(e);
				}
				iter_modelElements_PrimeCToGraphModelFlow = modelElements_PrimeCToGraphModelFlow.iterator();
			}
			java.util.Iterator<entity.primerefs.SourceEdgeDB> iter_modelElements_SourceEdge = modelElements_SourceEdge.iterator();
			while(iter_modelElements_SourceEdge.hasNext()) {
				entity.primerefs.SourceEdgeDB e = iter_modelElements_SourceEdge.next();
				if(e != null) {
					e.delete();
					modelElements_SourceEdge.remove(e);
				}
				iter_modelElements_SourceEdge = modelElements_SourceEdge.iterator();
			}
		} else {
			// clear all collections
			modelElements_SourceNode.clear();
			modelElements_SourceContainer.clear();
			modelElements_PrimeToNode.clear();
			modelElements_PrimeToEdge.clear();
			modelElements_PrimeToContainer.clear();
			modelElements_PrimeToGraphModel.clear();
			modelElements_PrimeCToNode.clear();
			modelElements_PrimeCToEdge.clear();
			modelElements_PrimeCToContainer.clear();
			modelElements_PrimeCToGraphModel.clear();
			modelElements_PrimeToNodeHierarchy.clear();
			modelElements_PrimeToAbstractNodeHierarchy.clear();
			modelElements_PrimeToNodeFlow.clear();
			modelElements_PrimeToEdgeFlow.clear();
			modelElements_PrimeToContainerFlow.clear();
			modelElements_PrimeToGraphModelFlow.clear();
			modelElements_PrimeCToNodeFlow.clear();
			modelElements_PrimeCToEdgeFlow.clear();
			modelElements_PrimeCToContainerFlow.clear();
			modelElements_PrimeCToGraphModelFlow.clear();
			modelElements_SourceEdge.clear();
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
		if(e instanceof entity.primerefs.SourceNodeDB) {
			modelElements_SourceNode.add((entity.primerefs.SourceNodeDB) e);
		} else 
		if(e instanceof entity.primerefs.SourceContainerDB) {
			modelElements_SourceContainer.add((entity.primerefs.SourceContainerDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeDB) {
			modelElements_PrimeToNode.add((entity.primerefs.PrimeToNodeDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToEdgeDB) {
			modelElements_PrimeToEdge.add((entity.primerefs.PrimeToEdgeDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToContainerDB) {
			modelElements_PrimeToContainer.add((entity.primerefs.PrimeToContainerDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToGraphModelDB) {
			modelElements_PrimeToGraphModel.add((entity.primerefs.PrimeToGraphModelDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToNodeDB) {
			modelElements_PrimeCToNode.add((entity.primerefs.PrimeCToNodeDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToEdgeDB) {
			modelElements_PrimeCToEdge.add((entity.primerefs.PrimeCToEdgeDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToContainerDB) {
			modelElements_PrimeCToContainer.add((entity.primerefs.PrimeCToContainerDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToGraphModelDB) {
			modelElements_PrimeCToGraphModel.add((entity.primerefs.PrimeCToGraphModelDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
			modelElements_PrimeToNodeHierarchy.add((entity.primerefs.PrimeToNodeHierarchyDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
			modelElements_PrimeToAbstractNodeHierarchy.add((entity.primerefs.PrimeToAbstractNodeHierarchyDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeFlowDB) {
			modelElements_PrimeToNodeFlow.add((entity.primerefs.PrimeToNodeFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToEdgeFlowDB) {
			modelElements_PrimeToEdgeFlow.add((entity.primerefs.PrimeToEdgeFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToContainerFlowDB) {
			modelElements_PrimeToContainerFlow.add((entity.primerefs.PrimeToContainerFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
			modelElements_PrimeToGraphModelFlow.add((entity.primerefs.PrimeToGraphModelFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToNodeFlowDB) {
			modelElements_PrimeCToNodeFlow.add((entity.primerefs.PrimeCToNodeFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
			modelElements_PrimeCToEdgeFlow.add((entity.primerefs.PrimeCToEdgeFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToContainerFlowDB) {
			modelElements_PrimeCToContainerFlow.add((entity.primerefs.PrimeCToContainerFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
			modelElements_PrimeCToGraphModelFlow.add((entity.primerefs.PrimeCToGraphModelFlowDB) e);
		} else 
		if(e instanceof entity.primerefs.SourceEdgeDB) {
			modelElements_SourceEdge.add((entity.primerefs.SourceEdgeDB) e);
		}
	}
	
	public boolean removeModelElements(PanacheEntity e) {
		return removeModelElements(e, false);
	}
	
	public boolean removeModelElements(PanacheEntity e, boolean delete) {
		// removes the entity from it's type-specific list
		if(e instanceof entity.primerefs.SourceNodeDB) {
			entity.primerefs.SourceNodeDB definitiveEntity = (entity.primerefs.SourceNodeDB) e;
			if(modelElements_SourceNode.contains(definitiveEntity)) {
				boolean result = modelElements_SourceNode.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.SourceContainerDB) {
			entity.primerefs.SourceContainerDB definitiveEntity = (entity.primerefs.SourceContainerDB) e;
			if(modelElements_SourceContainer.contains(definitiveEntity)) {
				boolean result = modelElements_SourceContainer.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeDB) {
			entity.primerefs.PrimeToNodeDB definitiveEntity = (entity.primerefs.PrimeToNodeDB) e;
			if(modelElements_PrimeToNode.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToNode.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToEdgeDB) {
			entity.primerefs.PrimeToEdgeDB definitiveEntity = (entity.primerefs.PrimeToEdgeDB) e;
			if(modelElements_PrimeToEdge.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToEdge.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToContainerDB) {
			entity.primerefs.PrimeToContainerDB definitiveEntity = (entity.primerefs.PrimeToContainerDB) e;
			if(modelElements_PrimeToContainer.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToContainer.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToGraphModelDB) {
			entity.primerefs.PrimeToGraphModelDB definitiveEntity = (entity.primerefs.PrimeToGraphModelDB) e;
			if(modelElements_PrimeToGraphModel.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToGraphModel.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToNodeDB) {
			entity.primerefs.PrimeCToNodeDB definitiveEntity = (entity.primerefs.PrimeCToNodeDB) e;
			if(modelElements_PrimeCToNode.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToNode.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToEdgeDB) {
			entity.primerefs.PrimeCToEdgeDB definitiveEntity = (entity.primerefs.PrimeCToEdgeDB) e;
			if(modelElements_PrimeCToEdge.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToEdge.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToContainerDB) {
			entity.primerefs.PrimeCToContainerDB definitiveEntity = (entity.primerefs.PrimeCToContainerDB) e;
			if(modelElements_PrimeCToContainer.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToContainer.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToGraphModelDB) {
			entity.primerefs.PrimeCToGraphModelDB definitiveEntity = (entity.primerefs.PrimeCToGraphModelDB) e;
			if(modelElements_PrimeCToGraphModel.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToGraphModel.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
			entity.primerefs.PrimeToNodeHierarchyDB definitiveEntity = (entity.primerefs.PrimeToNodeHierarchyDB) e;
			if(modelElements_PrimeToNodeHierarchy.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToNodeHierarchy.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
			entity.primerefs.PrimeToAbstractNodeHierarchyDB definitiveEntity = (entity.primerefs.PrimeToAbstractNodeHierarchyDB) e;
			if(modelElements_PrimeToAbstractNodeHierarchy.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToAbstractNodeHierarchy.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeFlowDB) {
			entity.primerefs.PrimeToNodeFlowDB definitiveEntity = (entity.primerefs.PrimeToNodeFlowDB) e;
			if(modelElements_PrimeToNodeFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToNodeFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToEdgeFlowDB) {
			entity.primerefs.PrimeToEdgeFlowDB definitiveEntity = (entity.primerefs.PrimeToEdgeFlowDB) e;
			if(modelElements_PrimeToEdgeFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToEdgeFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToContainerFlowDB) {
			entity.primerefs.PrimeToContainerFlowDB definitiveEntity = (entity.primerefs.PrimeToContainerFlowDB) e;
			if(modelElements_PrimeToContainerFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToContainerFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
			entity.primerefs.PrimeToGraphModelFlowDB definitiveEntity = (entity.primerefs.PrimeToGraphModelFlowDB) e;
			if(modelElements_PrimeToGraphModelFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeToGraphModelFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToNodeFlowDB) {
			entity.primerefs.PrimeCToNodeFlowDB definitiveEntity = (entity.primerefs.PrimeCToNodeFlowDB) e;
			if(modelElements_PrimeCToNodeFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToNodeFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
			entity.primerefs.PrimeCToEdgeFlowDB definitiveEntity = (entity.primerefs.PrimeCToEdgeFlowDB) e;
			if(modelElements_PrimeCToEdgeFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToEdgeFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToContainerFlowDB) {
			entity.primerefs.PrimeCToContainerFlowDB definitiveEntity = (entity.primerefs.PrimeCToContainerFlowDB) e;
			if(modelElements_PrimeCToContainerFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToContainerFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
			entity.primerefs.PrimeCToGraphModelFlowDB definitiveEntity = (entity.primerefs.PrimeCToGraphModelFlowDB) e;
			if(modelElements_PrimeCToGraphModelFlow.contains(definitiveEntity)) {
				boolean result = modelElements_PrimeCToGraphModelFlow.remove(definitiveEntity);
				if(delete && result) {
					definitiveEntity.delete();
				}
				return result;
			}
		} else 
		if(e instanceof entity.primerefs.SourceEdgeDB) {
			entity.primerefs.SourceEdgeDB definitiveEntity = (entity.primerefs.SourceEdgeDB) e;
			if(modelElements_SourceEdge.contains(definitiveEntity)) {
				boolean result = modelElements_SourceEdge.remove(definitiveEntity);
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
		if(e instanceof entity.primerefs.SourceNodeDB) {
			entity.primerefs.SourceNodeDB definitiveEntity = (entity.primerefs.SourceNodeDB) e;
			return modelElements_SourceNode.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.SourceContainerDB) {
			entity.primerefs.SourceContainerDB definitiveEntity = (entity.primerefs.SourceContainerDB) e;
			return modelElements_SourceContainer.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeDB) {
			entity.primerefs.PrimeToNodeDB definitiveEntity = (entity.primerefs.PrimeToNodeDB) e;
			return modelElements_PrimeToNode.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToEdgeDB) {
			entity.primerefs.PrimeToEdgeDB definitiveEntity = (entity.primerefs.PrimeToEdgeDB) e;
			return modelElements_PrimeToEdge.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToContainerDB) {
			entity.primerefs.PrimeToContainerDB definitiveEntity = (entity.primerefs.PrimeToContainerDB) e;
			return modelElements_PrimeToContainer.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToGraphModelDB) {
			entity.primerefs.PrimeToGraphModelDB definitiveEntity = (entity.primerefs.PrimeToGraphModelDB) e;
			return modelElements_PrimeToGraphModel.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToNodeDB) {
			entity.primerefs.PrimeCToNodeDB definitiveEntity = (entity.primerefs.PrimeCToNodeDB) e;
			return modelElements_PrimeCToNode.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToEdgeDB) {
			entity.primerefs.PrimeCToEdgeDB definitiveEntity = (entity.primerefs.PrimeCToEdgeDB) e;
			return modelElements_PrimeCToEdge.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToContainerDB) {
			entity.primerefs.PrimeCToContainerDB definitiveEntity = (entity.primerefs.PrimeCToContainerDB) e;
			return modelElements_PrimeCToContainer.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToGraphModelDB) {
			entity.primerefs.PrimeCToGraphModelDB definitiveEntity = (entity.primerefs.PrimeCToGraphModelDB) e;
			return modelElements_PrimeCToGraphModel.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
			entity.primerefs.PrimeToNodeHierarchyDB definitiveEntity = (entity.primerefs.PrimeToNodeHierarchyDB) e;
			return modelElements_PrimeToNodeHierarchy.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
			entity.primerefs.PrimeToAbstractNodeHierarchyDB definitiveEntity = (entity.primerefs.PrimeToAbstractNodeHierarchyDB) e;
			return modelElements_PrimeToAbstractNodeHierarchy.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToNodeFlowDB) {
			entity.primerefs.PrimeToNodeFlowDB definitiveEntity = (entity.primerefs.PrimeToNodeFlowDB) e;
			return modelElements_PrimeToNodeFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToEdgeFlowDB) {
			entity.primerefs.PrimeToEdgeFlowDB definitiveEntity = (entity.primerefs.PrimeToEdgeFlowDB) e;
			return modelElements_PrimeToEdgeFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToContainerFlowDB) {
			entity.primerefs.PrimeToContainerFlowDB definitiveEntity = (entity.primerefs.PrimeToContainerFlowDB) e;
			return modelElements_PrimeToContainerFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
			entity.primerefs.PrimeToGraphModelFlowDB definitiveEntity = (entity.primerefs.PrimeToGraphModelFlowDB) e;
			return modelElements_PrimeToGraphModelFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToNodeFlowDB) {
			entity.primerefs.PrimeCToNodeFlowDB definitiveEntity = (entity.primerefs.PrimeCToNodeFlowDB) e;
			return modelElements_PrimeCToNodeFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
			entity.primerefs.PrimeCToEdgeFlowDB definitiveEntity = (entity.primerefs.PrimeCToEdgeFlowDB) e;
			return modelElements_PrimeCToEdgeFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToContainerFlowDB) {
			entity.primerefs.PrimeCToContainerFlowDB definitiveEntity = (entity.primerefs.PrimeCToContainerFlowDB) e;
			return modelElements_PrimeCToContainerFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
			entity.primerefs.PrimeCToGraphModelFlowDB definitiveEntity = (entity.primerefs.PrimeCToGraphModelFlowDB) e;
			return modelElements_PrimeCToGraphModelFlow.contains(definitiveEntity);
		} else 
		if(e instanceof entity.primerefs.SourceEdgeDB) {
			entity.primerefs.SourceEdgeDB definitiveEntity = (entity.primerefs.SourceEdgeDB) e;
			return modelElements_SourceEdge.contains(definitiveEntity);
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
		
		// delete entity
		super.delete();
	}
}
