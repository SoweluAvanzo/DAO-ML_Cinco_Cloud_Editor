package de.jabc.cinco.meta.plugin.pyro.backend.generator

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class IGeneratorGenerator extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def filename() '''IGenerator.java'''

	def content() '''
		package de.jabc.cinco.meta.plugin.generator.runtime;
		
		import entity.core.*;
		import graphmodel.*;
		
		import java.io.*;
		import java.nio.charset.StandardCharsets;
		import java.util.LinkedList;
		import java.util.List;
		import java.util.Optional;
		
		import org.apache.commons.io.FileUtils;
		
		
		/**
		 * Author zweihoff
		 */
		public abstract class IGenerator<T extends GraphModel> {
		
			private List<GeneratedFile> files;
			
			private String basePath;
			
			private info.scce.pyro.core.FileController fileController;
			
			
			public IGenerator() {
				files = new LinkedList<>();
			}
			
			public final void generateFiles(T graphModel, String basePath,String staticResourceBase,java.util.Map<String,String[]> staticResources,info.scce.pyro.core.FileController fileController) throws IOException {
				this.basePath = basePath;
				this.fileController = fileController;
				
				generate(graphModel);
				//get generation base folder
				String generationBaseFolder = basePath;
				
				String workspacePath = SecurityOverrideFilter.getworkspacePath();
				String generationBaseFolder = workspacePath + basePath;
				File dir = new File(generationBaseFolder);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// Path path = Paths.get(generationBaseFolder);
				
				for (GeneratedFile f : files) {
							String genDirName = generationBaseFolder + f.getPath();
							File genDir = new File(genDirName);
							if (!genDir.exists()) {
								genDir.mkdir();
							}
							String fileName = genDirName  + f.getFilename();
							File file = new File(fileName);
							if (file.exists() && !file.isDirectory()) {
								file.delete();
							}
							Path path = Paths.get(fileName).normalize();
							java.nio.file.Files.writeString(path, f.getContent());
						}
				
						Path staticResourcePath = Paths.get(generationBaseFolder, staticResourceBase).normalize();
						File staticResourcesDest = new File(staticResourcePath.toString());
						if(!staticResourcesDest.exists() || !staticResourcesDest.isDirectory()) {
							staticResourcesDest.mkdirs();
						}
						// copy and overwrite with static resources
						for (java.util.Map.Entry<String, String[]> staticResource : staticResources.entrySet()) {
				
							String[] fileEntries = staticResource.getValue();
							for (String fileEntry : fileEntries) {
								Path p = Paths.get(fileEntry).normalize();
								File f = new File(p.toString());
							//	if (f.exists() && !f.isDirectory()) {
							//		f.delete();
							//	}
								try {
									Path copyDest = Paths.get(staticResourcesDest.toString(), suffix(f.toString(), staticResource.getKey())).normalize();
									File fileTocopy = new File (copyDest.toString());
									if(!fileTocopy.getParentFile().exists()) {
										fileTocopy.getParentFile().mkdirs();
									}
									java.nio.file.Files.copy(f.toPath(), fileTocopy.toPath().normalize(), StandardCopyOption.REPLACE_EXISTING);
								} catch (IOException e) {
									e.printStackTrace();
								}
				
							}
				
						}
					}
		«««	    public final File generateFilesTemporal(T graphModel) throws IOException {
«««	        generate(graphModel);
«««	        //create Files in temp directory
«««	        String sourceBasepath = System.getProperty("java.io.tmpdir")+"/"+graphModel.getId()+"/sources/";
«««	        String compressedBasepath = System.getProperty("java.io.tmpdir")+"/"+graphModel.getId()+"/compressed/generated.zip";
«««	        File compressed = new File(compressedBasepath);
«««	        compressed.getParentFile().mkdirs();
«««	        new File(sourceBasepath).mkdirs();
«««	        for(GeneratedFile gf:files)
«««	        {
«««	            File f = new File(sourceBasepath+"/"+gf.getPath()+"/"+gf.getFilename());
«««	            f.getParentFile().mkdirs();
«««	            f.createNewFile();
«««	
«««	            FileUtils.writeStringToFile(f,gf.getContent());
«««	        }
«««	        //create Archive
«««	        ZipUtils zu = new ZipUtils();
«««	        zu.zip(sourceBasepath,compressed);
«««	        //return Archive
«««	        return compressed;
«««	   }
	
	    protected abstract void generate(T graphModel);
	    
    	protected String suffix(String absolutPath, String resourceFolder) {
    		return absolutPath.substring(absolutPath.lastIndexOf(resourceFolder)+resourceFolder.length()+1);
    	}
	
	    protected final void createFile(String filename,String contel); {
	        createFile(filename,"",conel););
	    }
	
	    protected final void createFile(String filename,File f   } {
	        createFile(filename,"",   });
	    }
	
	    protected final void createFile(String filename,String path,String cont   } {
	        if(filename==null||path==null||content==n   } {
	            throw new IllegalStateException("All parameters has to be not n   } {
	      } {
	        if(filename.isEmpt   } {
	            throw new IllegalStateException("Filename has to be gi   } {
	      } {
	        if(content.isEmpt   } {
	            content    } {
	      } {
	        files.add(new GeneratedFile(filename,path,cont   });
	    }
	
	    protected final void createFile(String filename,String path,File f   } {
	        if(filename==null||path==null||file==n   } {
	            throw new IllegalStateException("All parameters has to be not n   } {
	      } {
	        if(filename.isEmpt   } {
	            throw new IllegalStateException("Filename has to be gi   } {
	        }
	
	        files.add(new GeneratedFile(filename,path,file));
		 }ile)  
	    
	}
	'''

}
