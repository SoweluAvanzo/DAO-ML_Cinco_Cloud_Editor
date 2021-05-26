package de.jabc.cinco.meta.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
/*
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
*/
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.workspace.IProjectConfigProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
/*
import de.jabc.cinco.meta.runtime.xapi.ResourceExtension;
import de.jabc.cinco.meta.util.xapi.FileExtension;
import de.jabc.cinco.meta.util.xapi.WorkspaceExtension;
*/
import mgl.Annotatable;
import mgl.Annotation;
import mgl.Attribute;
import mgl.Edge;
import mgl.GraphModel;
import mgl.GraphicalModelElement;
import mgl.Import;
import mgl.MGLModel;
import mgl.ModelElement;
import mgl.Node;
import mgl.UserDefinedType;
import productDefinition.CincoProduct;
import style.EdgeStyle;
import style.NodeStyle;
import style.Style;
import style.Styles;

public class CincoUtil {
	
	public static final String ID_STYLE = "style";
	public static final String ID_ICON = "icon";
	public static final String ID_LABEL = "label";
	public static final String ID_DISABLE= "disable";
	public static final String ID_DISABLE_CREATE = "create";
	public static final String ID_DISABLE_DELETE = "delete";
	public static final String ID_DISABLE_MOVE = "move";
	public static final String ID_DISABLE_RESIZE = "resize";
	public static final String ID_DISABLE_RECONNECT = "reconnect";
	public static final String ID_DISABLE_SELECT = "select";
	public static final String ID_ATTRIBUTE_HIDDEN = "propertiesViewHidden";
	public static final String ID_DISABLE_HIGHLIGHT = "disableHighlight";
	public static final String ID_DISABLE_HIGHLIGHT_CONTAINMENT = "containment";
	public static final String ID_DISABLE_HIGHLIGHT_RECONNECTION = "reconnection";
	public static Set<String> DISABLE_NODE_VALUES = new HashSet<String>(Arrays.asList(ID_DISABLE_CREATE, ID_DISABLE_DELETE, ID_DISABLE_MOVE, ID_DISABLE_RESIZE, ID_DISABLE_SELECT));
	public static Set<String> DISABLE_EDGE_VALUES = new HashSet<String>(Arrays.asList(ID_DISABLE_CREATE, ID_DISABLE_DELETE, ID_DISABLE_RECONNECT, ID_DISABLE_SELECT));
	public static Set<String> DISABLE_HIGHLIGHT_VALUES = new HashSet<String>(Arrays.asList(ID_DISABLE_HIGHLIGHT_CONTAINMENT, ID_DISABLE_HIGHLIGHT_RECONNECTION));
	
	/**
	 * Returns if the provided <code>modelElement</code> has move disabled.
	 * A {@link ModelElement} has delete disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the value "move".
	 * <br />
	 * Please note that only {@link Node Nodes} can have move disabled.
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for disabled move
	 * @return	returns true if the provided <code>modelElement</code> has move disabled or is generally disabled
	 */
	public static boolean isCreateDisabled(ModelElement me) {
		return isDisabled(me, ID_DISABLE_CREATE);
	}
	
	/**
	 * Returns if the provided <code>modelElement</code> has move disabled.
	 * A {@link ModelElement} has delete disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the value "move".
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for disabled move
	 * @return	returns true if the provided <code>modelElement</code> has move disabled or is generally disabled
	 */
	public static boolean isMoveDisabled(ModelElement modelElement) {
		return isDisabled(modelElement, ID_DISABLE_MOVE);
	}
	
	/**
	 * Returns if the provided <code>modelElement</code> has select disabled.
	 * A {@link ModelElement} has delete disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the value "select".
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for disabled select
	 * @return	returns true if the provided <code>modelElement</code> has select disabled or is generally disabled
	 */
	public static boolean isResizeDisabled(ModelElement me) {
		return isDisabled(me, ID_DISABLE_RESIZE);
	}
	
	/**
	 * Returns if the provided <code>modelElement</code> has select disabled.
	 * A {@link ModelElement} has delete disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the value "select".
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for disabled select
	 * @return	returns true if the provided <code>modelElement</code> has select disabled or is generally disabled
	 */
	public static boolean isSelectDisabled(ModelElement modelElement) {
		return isDisabled(modelElement, ID_DISABLE_SELECT);
	}
	
	/**
	 * Returns if the provided <code>modelElement</code> has reconnect disabled.
	 * A {@link ModelElement} has delete disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the value "reconnect".
	 * <br />
	 * Please note that only {@link Edge Edges} can have reconnect disabled.
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for disabled reconnect
	 * @return	returns true if the provided <code>modelElement</code> has reconnect disabled or is generally disabled
	 */
	public static boolean isReconnectDisabled(ModelElement modelElement) {
		return isDisabled(modelElement, ID_DISABLE_RECONNECT);
	}
	
	/**
	 * Returns if the provided <code>modelElement</code> has delete disabled.
	 * A {@link ModelElement} has delete disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the value "delete".
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for disabled deletion
	 * @return	returns true if the provided <code>modelElement</code> has delete disabled or is generally disabled
	 */
	public static boolean isDeleteDisabled(ModelElement modelElement) {
		return isDisabled(modelElement, ID_DISABLE_DELETE);
	}
	
	/**
	 * Returns if the provided <code>modelElement</code> is disabled.
	 * A {@link ModelElement} is considered disabled if it possesses an empty disabled annotation
	 * or a disabled annotation with the values "create", "delete", "move", "resize", "select" if the <code>modelElement</code>
	 * is a {@link Node} or "create", "delete", "reconnect", "select" if the <code>modelElement</code> is an {@link Edge}.
	 * 
	 * @param modelElement	the {@link ModelElement} which should be inspected for a disable annotation
	 * @return	returns true if the provided <code>modelElement</code> is considered disabled under the above described aspects
	 */
	public static boolean isDisabled(ModelElement modelElement) {
		Set<String> values = DISABLE_NODE_VALUES;
		if (modelElement instanceof mgl.Edge)
			values = DISABLE_EDGE_VALUES;
		for (Annotation annot : modelElement.getAnnotations())
			if (annot.getName().equals(ID_DISABLE))
				return (annot.getValue().isEmpty() || annot.getValue().containsAll(values));
		return false;
	}
	
	private static boolean isDisabled(ModelElement me, String id) {
		for (Annotation annot : me.getAnnotations()) {
			if (annot.getName().equals(ID_DISABLE)) {
				return (annot.getValue().isEmpty() || annot.getValue().contains(id));
			}
		}
		return false;
	}
	
	/**
	 * Returns if the provided <code>annotatable</code> has containment highlighting disabled.
	 * A {@link Annotatable} has containment highlighting disabled if it possesses an empty disableHighlight annotation
	 * or a disabledHightlight annotation with the value "containment".
	 * 
	 * @param annotatable	the {@link Annotatable} which should be inspected for disabled containment highlighting
	 * @return	returns true if the provided <code>modelElement</code> has containment highlighting disabled or has highlighting generally disabled
	 */
	public static boolean isHighlightContainmentDisabled(Annotatable annotatable) {
		return isHighlightDisabled(annotatable, ID_DISABLE_HIGHLIGHT_CONTAINMENT);
	}
	
	/**
	 * Returns if the provided <code>annotatable</code> has reconnection highlighting disabled.
	 * A {@link Annotatable} has reconnection highlighting disabled if it possesses an empty disableHighlight annotation
	 * or a disabledHightlight annotation with the value "reconnection".
	 * 
	 * @param annotatable	the {@link Annotatable} which should be inspected for disabled reconnection highlighting
	 * @return	returns true if the provided <code>modelElement</code> has reconnection highlighting disabled or has highlighting generally disabled
	 */
	public static boolean isHighlightReconnectionDisabled(Annotatable annotatable) {
		return isHighlightDisabled(annotatable, ID_DISABLE_HIGHLIGHT_RECONNECTION);
	}
	
	/**
	 * Returns if the provided <code>annotatable</code> has reconnection highlighting disabled.
	 * A {@link Annotatable} has reconnection highlighting disabled if it possesses an empty disableHighlight annotation
	 * or a disabledHightlight annotation with the value "reconnection".
	 * 
	 * @param annotatable	the {@link Annotatable} which should be inspected for disabled reconnection highlighting
	 * @return	returns true if the provided <code>modelElement</code> has reconnection highlighting disabled or has highlighting generally disabled
	 */
	public static boolean isHighlightDisabled(Annotatable me) {
		for (Annotation annot : me.getAnnotations()) {
			if (annot.getName().equals(ID_DISABLE_HIGHLIGHT)) {
				return annot.getValue().isEmpty() || annot.getValue().containsAll(DISABLE_HIGHLIGHT_VALUES);
			}
		}
		return false;
	}
	
	private static boolean isHighlightDisabled(Annotatable me, String id) {
		for (Annotation annot : me.getAnnotations()) {
			if (annot.getName().equals(ID_DISABLE_HIGHLIGHT)) {
				return annot.getValue().isEmpty() || annot.getValue().contains(id);
			}
		}
		return false;
	}
	
	/**
	 * Returns whether the provided <code>attr</code> possesses a readOnly annotation.
	 * The readOnly annotation signals that the annotated <code>attr</code> should not be editible.
	 * 
	 * @param attr	the {@link Attribute} which should be checked for a readOnly annotation
	 * @return	returns true if the provided <code>attr</code> possesses a readOnly annotation
	 */
	public static boolean isAttributeReadOnly(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("readOnly"))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns whether the provided <code>attr</code> possesses a file annotation.
	 * <br/>
	 * The file annotation signals that the annotated <code>attr</code> should contain the path to a file.
	 * A file picker will be provided in the resulting CINCO product.
	 * 
	 * @param attr	the {@link Attribute} which should be checked for a file annotation
	 * @return	returns true if the provided <code>attr</code> possesses a file annotation
	 */
	public static boolean isAttributeFile(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("file"))
				return true;
		}
		return false;
	}
	 /**
	  * Returns whether the provided <code>attr</code> possesses a possibleValuesProvider annotation.
	  * <br/>
	  * This annotation is used to show all possible values in a combobox.
	  * As the annotation's value a fully qualified name to a class which implements the combobox functionality is expected.
	  * This class has to extend {@link CincoValuesProvider}.
	  * 
	  * @param attr	the {@link Attribute} which should be checked for a possibleValuesProvider annotation
	  * @return	true if the provided <code>attr</code> possesses a possibleValuesProvider annotation
	  */
	public static boolean isAttributePossibleValuesProvider(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("possibleValuesProvider"))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the value of the provided <code>attr</code>'s possibleValuesProvider annotation.
	 * The returned value should be a fully qualified name to a class that extends {@link CincoValuesProvider}.
	 * <br/>
	 * This method does <strong>not</strong> check whether the returned fully qualified name is valid.
	 * <br/>
	 * If the possibleValuesProvider annotation is empty or possesses more than one value, this method returns <code>null</code>.
	 * 
	 * @param attr	the {@link Attribute} for which the possibleValuesProvider annotation's value should be returned
	 * @return	the value of the provided <code>attr</code>'s possibleValuesProvider annotation if it contains exactly one value.
	 * 			The value should be a fully qualified name of a class which extends {@link CincoValuesProvider}
	 */
	public static String getPossibleValuesProviderClass(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("possibleValuesProvider"))
				if(!annot.getValue().isEmpty() && annot.getValue().size() == 1)
					return annot.getValue().get(0);
		}
		return null;
	}
	
	/**
	 * Returns whether the provided <code>attr</code> possesses a color annotation.
	 * <br/>
	 * The file annotation signals that the annotated <code>attr</code> should contain a color value.
	 * A color picker will be provided in the resulting CINCO product.
	 * 
	 * @param attr	the {@link Attribute} which should be checked for a color annotation
	 * @return	returns true if the provided <code>attr</code> possesses a file annotation
	 */
	public static boolean isAttributeColor(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("color"))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns whether the provided <code>attr</code> possesses a propertiesViewHidden annotation.
	 * <br/>
	 * The propertiesViewHidden annotation signals that the annotated <code>attr</code> should not be visible
	 * in the CINCO products' cinco properties view.
	 * 
	 * @param attr	the {@link Attribute} which should be checked for a propertiesViewHidden annotation
	 * @return	returns true if the provided <code>attr</code> possesses a propertiesViewHidden annotation
	 */
	public static boolean isAttributeHidden(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals(ID_ATTRIBUTE_HIDDEN))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns whether the provided <code>attr</code> possesses a grammar annotation.
	 * <br/>
	 * The grammar annotation signals that the annotated <code>attr</code> should contain a model of the provided xtext grammar.
	 * In the resulting CINCO product a Xtext editor will be embeded into the cinco property view for this attribute.
	 * 
	 * @param attr	the {@link Attribute} which should be checked for a grammar annotation
	 * @return	returns true if the provided <code>attr</code> possesses a grammar annotation
	 */
	public static boolean isGrammarAttribute(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("grammar"))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns whether the provided <code>userDefinedType</code> possesses a label annotation.
	 * <br/>
	 * The label annotation signals that the annotated <code>userDefinedType</code> should be visible
	 * under the referenced attribute in the cinco properties view tree.
	 * 
	 * @param attr	the {@link UserDefinedType} which should be checked for a label annotation
	 * @return	returns true if the provided <code>userDefinedType</code> possesses a label annotation
	 */
	public static boolean hasLabel(UserDefinedType userDefinedType) {
		for (Annotation annot : userDefinedType.getAnnotations()) {
			if (annot.getName().equals(ID_LABEL))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the {@link Resource} representing the style file reachable under the provided <code>pathToStyles</code>.
	 * If no file can be found <code>null</code> is returned.
	 * 
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @param pathToStyles	a {@link String} that states the path to the desired style file
	 * @return	the {@link Resource} which represent the style file at the provided <code>pathToStyles</code>.
	 * 			Returns <code>null</code> if no such Resource is available
	 */
	public static Resource getStylesResource(IWorkspaceContext workspaceContext, String pathToStyles) {
		Resource res = null;
		URI uri = URI.createURI(pathToStyles, true);
		try {
			if (uri.isPlatformResource())
				res = new ResourceSetImpl().getResource(uri, true);
			else {
				File file = workspaceContext.getFile(pathToStyles);
				URI fileURI = URI.createPlatformResourceURI(file.getAbsolutePath(), true);
				res = new ResourceSetImpl().getResource(fileURI, true);
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the {@link Styles} object which is used in the {@link MGLModel} that the provided <code>graphModel</code> has been defined in.
	 * If no {@link Styles} object can be found <code>null</code> is returned instead.
	 * 
	 * @param model the {@link MGLModel} or the {@link GraphModel} the {@link Styles} object should be returned
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	a {@link Styles} object that is used in <code>graphModel</code>'s {@link MGLModel} to define appearances
	 * @see	#getStyles(MGLModel)
	 */
	public static Styles getStyles(EObject model, IWorkspaceContext workspaceContext) {
		if(model instanceof MGLModel) {
			return getStyles((MGLModel) model, workspaceContext);
		} else if(model instanceof GraphModel) {
			return getStyles((MGLModel) model.eContainer(), workspaceContext);
		}
		throw new RuntimeException("model is neither an MGLModel nor a GraphModel.");
	}
	
	/**
	 * Returns the {@link Styles} object which is used in the provided <code>mgl</code>.
	 * If no {@link Styles} object can be found <code>null</code> is returned instead.
	 * 
	 * @param mgl the {@link MGLModel} for which the used {@link Styles} object should be returned
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	a {@link Styles} object that is used in the provided <code>mgl</code>. <code>null</code> if no {@link Styles} is in use
	 */
	public static Styles getStyles(MGLModel mgl, IWorkspaceContext workspaceContext) {
		String path = mgl.getStylePath();
		if (path.length() > 0) {
			URI uri = workspaceContext.getFileURI(path);
			try {
				Resource res = null;
				if (uri.isPlatformResource()) {
					res = new ResourceSetImpl().getResource(uri, true);
				}
				else {
					File file = workspaceContext.getFile(mgl.eResource().getURI());
					if (file.exists()) {
						URI fileURI = URI.createPlatformResourceURI(file.getAbsolutePath(), true);
						res = new ResourceSetImpl().getResource(fileURI, true);
					}
					else {
						return null;
					}
				}
				
				for (Object o : res.getContents()) {
					if (o instanceof Styles)
						return (Styles) o;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Returns whether the provided <code>modelElement</code> uses a style which contains a reference to an apperance provider.
	 * If no style is in use at the provided <code>modelElement</code> <code>false</code> is returned.
	 * 
	 * @param modelElement	the {@link ModelElement} for which its used {@link Style} should be checked for usages of appearance providers 
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	returns <code>true</code> if the <code>modelElement</code>'s {@link Style} in use contains an appearance provider.
	 * 			Returns <code>false</code> if not or if the <code>modelElement</code> has no style in use
	 * @see #getAppearanceProvider(ModelElement)
	 * @see #getStyleForModelElement(ModelElement, Styles)
	 */
	public static boolean hasAppearanceProvider(ModelElement modelElement, IWorkspaceContext workspaceContext) {
		Style style = getStyleForModelElement(modelElement, getStyles(MGLUtil.mglModel(modelElement), workspaceContext));
		return style != null ? style.getAppearanceProvider() != null && !style.getAppearanceProvider().isEmpty() : false;
	}
	
	/**
	 * Returns the fully qualified name to the appearance provider of <code>modelElement</code>'s used style.
	 * If no style is in use at the provided <code>modelElement</code> or no appearance provider is used an empty string is returned.
	 * 
	 * @param modelElement	the {@link ModelElement} for which its used {@link Style}'s appearance provider fully qualified name should be returned
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	a {@link String} that represents the fully qualified name of the <code>modelElement</code>'s style appearance provider if available.
	 * 			If not an empty {@link String} is returned
	 * @see #hasAppearanceProvider(ModelElement)
	 * @see #getStyleForModelElement(ModelElement, Styles)
	 */
	public static String getAppearanceProvider(ModelElement modelElement, IWorkspaceContext workspaceContext) {
		Style style = getStyleForModelElement(modelElement, getStyles(MGLUtil.mglModel(modelElement), workspaceContext));
		return style != null ? style.getAppearanceProvider().replaceAll("\\\"", "") : "";
	}
	
	/**
	 * Returns the {@link Style} the provided <code>modelElement</code> uses, that is defined in the provided <code>styles</code>.
	 * If no matching {@link Style} can be found, <code>null</code> is returned instead
	 * 
	 * @param modelElement	the {@link ModelElement} for which the used {@link Style} should be returned
	 * @param styles		the {@link Styles} which should be searched for the {@link Style} applied to the provided <code>modelElement</code>
	 * @return the {@link Style} that is contained in the provided <code>styles</code> and is used at the provided <code>modelElement</code>
	 */
	public static Style getStyleForModelElement(ModelElement modelElement , Styles styles) {
		if (modelElement instanceof Node)
			return getStyleForNode((mgl.Node) modelElement, styles);
		if (modelElement instanceof Edge)
			return getStyleForEdge((mgl.Edge) modelElement, styles);
		return null;
	}
	
	/**
	 * Returns the {@link NodeStyle} the provided <code>node</code> uses, that is defined in the provided <code>styles</code>.
	 * If no matching {@link NodeStyle} can be found, <code>null</code> is returned instead
	 * 
	 * @param node		the {@link Node} for which the used {@link NodeStyle} should be returned
	 * @param styles	the {@link Styles} which should be searched for the {@link NodeStyle} applied to the provided <code>node</code>
	 * @return the {@link NodeStyle} that is contained in the provided <code>styles</code> and is used at the provided <code>node</code>
	 * @see #findStyle(Styles, String)
	 * @see #getStyleForEdge(Edge, Styles)
	 * @see #getStyleName(ModelElement)
	 */
	public static NodeStyle getStyleForNode(Node node, Styles styles) {
		String styleName = getStyleName(node);
		Style findStyle = findStyle(styles, styleName);
		if (findStyle instanceof NodeStyle)
			return (NodeStyle) findStyle;
		return null;
	}
	
	/**
	 * Returns the {@link EdgeStyle} the provided <code>edge</code> uses, that is defined in the provided <code>styles</code>.
	 * If no matching {@link EdgeStyle} can be found, <code>null</code> is returned instead
	 * 
	 * @param edge		the {@link Edge} for which the used {@link EdgeStyle} should be returned
	 * @param styles	the {@link Styles} which should be searched for the {@link EdgeStyle} applied to the provided <code>edge</code>
	 * @return the {@link EdgeStyle} that is contained in the provided <code>styles</code> and is used at the provided <code>edge</code>
	 * @see #findStyle(Styles, String)
	 * @see #getStyleForNode(Node, Styles)
	 * @see #getStyleName(ModelElement)
	 */
	public static EdgeStyle getStyleForEdge(Edge edge, Styles styles) {
		String styleName = getStyleName(edge);
		Style findStyle = findStyle(styles, styleName);
		if (findStyle instanceof EdgeStyle)
			return (EdgeStyle) findStyle;
		return null;
	}
	
	/**
	 * Searches the provided <code>styles</code> for a {@link Style} with the provided <code>name</code>.
	 * If no such {@link Style} can be found <code>null</code> is returned.
	 * 
	 * @param styles	the {@link Styles} which should be searched for a {@link Style} with the provided <code>name</code>
	 * @param name		a {@link String} which represents the name of the {@link Style} that should be searched
	 * @return	the {@link Style} defined in the provided <code>styles</code> which has the provided <code>name</code>
	 */
	public static Style findStyle(Styles styles, String name) {
		if (styles == null)
			return null;
		for (Style s : styles.getStyles()) {
			if (name.equals(s.getName()))
				return s;
		}
		return null;
	}

	/**
	 * Returns the value of the <code>usedStyle</code> attribute of the provided <code>graphicalModelElement</code>.
	 * If the value of <code>usedStyle</code> is empty <code>null</code> is returned instead.
	 * 
	 * @param graphicalModelElement	the {@link GraphicalModelElement} for which the style name should be returned
	 * @return	a {@link String} that represents the style name present at the provided <code>graphicalModelElement</code>.
	 * 			Returns <code>null</code> if no style name is present at the <code>graphicalModelElement</code>
	 */
	public static String getStyleName(GraphicalModelElement graphicalModelElement) {
		String styleName = graphicalModelElement.getUsedStyle();
		if(styleName.isEmpty()) {
			return null;
		} else {
			return styleName;
		}
	}
	
	/**
	 * Returns the {@link CincoProduct} the provided <code>file</code> contains.
	 * If the file does not represent a {@link CincoProduct} <code>null</code> is returned instead.
	 * 
	 * @param uri the {@link URI} for which the {@link CincoProduct} should be returned
	 * @return	returns the {@link CincoProduct} the provided <code>file</code> contains. If it doesn't <code>null</code> is returned instead
	 */
	public static CincoProduct getCincoProduct(URI uri) {
		Resource res = new ResourceSetImpl().getResource(uri, true);
		return getCincoProduct(res);
	}

	/**
	 * Returns the {@link CincoProduct} the provided <code>resource</code> contains.
	 * If the resource does not represent a {@link CincoProduct} <code>null</code> is returned instead.
	 * 
	 * @param resource the {@link Resource} for which the {@link CincoProduct} should be returned
	 * @return	the {@link CincoProduct} the provided <code>resource</code> contains. If it doesn't <code>null</code> is returned instead
	 */
	public static CincoProduct getCincoProduct(Resource resource) {
		for (TreeIterator<EObject> it = resource.getAllContents(); it.hasNext(); ) {
			EObject o = it.next();
			if (o instanceof CincoProduct)
				return (CincoProduct) o; 
		}
		return null;
	}
	
	/**
	 * Returns the first {@link Annotation} of the provided <code>attr</code> which matches the <code>annotName</code>.
	 * If no annotation exists or no annotations matches the <code>annotName</code> </code>null</code> is returned instead.
	 * 
	 * @param attr	the {@link Attribute} which should be checked for an {@link Annotation} with the name <code>annotName</code>
	 * @param annotName	the {@link String} which represents the name of the {@link Annotation} that should be looked for at the <code>attr</code>
	 * @return	the {@link Annotation} which matches the provided <code>annotName</code> and is present at the provided <code>attr</code>.
	 * 			Returns <code>null</code> if no annotations exists or no annotation matches the <code>annotName</code>
	 */
	public static Annotation getAnnotation(Attribute attr, String annotName) {
		List<Annotation> annots = attr.getAnnotations().stream().filter(a -> a.getName().equals(annotName)).collect(Collectors.toList());
		return (annots.isEmpty()) ? null : annots.get(0);
	}
	
	/**
	 * Returns whether the <code>attr</code> is a list attribute which can contain multiple entries.
	 * This check is done by verifying the upper bound property of <code>attr</code> is greater than one.
	 * 
	 * @param attr	the {@link Attribute} to check whether its multi valued
	 * @return	<code>true</code> if the upper bound of <code>attr</code> is greater than one,
	 * 			<code>false</code> otherwise.
	 */
	public static boolean isAttributeMultiValued(Attribute attr) {
		return attr.getUpperBound() != 1;
	}
	
	/**
	 * Returns whether the <code>attr</code> possesses the multiLine annotation.
	 * <br/>
	 * The multiLine annotation signals that the annotated <code>attr</code> should be displayed as a text area
	 * instead of a simple text field in the cinco product's property view.
	 * The annotation is only valid for string {@link Attribute Attributes}.
	 * 
	 * @param attr	the {@link Attribute} to check for the multiLine annotation
	 * @return	<code>true</code> if the <code>attr</code> possesses a multiLine annotation, <code>false</code> otherwise
	 */
	public static boolean isAttributeMultiLine(Attribute attr) {
		for (Annotation annot : attr.getAnnotations()) {
			if (annot.getName().equals("multiLine"))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns all file extensions in use at any {@link GraphModel} imported in the {@link MGLModel} the provided
	 * <code>graphModel</code> is defined in.
	 * Also includes all file extensions used in any imported ecore model.
	 * <br />
	 * If no imports are used or no import uses any file extension an empty {@link List} is returned instead.
	 * 
	 * @param graphModel	the {@link GraphModel} for which's {@link MGLModel} all imported file extensions should be returned
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	a {@link List} that contains all file extensions as {@link String Strings} that are either used in any 
	 * 			{@link GraphModel} present in an imported {@link MGLModel} or in any imported ecore model. The {@link List}
	 * 			is empty if no import exists or no imports possesses any file extension
	 */
	public static List<String> getUsedExtensions(GraphModel graphModel, IWorkspaceContext workspaceContext) {
		List<String> extensions = new ArrayList<>();
		for (Import i : MGLUtil.mglModel(graphModel).getImports()) {
			if (i.getImportURI().endsWith(".mgl")) {
				List<GraphModel> gm = getImportedGraphModels(i, workspaceContext);
				for (GraphModel n : gm) {
					extensions.add(n.getFileExtension());
				}
			}
			if (i.getImportURI().endsWith(".ecore")) {
				GenModel gm = getImportedGenmodel(i, workspaceContext);
				extensions.add(getFileExtension(gm));
			}
		}
		extensions.add(graphModel.getFileExtension());
		return extensions;
	}
	
	/**
	 * Returns the matching {@link GenModel} of the provided <code>imprt</code>.
	 * This only returns a {@link GenModel} if the <code>imprt</code> imports an ecore model.
	 * If <code>imprt</code> imports a {@link MGLModel} instead, <code>null</code> is returned. 
	 * 
	 * @param imprt	the {@link Import} for which the matching {@link GenModel} should be returned
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	the {@link GenModel} that matches the ecore model <code>imprt</code> imports
	 */
	public static GenModel getImportedGenmodel(Import imprt, IWorkspaceContext workspaceContext) {
		String importURI = imprt.getImportURI();
		if(importURI.endsWith(".ecore")) {
			URI uri = URI.createURI(FilenameUtils.removeExtension(imprt.getImportURI()).concat(".genmodel"));
			File file = workspaceContext.getFile(uri);
			return workspaceContext.getContent(file, GenModel.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the {@link MGLModel} the provided <code>imprt</code> imports.
	 * Returns <code>null</code> if the import doesn't reference a MGL model.
	 * 
	 * @param imprt	the {@link Import} from which the referenced {@link MGLModel} should be returned
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	the {@link MGLModel} referenced in the provided <code>imprt</code>, <code>null</code>
	 * 			if no {@link MGLModel} is referenced
	 */
	public static MGLModel getImportedMGLModel(Import imprt, IWorkspaceContext workspaceContext) {
		URI uri = URI.createURI(imprt.getImportURI(), true);
		File file = workspaceContext.getFile(uri);
		return workspaceContext.getContent(file, MGLModel.class);
	}

	/**
	 * Returns <strong>only the first</strong> {@link GraphModel} defined in the {@link MGLModel} referenced by the provided <code>imprt</code>.
	 * 
	 * @param imprt	the {@link Import} that references the {@link MGLModel} of which the first defined {@link GraphModel} should be returned form
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	the first {@link GraphModel} that is defined in the {@link MGLModel} <code>imprt</code> references 
	 */
	public static GraphModel getImportedGraphModel(Import imprt, IWorkspaceContext workspaceContext) {
		URI uri = URI.createURI(imprt.getImportURI(), true);
		File file = workspaceContext.getFile(uri);
		return workspaceContext.getContent(file, GraphModel.class);
	}
	
	/**
	 * Return the {@link GraphModel GraphModels} contained in the provided <code>imprt</code>'s {@link MGLModel}.
	 * If the <code>imprt</code> does not point on a {@link MGLModel} or no {@link GraphModel GraphModels} are contained
	 * in it, an empty list is returned instead.
	 * 
	 * @param imprt	the {@link Import} to retrieve the {@link GraphModel GraphModels} from
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return	a {@link List} that contains every {@link GraphModel} that has been defined in the {@link MGLModel}
	 * 			the provided <code>imprt</code> points at. The {@link List} is empty if no {@link GraphModel} is contained
	 * 			or <code>imprt</code> points not at an {@link MGLModel}
	 */
	public static List<GraphModel> getImportedGraphModels(Import imprt, IWorkspaceContext workspaceContext) {
		URI uri = URI.createURI(imprt.getImportURI(), true);
		File file = workspaceContext.getFile(uri);
		ArrayList<GraphModel> resultList = new ArrayList<GraphModel>();
		resultList.addAll(workspaceContext.getContent(file, MGLModel.class).getGraphModels());
		return resultList;
	}
	
	private static String getFileExtension(GenModel genModel) {
		for (GenPackage gp : genModel.getAllGenPackagesWithClassifiers()) {
			return gp.getFileExtension();
		}
		
		return "";
	}

	/**
	 * Returns the {@link CincoProduct} the provided <code>cpdFile</code> contains.
	 * 
	 * @param cpdPath the URI which points at an cpd file of which the {@link CincoProduct} should be returned
	 * @return	the {@link CincoProduct} the provided <code>cpdFile</code> contains
	 * @throws	RuntimeException	thrown if resource referenced by <code>cpdFile</code> could not be loaded or is empty
	 */
	public static CincoProduct getCPD(URI uri) {
			Resource res = new ResourceSetImpl().getResource(uri, true);
			if (res == null)
				throw new RuntimeException("Could not load resource for: " + uri);
			if (res.getContents().isEmpty())
				throw new RuntimeException("Resource: \""+res+ "\" is empty...");
			return (CincoProduct) res.getContents().get(0);
	}
	
	/**
	 * Loads the resource for the specified {@link path}
	 * 
	 * @param path The path describing the resource location
	 * @param res A helper variable: If the path is given as project relative path, this parameter is used to compute the current {@link IProject}
	 */
	public static Resource getResource(String path, Resource res, IWorkspaceContext workspaceContext) {
        if (path == null || path.isEmpty())
        	return null;
    	URI uri = workspaceContext.getFileURI(path);
    	return res.getResourceSet().getResource(uri,true);
	}
	
	/**
	 * Writes the provided <code>contents</code> to the provided <code>file</code>.
	 * If the file already existed its contents are overwritten, otherwise the file is created newly.
	 * 
	 * @param file	the {@link IFile} in which the <code>contents</code> should be written
	 * @param contents	the {@link String} the provided <code>file</code> should contain after executing this method
	 */
	public static void writeContentToFile(File file, String contents) {
			if (file.exists()) {
				if(file.canWrite()) {
					java.io.Writer fileWriter = null;
					try {
						fileWriter = new java.io.FileWriter(file.getAbsolutePath());
						fileWriter.write(contents);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(fileWriter != null) {
							try {
								fileWriter.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					throw new RuntimeException("couldn't write to file: "+file.getAbsolutePath());
				}
			} else {
				File newFile = new File(file.getAbsoluteFile(), contents);
				if(!newFile.exists()) {
					throw new RuntimeException("couldn't create file: "+file.getAbsolutePath()+" (or this method has a bug)");
				}
			}
	}

	/**
	 * Returns the {@link Annotation} of the name <code>annotName</code> present at the provided <code>modelElement</code>.
	 * If no {@link Annotation} or no with a matching <code>annotName</code> is present at the <code>modelElement</code>
	 * <code>null</code> is returned instead.
	 * 
	 * @param modelElement	the {@link ModelElement} that the {@link Annotation} should be retrieved from
	 * @param annotName		the {@link String} representing the name of the desired {@link Annotation}
	 * @return	the {@link Annotation} that matches the <code>annotName</code> and is present at <code>modelElement</code>
	 */
	public static Annotation findAnnotation(ModelElement modelElement, String annotName) {
		EList<Annotation> anno = modelElement.getAnnotations();
		Iterator<Annotation> iter = anno.iterator();
		while(iter.hasNext())
		{
			Annotation next = iter.next();
			if(next.getName().equals(annotName))
			{
				return next;
			}
		}
		return null;
	}
	
	/**
	 * Returns the {@link Annotation} object of the postCreate annotation present at the <code>modelElement</code>.
	 * If no such annotation is present at the <code>modelElement</code> <code>null</code> is returned instead.
	 * 
	 * @param modelElement	the {@link ModelElement} to retrieve the postCreate annotation from
	 * @return	the {@link Annotation} object of the postCreate annotation at the <code>modelElement</code> or <code>null</code>
	 * 			if no such annotation exists for <code>modelElement</code>
	 */
	public static Annotation findAnnotationPostCreate(ModelElement modelElement) {
		return findAnnotation(modelElement, "postCreate");
	}
	
	/**
	 * Returns the {@link Annotation} object of the postMove annotation present at the <code>modelElement</code>.
	 * If no such annotation is present at the <code>modelElement</code> <code>null</code> is returned instead.
	 * 
	 * @param modelElement	the {@link ModelElement} to retrieve the postMove annotation from
	 * @return	the {@link Annotation} object of the postMove annotation at the <code>modelElement</code> or <code>null</code>
	 * 			if no such annotation exists for <code>modelElement</code>
	 */
	public static Annotation findAnnotationPostMove(ModelElement modelElement) {
		return findAnnotation(modelElement, "postMove");
	}
	
	/**
	 * Returns the {@link Annotation} object of the postResize annotation present at the <code>modelElement</code>.
	 * If no such annotation is present at the <code>modelElement</code> <code>null</code> is returned instead.
	 * 
	 * @param modelElement	the {@link ModelElement} to retrieve the postResize annotation from
	 * @return	the {@link Annotation} object of the postResize annotation at the <code>modelElement</code> or <code>null</code>
	 * 			if no such annotation exists for <code>modelElement</code>
	 */
	public static Annotation findAnnotationPostResize(ModelElement modelElement) {
		return findAnnotation(modelElement, "postResize");
	}
	
	/**
	 * Returns the {@link Annotation} object of the postSelect annotation present at the <code>modelElement</code>.
	 * If no such annotation is present at the <code>modelElement</code> <code>null</code> is returned instead.
	 * 
	 * @param modelElement	the {@link ModelElement} to retrieve the postSelect annotation from
	 * @return	the {@link Annotation} object of the postSelect annotation at the <code>modelElement</code> or <code>null</code>
	 * 			if no such annotation exists for <code>modelElement</code>
	 */
	public static Annotation findAnnotationPostSelect(ModelElement modelElement) {
		return findAnnotation(modelElement, "postSelect");
	}
	
	/**
	 * Returns the {@link Annotation} object of the doubleClickAction annotation present at the <code>modelElement</code>.
	 * If no such annotation is present at the <code>modelElement</code> <code>null</code> is returned instead.
	 * 
	 * @param modelElement	the {@link ModelElement} to retrieve the doubleClickAction annotation from
	 * @return	the {@link Annotation} object of the doubleClickAction annotation at the <code>modelElement</code> or <code>null</code>
	 * 			if no such annotation exists for <code>modelElement</code>
	 */
	public static Annotation findAnnotationDoubleClick(ModelElement modelElement) {
		return findAnnotation(modelElement, "doubleClickAction");
	}
	
}
