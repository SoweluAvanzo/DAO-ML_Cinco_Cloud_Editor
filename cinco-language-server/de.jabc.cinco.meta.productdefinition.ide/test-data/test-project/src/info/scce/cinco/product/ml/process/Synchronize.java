package info.scce.cinco.product.ml.process;

import entity.core.PyroProjectService_Connect_Jupyter_AccountDB;
import entity.core.PyroUserDB;
import entity.jupyter.JupyterDB;
import jupyter.Function;
import jupyter.FunctionGroup;
import jupyter.Jupyter;
import jupyter.Parameter;
import jupyter.impl.FunctionGroupImpl;
import jupyter.impl.FunctionImpl;
import jupyter.impl.JupyterImpl;
import jupyter.impl.ParameterImpl;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;

import javax.json.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Synchronize extends CincoCustomAction<graphmodel.GraphModel> {

    private JupyterUtil util = new JupyterUtil();

    public boolean canExecute(PyroUserDB user, graphmodel.GraphModel model) {
    	return model instanceof info.scce.cinco.product.ml.process.mlprocess.MLProcess 
        	&& util.getService() != null;
    }

    @Override
    public void execute(graphmodel.GraphModel model) {
    	// PyroUserDB user = getCurrentUser();
        PyroProjectService_Connect_Jupyter_AccountDB service = util.getService();

        if(service == null) {
            return;
        }
        
        String token = service.getToken();
        String username = service.getUsername();
        String url = service.getURL();
        
        /*
         * USER
         */
        JsonObject userObj = util.checkUser(username,token,url);

        /*
        Check Server status
         */
        if(userObj.isNull("server")) {
            util.startServer(username,token,url);
        } else {
            System.out.println("Server is running");
        }

        List<String> elements = null;
        try {
            System.out.println("Start Function reading");
            elements = getFunctions(username,"",token,url);
            elements.forEach(System.out::println);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(elements == null) {
            return;
        }

        List<PyFile> pyFiles = null;
        try {
            System.out.println("Fetch source code");
            pyFiles = extractFunctions(username, elements,token,url);
            pyFiles.forEach(System.out::println);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if(pyFiles == null) {
            return;
        }

        /*
         * synchronize ecore
         */

        System.out.println("Start Ecore sync");
        
        // TODO: SAMI - this behaviour shall be discussed
        JupyterDB singleton = JupyterDB.findAll().firstResult();
        Jupyter lib = singleton != null ?
        		new JupyterImpl(singleton)
        		: new JupyterImpl();
        lib.setFilename("Jupyter");
        
        // reset
        List<FunctionGroup> fgtoDelete = new LinkedList<>();
        for(FunctionGroup fg:lib.getFunctionGroup()) {
            if(	pyFiles.stream().noneMatch(n->
            		n.filename.equals(fg.getName())
            	)
            ) {
            	fgtoDelete.add(fg);
                List<Function> funs = new LinkedList<>(fg.getFunctions());
                funs.forEach(n->{
                    n.clearInputs(true);
                    if(n.getOutput() != null) {
                        n.getOutput().delete();
                    }
                    n.setOutput(null);
                    n.delete();
                });
            }
        }
        for(FunctionGroup n : fgtoDelete)
        {
            lib.getFunctionGroup().remove(n);
            n.delete();
        }

        // rebuild
        for(PyFile file:pyFiles) {
        	
        	/**
             * FUNCTION-GROUP
             */
            Optional<FunctionGroup> functionGroup = lib.getFunctionGroup().stream()
        		.filter(n -> 
        			n.getName().equals(file.filename)
        		).findFirst();
            FunctionGroup fg = null;
            if(functionGroup.isPresent()) {
                // already present (update)
            	fg = functionGroup.get();
            } else {
            	// new
                fg = new FunctionGroupImpl();
                fg.setName(file.filename);
                lib.addFunctionGroup(fg);
            }

            /**
             * FUNCTIONS
             */

            List<PyFunction> funs = file.functions;
            // remove no more present Functions
            List<Function> funToDelete = fg.getFunctions().stream()
        		.filter(n ->
        			funs.stream().noneMatch(f ->
        				f.functionName.equals(n.getName())
        			)
        		).collect(Collectors.toList());
            funToDelete.forEach(n -> {
                n.clearInputs(true);
                if(n.getOutput() != null) {
                	n.getOutput().delete();
                }
                n.setOutput(null);
                n.delete();
            });
            
            // update
            for(PyFunction f:funs) {
                Optional<Function> optional = fg.getFunctions().stream()
            		.filter(n ->
            			n.getName().equals(f.functionName)
            		).findFirst();
                final Function newFun  = optional.isPresent() ? optional.get() : new FunctionImpl();
                if(!optional.isPresent()) {
                	fg.addFunctions(newFun);
                }
                newFun.setName(f.functionName);
                newFun.setDocumentation(f.documentation);
                newFun.setImport(f.importStatement);

                /**
                 * INPUTS
                 */
                
                // remove old inputs
                List<Parameter> toDelete =  newFun
                    .getInputs()
                    .stream()
                    .filter(n -> f.inputs.stream()
                        .noneMatch(i ->
                        	i.name.equals(n.getName()) && i.typeName.equals(n.getTypeName())
                        )
                    ).collect(Collectors.toList());
                toDelete.forEach(n -> newFun.removeInputs(n, true));
                
                // update
                for(PyParameter parameter : f.inputs) {
                    Optional<Parameter> optionalParam = newFun.getInputs().stream()
            			.filter(n ->
            				n.getName().equals(parameter.name) && n.getTypeName().equals(parameter.typeName)
            			).findFirst();
                    if(!optionalParam.isPresent()) {
                        //not present add new
                        Parameter p = new ParameterImpl();
                        p.setName(parameter.name);
                        p.setTypeName(parameter.typeName);
                        newFun.addInputs(p);
                    }
                    //else no sync needed
                }
                
                /**
                 * OUTPUTS
                 */
                
                //sync outputs
                if(newFun.getOutput() != null && f.output != null) {
                    if(
                		!(
            				newFun.getOutput().getTypeName().equals(f.output.typeName)
            				&& newFun.getOutput().getName().equals(f.output.name)
                		)
                    ) {
                        //changed output delete and renew
                        Parameter p = new ParameterImpl();
                        p.setName(f.output.name);
                        p.setTypeName(f.output.typeName);
                        newFun.getOutput().delete();
                        newFun.setOutput(p);
                    }
                    // else no sync needed
                }
                else if(newFun.getOutput() != null && f.output == null) {
                    newFun.getOutput().delete();
                    newFun.setOutput(null);
                }
                else if(newFun.getOutput() == null && f.output != null) {
                	Parameter p = new ParameterImpl();
                	p.setName(f.output.name);
                    p.setTypeName(f.output.typeName);
                    newFun.setOutput(p);
                }
            }
        }
        // synchronize to ecore-view
        this.commandExecuter().sync(lib);
    }

    private List<String> getFunctions(String username,String path,String token,String url) throws ExecutionException, InterruptedException {

        List<String> innerFolders = new LinkedList<>();
        // read all files
        JsonObject resp = util.get(url+JupyterUtil.NOTEBOOK_URL+"/"+username+"/api/contents"+path,token).get();

        if(resp.containsKey("content") && resp.getJsonArray("content") != null) {
            for(JsonObject inner:resp.getJsonArray("content").stream().filter(n->n instanceof JsonObject).map(n->(JsonObject)n).collect(Collectors.toList())) {
                if(
                        inner.containsKey("type")
                    && !inner.isNull("type")
                    && inner.containsKey("mimetype")
                    && !inner.isNull("mimetype")
                    && inner.getString("type").equals("file")
                    && inner.getString("mimetype").equals("text/x-python")
                ) {
                    //.py file
                    innerFolders.add(path+"/"+inner.getString("name"));
                }
                if(
                        inner.containsKey("type")
                    && !inner.isNull("type")
                    && inner.getString("type").equals("notebook")
                ) {
                    //jupyter notebook
                    innerFolders.add(path+"/"+inner.getString("name"));
                }
                if(
                        inner.containsKey("type")
                    && !inner.isNull("type")
                    && inner.getString("type").equals("directory")
                ) {
                    innerFolders.addAll(getFunctions(username, path+"/"+inner.getString("name"),token,url));
                }
            }
        }
        return innerFolders;
    }

    private String noExtension(String s) {
        return s.substring(0,s.lastIndexOf("."));
    }

    private List<PyFile> extractFunctions(String username, List<String> files,String token,String url) throws ExecutionException, InterruptedException {
        List<PyFile> pyFiles = new LinkedList<>();
        for(String f:files) {
            JsonObject resp = util.get(url+JupyterUtil.NOTEBOOK_URL+"/"+username+"/api/contents"+f,token).get();
            if(f.endsWith(".py")) {
                processSourceCode(f,resp.getString("content"),pyFiles,false);
            }
            if(f.endsWith(".ipynb")) {
                for(JsonObject c:resp.getJsonObject("content").getJsonArray("cells").stream().filter(n->n instanceof JsonObject).map(n->(JsonObject)n).filter(n->n.containsKey("cell_type") && n.getString("cell_type").equals("code")).collect(Collectors.toList())) {
                    processSourceCode(f,c.getString("source"),pyFiles,true);
                }
            }
        }
        return pyFiles;
    }

    private void processSourceCode(String path,String code,List<PyFile> pyFiles,boolean isNotebook) {
        String lines[] = code.split("\\r?\\n");
        String fileName = noExtension(path.substring(path.lastIndexOf("/")+1));
        List<PyFunction> pyFunctions = new LinkedList<>();
        for(int i = 0;i<lines.length;i++) {
            if(lines[i].startsWith("#")) {
                String comment = lines[i].replaceAll(" ", "");
                if(comment.startsWith("#Method:")) {
                    PyFunction pf = new PyFunction();
                    pf.documentation = lines[i];
                    pf.label = comment.substring(comment.lastIndexOf("#Method:") + 8);
                    if(isNotebook) {
                        pf.functionName = pf.label;
                        pf.importStatement = "from ipynb.fs.full."+noExtension(path.replaceAll("/",""))+" import "+pf.functionName;
                    } else {
                        pf.importStatement = "import "+noExtension(path.substring(1).replaceAll("/","\\."))+" as "+fileName;
                        pf.functionName = fileName+"."+pf.label;
                    }

                    if(lines.length >= i+1 && lines[i+1].startsWith("# Inputs:")) {
                        pf.documentation += "\n"+lines[i+1];
                        String inputs = lines[i+1].replaceAll(" ", "");
                        String list = inputs.substring(inputs.lastIndexOf("#Inputs:") + 8);
                        if(list.contains(",")) {
                            for(String p:list.split(",")) {
                                if(p.contains(":")) {
                                    pf.inputs.add(new PyParameter(p.split(":")[0],p.split(":")[1]));
                                }
                            }
                        } else {
                            if(list.contains(":")) {
                                pf.inputs.add(new PyParameter(list.split(":")[0],list.split(":")[1]));
                            }
                        }

                        if(lines.length >= i+2 &&  lines[i+2].startsWith("# Output:")) {
                            pf.documentation += "\n"+lines[i+2];
                            String output = lines[i+2].replaceAll(" ", "");
                            String outputParam = output.substring(output.lastIndexOf("#Output:") + 8);
                            if(outputParam.contains(":")) {
                                pf.output = new PyParameter(outputParam.split(":")[0],outputParam.split(":")[1]);
                            }
                        }
                        pyFunctions.add(pf);
                    }
                }
            }
        }
        if(!pyFunctions.isEmpty()) {
            PyFile f = new PyFile();
            f.filename = fileName;
            f.functions.addAll(pyFunctions);
            pyFiles.add(f);
        }
    }
}

class PyFile {
    String filename;
    List<PyFunction> functions = new LinkedList<>();
    @Override()
    public String toString() {
        return filename+"->"+functions.stream().map(PyFunction::toString).collect(Collectors.joining(","));
    }
}

class PyFunction {
    String label;
    String importStatement;
    String functionName;
    List<PyParameter> inputs = new LinkedList<>();
    PyParameter output;
    String documentation;

    @Override()
    public String toString() {
        return functionName+"("+inputs.stream().map((n)->n.name+":"+n.typeName).collect(Collectors.joining(","))+")->"+(output==null?"void":(output.name+":"+output.typeName));
    }
}

class PyParameter {
    String name;
    String typeName;
    PyParameter(String name,String typeName) {
        this.name = name;
        this.typeName = typeName;
    }
}