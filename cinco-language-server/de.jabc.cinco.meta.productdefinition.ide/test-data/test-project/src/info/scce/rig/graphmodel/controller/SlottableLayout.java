package info.scce.rig.graphmodel.controller;

import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.Slot;
import info.scce.rig.pipeline.Slottable;
import info.scce.rig.pipeline.Target;

public class SlottableLayout {
	
	public static final int HEADER_HEIGHT = 30;
	public static final int FOOTER_HEIGHT = 20;
	
	public static final int MIN_WIDTH = 200;
	public static final int MAX_WIDTH = 500;
	
	private static boolean suppress = false;
	
	public static final int clampWidth (int width) {
		return Math.min(MAX_WIDTH, Math.max(MIN_WIDTH, width));
	}
	
	public static void layout(Slottable slottable) {
		layout(slottable, null);
	}
	
	public static void layout (Slottable slottable, Slot ignore) {
		if (suppress) return;
		
		int width = clampWidth(slottable.getWidth());
		int height = HEADER_HEIGHT;
		
		for (Slot slot : slottable.getNodes(Slot.class)) {
			if (slot == ignore) continue;
			
			boolean hasIncoming = !slot.getIncoming().isEmpty();
			
			if (hasIncoming || !slottable.isMinimized()) {
				// setWidth does not correctly update text positions, use resize
				slot.resize(width, slot.getHeight());
				slot.move(0, height);
				height += slot.getHeight();

			} else {
				slot.move(0, -slot.getHeight());
			}
		}
		
		if (slottable instanceof Job || slottable instanceof Target 
				|| (height == HEADER_HEIGHT && !slottable.isMinimized()))
			height += FOOTER_HEIGHT;
		
		suppress = true;
		slottable.resize (width, height);
		suppress = false;
	}
}
