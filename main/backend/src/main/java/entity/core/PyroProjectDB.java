package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name="entity_core_pyroproject")
public class PyroProjectDB extends entity.core.PyroFileContainerDB {
	
	public String name;
	
	public boolean isPublic;
	
	public String description;
	
	@javax.persistence.ManyToOne
	@javax.persistence.JoinColumn(name = "owner_PyroUserDB_id")
	public entity.core.PyroUserDB owner;
	
	@javax.persistence.ManyToOne
	@javax.persistence.JoinColumn(name = "organization_PyroOrganizationDB_id")
	public entity.core.PyroOrganizationDB organization;
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroFolderDB> innerFolders = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroBinaryFileDB> binaryFiles = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroURLFileDB> urlFiles = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.core.PyroTextualFileDB> textualFiles = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.empty.EmptyDB> files_Empty = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.primerefs.PrimeRefsDB> files_PrimeRefs = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.hierarchy.HierarchyDB> files_Hierarchy = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.hooksandactions.HooksAndActionsDB> files_HooksAndActions = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.flowgraph.FlowGraphDB> files_FlowGraph = new java.util.ArrayList<>();
	
	@javax.persistence.OneToMany(mappedBy="parent")
	public java.util.Collection<entity.externallibrary.ExternalLibraryDB> files_ExternalLibrary = new java.util.ArrayList<>();
	
	public java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> getProjectServices() {
		java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> services = new java.util.ArrayList<>();	
		return services;
	}
	
	public java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> getFiles() {
		java.util.Collection<io.quarkus.hibernate.orm.panache.PanacheEntity> files = new java.util.ArrayList<>();	
		files.addAll(binaryFiles);
		files.addAll(urlFiles);
		files.addAll(textualFiles);
		
		files.addAll(files_Empty);
		files.addAll(files_PrimeRefs);
		files.addAll(files_Hierarchy);
		files.addAll(files_HooksAndActions);
		files.addAll(files_FlowGraph);
		
		files.addAll(files_ExternalLibrary);
		
		return files;
	}
	
	public void addFile(PanacheEntity e) {
		if(e instanceof entity.core.PyroBinaryFileDB) {
			binaryFiles.add((entity.core.PyroBinaryFileDB) e);
		} else if(e instanceof entity.core.PyroURLFileDB) {
			urlFiles.add((entity.core.PyroURLFileDB) e);
		} else if(e instanceof entity.core.PyroTextualFileDB) {
			textualFiles.add((entity.core.PyroTextualFileDB) e);
		} else if(e instanceof entity.empty.EmptyDB) {
			files_Empty.add((entity.empty.EmptyDB) e);
		} else if(e instanceof entity.primerefs.PrimeRefsDB) {
			files_PrimeRefs.add((entity.primerefs.PrimeRefsDB) e);
		} else if(e instanceof entity.hierarchy.HierarchyDB) {
			files_Hierarchy.add((entity.hierarchy.HierarchyDB) e);
		} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
			files_HooksAndActions.add((entity.hooksandactions.HooksAndActionsDB) e);
		} else if(e instanceof entity.flowgraph.FlowGraphDB) {
			files_FlowGraph.add((entity.flowgraph.FlowGraphDB) e);
		} else if(e instanceof entity.externallibrary.ExternalLibraryDB) {
			files_ExternalLibrary.add((entity.externallibrary.ExternalLibraryDB) e);
		}
	}
	
	public boolean removeFile(PanacheEntity e, boolean delete) {
		if(e instanceof entity.core.PyroBinaryFileDB) {
			binaryFiles.remove((entity.core.PyroBinaryFileDB) e);
		} else if(e instanceof entity.core.PyroURLFileDB) {
			urlFiles.remove((entity.core.PyroURLFileDB) e);
		} else if(e instanceof entity.core.PyroTextualFileDB) {
			textualFiles.remove((entity.core.PyroTextualFileDB) e);
		} else if(e instanceof entity.empty.EmptyDB) {
			files_Empty.remove((entity.empty.EmptyDB) e);
		} else if(e instanceof entity.primerefs.PrimeRefsDB) {
			files_PrimeRefs.remove((entity.primerefs.PrimeRefsDB) e);
		} else if(e instanceof entity.hierarchy.HierarchyDB) {
			files_Hierarchy.remove((entity.hierarchy.HierarchyDB) e);
		} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
			files_HooksAndActions.remove((entity.hooksandactions.HooksAndActionsDB) e);
		} else if(e instanceof entity.flowgraph.FlowGraphDB) {
			files_FlowGraph.remove((entity.flowgraph.FlowGraphDB) e);
		} else if(e instanceof entity.externallibrary.ExternalLibraryDB) {
			files_ExternalLibrary.remove((entity.externallibrary.ExternalLibraryDB) e);
		}
		else {
			return false;
		}
		if(delete)
			e.delete();
		return true;
	}
}
