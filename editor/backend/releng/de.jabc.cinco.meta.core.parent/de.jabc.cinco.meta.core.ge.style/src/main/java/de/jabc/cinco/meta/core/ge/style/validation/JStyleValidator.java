package de.jabc.cinco.meta.core.ge.style.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.workspace.IProjectConfigProvider;

import com.google.inject.Inject;

import style.AbstractShape;
import style.ConnectionDecorator;
import style.ContainerShape;
import style.EdgeStyle;
import style.Image;
import style.MultiText;
import style.NodeStyle;
import style.Style;
import style.StylePackage;
import style.Styles;
import style.Text;
import de.jabc.cinco.meta.core.utils.IWorkspaceContext;
import de.jabc.cinco.meta.core.utils.PathValidator;
import de.jabc.cinco.meta.core.utils.WorkspaceContext;

public class JStyleValidator extends AbstractStyleValidator{

	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	public static String NO_PATH = "noPathSpecified";
	public static String INVALID_PATH = "invalidPath";
	
	@Check
	public void imagePath(Image image) {
		if (image.getPath() == null || image.getPath().isEmpty())
 			warning("No Path specified", StylePackage.Literals.IMAGE__PATH, NO_PATH);

		IWorkspaceContext workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, image);
 		boolean exists = PathValidator.checkPath(image, image.getPath(), workspaceContext);
 		if (!exists)
 			error("Path does not exists!", StylePackage.Literals.IMAGE__PATH, INVALID_PATH);
	}
	
	@Check
	public void checkUniqueStyleNames(NodeStyle ns) {		
		List<Style> nodeStyles = getStyles(ns).stream().
				filter( s -> s instanceof NodeStyle && s.getName().equals(ns.getName())).
				collect(Collectors.toList());

		if (nodeStyles.size() >= 2 )
			error("Duplicate nodeStyle definition: " + ns.getName(), StylePackage.Literals.STYLE__NAME);
	}		
	
	@Check
	public void checkUniqueStyleNames(EdgeStyle es) {
		List<Style> tmp = ((Styles) es.eContainer()).getStyles().stream().
				filter( s -> (s instanceof EdgeStyle) && (s.getName().equals(es.getName())) ).
				collect(Collectors.toList());
		
		if (tmp.size() >= 2)
			error("Duplicate edgeStyle definition: " + es.getName(), StylePackage.Literals.STYLE__NAME);
	}	
	
	@Check
	public void checkUniqueDecoratorNames(ConnectionDecorator cd) {
		List<ConnectionDecorator> filtered = ((EdgeStyle) cd.eContainer()).getDecorator().
			stream().filter(tmp -> tmp.getName() != null &&  tmp.getName().equals(cd.getName()) && !tmp.getName().isEmpty()).collect(Collectors.toList());
		if (filtered.size() >= 2)
			error("Duplicate decorator name: " + cd.getName(), StylePackage.Literals.CONNECTION_DECORATOR__NAME);		
	}
	
	@Check
	public void checkNoShapeNamesInDecorator(ConnectionDecorator cd){
		AbstractShape abstractShape = (AbstractShape) cd.getDecoratorShape();
		String sName = abstractShape.getName();
		if (sName != null && !sName.isEmpty())
			error("Naming elements not allowed here. Assign a name directly to the decorator", 
					StylePackage.Literals.CONNECTION_DECORATOR__DECORATOR_SHAPE);
	}

	@Check
	public void checkUniqueShapeNames(AbstractShape as) {
		AbstractShape mainShape = getMainShape(as);
		ArrayList<AbstractShape> shapes = new ArrayList<AbstractShape>();
		getAllShapes(mainShape, shapes);
		List<AbstractShape> namedShapes = shapes.stream().filter(s -> s.getName() != null && s.getName().equals(as.getName())).collect(Collectors.toList());
		if (namedShapes.size() >= 2 )
			error("Duplicate shape name..." , StylePackage.Literals.ABSTRACT_SHAPE__NAME);
		
	}

	@Check 
	public void checkParameterCount(Style s) {
		int parameterCount = s.getParameterCount();
		if (parameterCount < 0)
			error("Negative parameter count not allowed...", 
					StylePackage.Literals.STYLE__PARAMETER_COUNT);
		if (parameterCount > 0) {
			boolean containsTextElement = false;
			List<AbstractShape> shapes = new ArrayList<AbstractShape>();
			if (s instanceof NodeStyle)
				getAllShapes(((NodeStyle) s).getMainShape(), shapes);
			else getAllShapes((EdgeStyle) s, shapes);
			for (AbstractShape as : shapes)
				if ((as instanceof Text || as instanceof MultiText)) {
					containsTextElement = true;
					break;
				}
		
			if (!containsTextElement)
				warning("Parameter count is greater than 1 but no (multi)text element defined...", 
						StylePackage.Literals.STYLE__PARAMETER_COUNT);
		}
	}
	
	private List<Style> getStyles(Style s) {
		return ((Styles) s.eContainer()).getStyles();
	}
	
	private void getAllShapes(AbstractShape as, List<AbstractShape> result) {
		result.add(as);
		if (as instanceof ContainerShape)
			for (AbstractShape s : ((ContainerShape) as).getChildren()) {
				getAllShapes(s, result);
			}
	}
	
	private void getAllShapes(EdgeStyle es, List<AbstractShape> result) {
		for (ConnectionDecorator cd : es.getDecorator()) {
			result.add((AbstractShape) cd.getDecoratorShape());
		}
	}
	
	private AbstractShape getMainShape(AbstractShape as) {
		AbstractShape i = as;
		while (i != null && i.eContainer() instanceof AbstractShape)
			i = (AbstractShape) i.eContainer();
		return i;
	}
}