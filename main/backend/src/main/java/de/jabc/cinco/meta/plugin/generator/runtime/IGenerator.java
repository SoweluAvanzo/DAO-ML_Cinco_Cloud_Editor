package de.jabc.cinco.meta.plugin.generator.runtime;

import entity.core.*;
import graphmodel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import info.scce.pyro.core.rest.types.PyroProject;
import org.apache.commons.io.FileUtils;


/**
 * Author zweihoff
 */
public abstract class IGenerator<T extends GraphModel> {

	private List<GeneratedFile> files;
	
	private PyroProjectDB root;
	private String basePath;
	
	private info.scce.pyro.core.FileController fileController;
	
	
	public IGenerator() {
		files = new LinkedList<>();
	}
	
	public final void generateFiles(T graphModel,PyroProjectDB root, String basePath,String staticResourceBase,java.util.Map<String,String[]> staticResources,info.scce.pyro.core.FileController fileController) throws IOException {
		this.root = root;
		this.basePath = basePath;
		this.fileController = fileController;
		
		generate(graphModel);
		//get generation base folder
		Object generationBaseFolder = getFolder(basePath,root);
		if(generationBaseFolder==null) {
			generationBaseFolder = createFolder(basePath,root);
		}
		
		//copy and overwrite with static resources
		for(java.util.Map.Entry<String,String[]> staticResource:staticResources.entrySet())
		{
			for(String url:staticResource.getValue()) {
				
				String file = url;
				String folder = "";
				String extension = "";
				String filename = file;
				if(file.contains("/")) {
					file = url.substring(url.lastIndexOf("/")+1);
					folder = url.substring(0,url.lastIndexOf("/"));
				}

				if(file.contains(".")) {
					extension = file.substring(file.lastIndexOf(".")+1);
					filename = file.substring(0,file.lastIndexOf("."));
				}

				Object pf = createFolder(folder,generationBaseFolder);
				
				final String fFilename = filename;
				final String fExtension = extension;
				
				deletePriorFile(pf,fFilename,fExtension);
				
				PyroURLFileDB pyroURLFile = new PyroURLFileDB();
				pyroURLFile.filename = filename;
				pyroURLFile.url = staticResourceBase+"/"+staticResource.getKey()+"/"+url;
				pyroURLFile.extension = extension;
				if(pf instanceof PyroProjectDB) {
				    pyroURLFile.parent = (PyroProjectDB)pf;
				    pyroURLFile.persist();
					((PyroProjectDB)pf).urlFiles.add(pyroURLFile);
					((PyroProjectDB)pf).persist();
				}
				else if(pf instanceof PyroFolderDB) {
				    pyroURLFile.parent = (PyroFolderDB)pf;
				    pyroURLFile.persist();
					((PyroFolderDB)pf).urlFiles.add(pyroURLFile);
					((PyroFolderDB)pf).persist();
				}
        	}

		}
		
		//generate files and overwrite existing
		for(GeneratedFile gf:files)
		{
			final String extension = gf.getFilename().substring(gf.getFilename().lastIndexOf(".")+1);
			final String filename = gf.getFilename().substring(0,gf.getFilename().lastIndexOf("."));
			
			Object pf = createFolder(gf.getPath(),generationBaseFolder);
			//delete prior file
			deletePriorFile(pf, filename, extension);
			
			BaseFileDB fr = null;
			if(gf.getContent()!=null) {
				
				InputStream stream = new ByteArrayInputStream(gf.getContent().getBytes(StandardCharsets.UTF_8));
				fr = fileController.storeFile(gf.getFilename(),stream);
			} else {
				fr = fileController.storeFile(gf.getFilename(),new FileInputStream(gf.getFile()));
            }
            
			PyroBinaryFileDB pyroBinaryFile = new PyroBinaryFileDB();
			pyroBinaryFile.filename = filename;
			pyroBinaryFile.file = fr;
			pyroBinaryFile.extension = extension;
			if(pf instanceof PyroProjectDB) {
			    pyroBinaryFile.parent = (PyroProjectDB)pf;
			    pyroBinaryFile.persist();
			    ((PyroProjectDB)pf).binaryFiles.add(pyroBinaryFile);
			    ((PyroProjectDB)pf).persist();
			}
			else if(pf instanceof PyroFolderDB) {
			    pyroBinaryFile.parent = (PyroFolderDB)pf;
			    pyroBinaryFile.persist();
			    ((PyroFolderDB)pf).binaryFiles.add(pyroBinaryFile);
			    ((PyroFolderDB)pf).persist();
			}
		}
	}

	private void deletePriorFile(Object pf,String filename, String extension) {
	    if(pf instanceof PyroProjectDB) {
	        deletePriorFile((PyroProjectDB) pf,filename,extension);
	    }
	    if(pf instanceof PyroFolderDB) {
	        deletePriorFile((PyroFolderDB) pf,filename,extension);
	    }
	}

	private void deletePriorFile(PyroFolderDB pf,String filename, String extension) {
		//delete prior file
		{
		    Optional<PyroBinaryFileDB> optFile = pf.binaryFiles.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
		    if(optFile.isPresent()) {
		        PyroBinaryFileDB pyroFile = optFile.get();
		        pf.binaryFiles.remove(pyroFile);
		        pyroFile.file.delete();
		        pyroFile.delete();
		    }
		}
		{
		    Optional<PyroURLFileDB> optFile = pf.urlFiles.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
		    if(optFile.isPresent()) {
		        PyroURLFileDB pyroFile = optFile.get();
		        pf.urlFiles.remove(pyroFile);
		        pyroFile.delete();
		    }
		}
		{
		    Optional<PyroTextualFileDB> optFile = pf.textualFiles.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
		    if(optFile.isPresent()) {
		        PyroTextualFileDB pyroFile = optFile.get();
		        pf.textualFiles.remove(pyroFile);
		        pyroFile.delete();
		    }
		}
		{
			Optional<entity.empty.EmptyDB> optFile = pf.files_Empty.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.empty.EmptyDB graph = optFile.get();
				//info.scce.cinco.product.empty.empty.impl.EmptyImpl graphImpl = new info.scce.cinco.product.empty.empty.impl.EmptyImpl(graph,null);
				pf.files_Empty.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.primerefs.PrimeRefsDB> optFile = pf.files_PrimeRefs.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.primerefs.PrimeRefsDB graph = optFile.get();
				//info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl graphImpl = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl(graph,null);
				pf.files_PrimeRefs.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.hierarchy.HierarchyDB> optFile = pf.files_Hierarchy.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.hierarchy.HierarchyDB graph = optFile.get();
				//info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl graphImpl = new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(graph,null);
				pf.files_Hierarchy.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.hooksandactions.HooksAndActionsDB> optFile = pf.files_HooksAndActions.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.hooksandactions.HooksAndActionsDB graph = optFile.get();
				//info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl graphImpl = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(graph,null);
				pf.files_HooksAndActions.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.flowgraph.FlowGraphDB> optFile = pf.files_FlowGraph.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.flowgraph.FlowGraphDB graph = optFile.get();
				//info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphImpl graphImpl = new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphImpl(graph,null);
				pf.files_FlowGraph.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.externallibrary.ExternalLibraryDB> optFile = pf.files_ExternalLibrary.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.externallibrary.ExternalLibraryDB graph = optFile.get();
				//externallibrary.impl.ExternalLibraryImpl graphImpl = new externallibrary.impl.ExternalLibraryImpl(graph,null);
				pf.files_ExternalLibrary.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
	}
	
	private void deletePriorFile(PyroProjectDB pf,String filename, String extension) {
		//delete prior file
		{
		    Optional<PyroBinaryFileDB> optFile = pf.binaryFiles.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
		    if(optFile.isPresent()) {
		        PyroBinaryFileDB pyroFile = optFile.get();
		        pf.binaryFiles.remove(pyroFile);
		        pyroFile.file.delete();
		        pyroFile.delete();
		    }
		}
		{
		    Optional<PyroURLFileDB> optFile = pf.urlFiles.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
		    if(optFile.isPresent()) {
		        PyroURLFileDB pyroFile = optFile.get();
		        pf.urlFiles.remove(pyroFile);
		        pyroFile.delete();
		    }
		}
		{
		    Optional<PyroTextualFileDB> optFile = pf.textualFiles.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
		    if(optFile.isPresent()) {
		        PyroTextualFileDB pyroFile = optFile.get();
		        pf.textualFiles.remove(pyroFile);
		        pyroFile.delete();
		    }
		}
		{
			Optional<entity.empty.EmptyDB> optFile = pf.files_Empty.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.empty.EmptyDB graph = optFile.get();
				//info.scce.cinco.product.empty.empty.impl.EmptyImpl graphImpl = new info.scce.cinco.product.empty.empty.impl.EmptyImpl(graph,null);
				pf.files_Empty.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.primerefs.PrimeRefsDB> optFile = pf.files_PrimeRefs.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.primerefs.PrimeRefsDB graph = optFile.get();
				//info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl graphImpl = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl(graph,null);
				pf.files_PrimeRefs.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.hierarchy.HierarchyDB> optFile = pf.files_Hierarchy.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.hierarchy.HierarchyDB graph = optFile.get();
				//info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl graphImpl = new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(graph,null);
				pf.files_Hierarchy.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.hooksandactions.HooksAndActionsDB> optFile = pf.files_HooksAndActions.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.hooksandactions.HooksAndActionsDB graph = optFile.get();
				//info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl graphImpl = new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(graph,null);
				pf.files_HooksAndActions.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.flowgraph.FlowGraphDB> optFile = pf.files_FlowGraph.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.flowgraph.FlowGraphDB graph = optFile.get();
				//info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphImpl graphImpl = new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphImpl(graph,null);
				pf.files_FlowGraph.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
		{
			Optional<entity.externallibrary.ExternalLibraryDB> optFile = pf.files_ExternalLibrary.stream().filter(n->n.filename.equals(filename)&&n.extension.equals(extension)).findAny();
			if(optFile.isPresent()) {
				entity.externallibrary.ExternalLibraryDB graph = optFile.get();
				//externallibrary.impl.ExternalLibraryImpl graphImpl = new externallibrary.impl.ExternalLibraryImpl(graph,null);
				pf.files_ExternalLibrary.remove(graph);
				pf.persist();
				graph.delete();
			}
		}
	}
	
    public final File generateFilesTemporal(T graphModel) throws IOException {
        generate(graphModel);
        //create Files in temp directory
        String sourceBasepath = System.getProperty("java.io.tmpdir")+"/"+graphModel.getId()+"/sources/";
        String compressedBasepath = System.getProperty("java.io.tmpdir")+"/"+graphModel.getId()+"/compressed/generated.zip";
        File compressed = new File(compressedBasepath);
        compressed.getParentFile().mkdirs();
        new File(sourceBasepath).mkdirs();
        for(GeneratedFile gf:files)
        {
            File f = new File(sourceBasepath+"/"+gf.getPath()+"/"+gf.getFilename());
            f.getParentFile().mkdirs();
            f.createNewFile();

            FileUtils.writeStringToFile(f,gf.getContent());
        }
        //create Archive
        ZipUtils zu = new ZipUtils();
        zu.zip(sourceBasepath,compressed);
        //return Archive
        return compressed;
    }

    protected abstract void generate(T graphModel);

    protected final void createFile(String filename,String content) {
        createFile(filename,"",content);
    }

    protected final void createFile(String filename,File file) {
        createFile(filename,"",file);
    }

    protected final void createFile(String filename,String path,String content) {
        if(filename==null||path==null||content==null) {
            throw new IllegalStateException("All parameters has to be not null");
        }
        if(filename.isEmpty()) {
            throw new IllegalStateException("Filename has to be given");
        }
        if(content.isEmpty()) {
            content = " ";
        }
        files.add(new GeneratedFile(filename,path,content));
    }

    protected final void createFile(String filename,String path,File file) {
        if(filename==null||path==null||file==null) {
            throw new IllegalStateException("All parameters has to be not null");
        }
        if(filename.isEmpty()) {
            throw new IllegalStateException("Filename has to be given");
        }

        files.add(new GeneratedFile(filename,path,file));
    }
    
    private Object createFolder(String newFolderPath,Object folder) {
	    if(newFolderPath==null||newFolderPath.isEmpty()) {
	        return folder;
	    }
	    String[] folders = newFolderPath.split("/");
	    Object current = folder;
	    for (String folder1 : folders) {
	        if(folder1.isEmpty()) {
	            continue;
	        }
	        if(current instanceof PyroFolderDB) {
	
	            Optional<PyroFolderDB> pf = ((PyroFolderDB)current).innerFolders.stream().filter(n -> n.name.equals(folder1)).findAny();
	            if (!pf.isPresent()) {
	                PyroFolderDB newFolder = new PyroFolderDB();
	                newFolder.name = folder1;
	                newFolder.parent = (PyroFolderDB) current;
	                newFolder.persist();
	                ((PyroFolderDB)current).innerFolders.add(newFolder);
	                ((PyroFolderDB)current).persist();
	                current = newFolder;
	
	            } else {
	                current = pf.get();
	            }
	        }
	        if(current instanceof PyroProjectDB) {
	            Optional<PyroFolderDB> pf = ((PyroProjectDB)current).innerFolders.stream().filter(n -> n.name.equals(folder1)).findAny();
	            if (!pf.isPresent()) {
	                PyroFolderDB newFolder = new PyroFolderDB();
	                newFolder.name = folder1;
	                newFolder.parent = (PyroProjectDB) current;
	                newFolder.persist();
	                ((PyroProjectDB)current).innerFolders.add(newFolder);
	                ((PyroProjectDB)current).persist();
	                current = newFolder;
	
	            } else {
	                current = pf.get();
	            }
	        }
	    }
	    return current;
	}
    
    private PyroFolderDB getFolder(String path,PyroFolderDB folder) {
        String[] folders = path.split("/");
        PyroFolderDB current = folder;
        for (String folder1 : folders) {
            Optional<PyroFolderDB> pf = current.innerFolders.stream().filter(n -> n.name.equals(folder1)).findFirst();
            if (pf.isPresent()) {
                current = pf.get();
            } else {
                return null;
            }
        }
        return current;
    }

    private Object getFolder(String path,PyroProjectDB folder) {
        String[] folders = path.split("/");
        Object current = folder;

        for (String folder1 : folders) {
            if(current instanceof PyroFolderDB) {
                Optional<PyroFolderDB> pf = ((PyroFolderDB)current).innerFolders.stream().filter(n -> n.name.equals(folder1)).findFirst();
                if (pf.isPresent()) {
                    current = pf.get();
                } else {
                    return null;
                }
            } else if(current instanceof PyroProjectDB) {
                Optional<PyroFolderDB> pf = ((PyroProjectDB)current).innerFolders.stream().filter(n -> n.name.equals(folder1)).findFirst();
                if (pf.isPresent()) {
                    current = pf.get();
                } else {
                    return null;
                }
            }
        }
        return current;
    }
    
}
