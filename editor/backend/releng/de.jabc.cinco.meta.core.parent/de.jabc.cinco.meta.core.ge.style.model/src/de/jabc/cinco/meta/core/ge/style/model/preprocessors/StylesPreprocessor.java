package de.jabc.cinco.meta.core.ge.style.model.preprocessors;

import style.AbsolutPosition;
import style.AbstractShape;
import style.ContainerShape;
import style.NodeStyle;
import style.Polygon;
import style.Size;
import style.Style;
import style.StyleFactory;
import style.Styles;

public class StylesPreprocessor {

	public static void preprocess(Styles styles) {
		for (Style s : styles.getStyles()) {
			if (s instanceof NodeStyle) {
				AbstractShape ms = ((NodeStyle) s).getMainShape();
				adjustSizes(ms);
				setPolygonPositions(ms);
			}
		}
	}

	private static void adjustSizes(AbstractShape ms) {
		Size size = ms.getSize();
		boolean heightFixed = size != null ? size.isHeightFixed() : false;
		boolean widthFixed = size != null ? size.isWidthFixed() : false;
		
		if (ms instanceof ContainerShape) {
			for (AbstractShape as : ((ContainerShape) ms).getChildren()) {
				if (heightFixed || widthFixed)
					setSize(as, widthFixed, heightFixed);
				else adjustSizes(as);
			}
		}
	}

	private static void setSize(AbstractShape as, boolean widthFixed, boolean heightFixed) {
		propagateSizeFix(as, widthFixed, heightFixed);
		adjustSizes(as);
	}

	private static void propagateSizeFix(AbstractShape as, boolean widthFixed, boolean heightFixed) {
		if (as.getSize() != null) {
			as.getSize().setWidthFixed(widthFixed);
			as.getSize().setHeightFixed(heightFixed);
		}
	}
	
	private static void setPolygonPositions(AbstractShape ms) {
		if (ms instanceof Polygon) {
			Polygon p = (Polygon) ms;
			if (p.getPosition() == null) {
				AbsolutPosition pos = setPosition(p);
				p.setPosition(pos);
			}
		}
	}

	private static AbsolutPosition setPosition(Polygon p) {
		AbsolutPosition pos = StyleFactory.eINSTANCE.createAbsolutPosition();
		pos.setXPos(p.getPoints().get(0).getX());
		pos.setYPos(p.getPoints().get(0).getY());
		return pos;
	}
}
