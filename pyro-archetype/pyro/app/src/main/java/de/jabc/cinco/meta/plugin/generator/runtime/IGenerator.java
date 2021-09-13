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

		//copy and overwrite with static resources
		for(java.util.Map.Entry<String,String[]> staticResource:staticResources.entrySet())
		{
			//TODO transfer files to theia
		}
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
    
    
}
