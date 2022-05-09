package de.jabc.cinco.meta.core.ge.style.generator.runtime.highlight;

import graphmodel.ModelElement;
import info.scce.pyro.core.command.types.HighlightCommand;
import info.scce.pyro.core.highlight.HighlightFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.graphiti.util.*;

public class Highlight {


	private Set<ModelElement> pes = new HashSet<>();

	private LightType lightType = LightType.Default;

	private IColorConstant foreground;
	private IColorConstant background;

	private HighlightFactory highlightFactory;

	private boolean isOn = false;

	public Highlight() {
		this(IColorConstant.BLUE,IColorConstant.DARK_BLUE);

	}


	public Highlight(Highlight archetype) {
		this(archetype.getForegroundColor(), archetype.getBackgroundColor());
		pes = archetype.pes;
	}

	public Highlight(IColorConstant foreground,IColorConstant background) {
		highlightFactory = HighlightFactory.eINSTANCE;
		this.foreground = foreground;
		this.background = background;
	}

	private void addToGlobal(ModelElement me) {
		HighlightCommand hc = new HighlightCommand();
		hc.setId(Long.valueOf(me.getId()));

		hc.setForegroundColorR(foreground.getRed());
		hc.setForegroundColorG(foreground.getGreen());
		hc.setForegroundColorB(foreground.getBlue());

		hc.setBackgroundColorR(background.getRed());
		hc.setBackgroundColorG(background.getGreen());
		hc.setBackgroundColorB(background.getBlue());

		hc.setLightType(lightType.name());

		Optional<HighlightCommand> opt = highlightFactory.getCommandExecuter().getHighlightings().stream().filter(n->(n.getId()+"").equals(me.getId())).findAny();
		if(opt.isPresent()) {
			highlightFactory.getCommandExecuter().getHighlightings().remove(opt.get());
		}
		highlightFactory.getCommandExecuter().getHighlightings().add(hc);
	}

	public Highlight add(ModelElement me) {
		pes.add(me);
		if(isOn) {
			addToGlobal(me);
		}
		return this;
	}


	public Highlight remove(ModelElement me) {
		pes.remove(me);
		if(isOn) {
			Optional<HighlightCommand> opt = highlightFactory.getCommandExecuter().getHighlightings().stream().filter(n->(n.getId()+"").equals(me.getId())).findAny();
			if(opt.isPresent()) {
				highlightFactory.getCommandExecuter().getHighlightings().remove(opt.get());
			}
		}
		return this;
	}

	public Highlight clear() {
		if (isOn()) {
			off();
		}
		pes.clear();
		return this;
	}


	public boolean isOn() {
		return this.isOn;
	}

	public IColorConstant getForegroundColor() {
		return foreground;
	}


	public IColorConstant getBackgroundColor() {
		return background;
	}


	/**
	 * red, green and blue values expressed as integers in the range 0 to 255
	 * (where 0 is black and 255 is full brightness).
	 */
	public Highlight setForegroundColor(int red, int green, int blue) {
		foreground = new ColorConstant(red,green,blue);
		refreshAll();
		return this;
	}

	/**
	 * RGB values in hexadecimal format. This means, that the String must have a
	 * length of 6 characters. Example: <code>"FF0000"</code> represents a red
	 * color.
	 * 
	 */
	public Highlight setForegroundColor(String hexRGBString) {
		foreground = new ColorConstant(hexRGBString);
		refreshAll();
		return this;
	}

	public Highlight setForegroundColor(IColorConstant fgColor) {
		foreground = fgColor;
		refreshAll();
		return this;
	}


	public Highlight setBackgroundColor(IColorConstant bgColor) {
		this.background = bgColor;
		refreshAll();
		return this;
	}

	/**
	 * red, green and blue values expressed as integers in the range 0 to 255
	 * (where 0 is black and 255 is full brightness).
	 */
	public Highlight setBackgroundColor(int red, int green, int blue) {
		background = new ColorConstant(red,green,blue);
		refreshAll();
		return this;
	}

	/**
	 * RGB values in hexadecimal format. This means, that the String must have a
	 * length of 6 characters. Example: <code>"FF0000"</code> represents a red
	 * color.
	 * 
	 */
	public Highlight setBackgroundColor(String hexRGBString) {
		background = new ColorConstant(hexRGBString);
		refreshAll();
		return this;
	}
	
	public Highlight setColors(IColorConstant fgColor, IColorConstant bgColor) {
		this.foreground = fgColor;
		this.background = bgColor;
		refreshAll();
		return this;
	}
	
	public Highlight on() {
		isOn = true;
		refreshAll();
		return this;
	}

	public Highlight flash() {
		flash(1.0);
		return this;
	}

	public Highlight flash(double effectTimeInSeconds) {
		lightType = LightType.Flash;
		refreshAll();
		return this;
	}

	public Highlight blink() {
		return blink(1.0);
	}

	public Highlight blink(double effectTimeInSeconds) {
		lightType = LightType.Blink;
		refreshAll();
		return this;
	}

	public Highlight swell() {
		return swell(0.5);
	}

	public Highlight swell(double effectTimeInSeconds) {
		lightType = LightType.Swell;
		refreshAll();
		return this;
	}

	public Highlight fade() {
		return fade(0.5);
	}

	public Highlight fade(double effectTimeInSeconds) {
		lightType = LightType.Fade;
		refreshAll();
		return this;
	}


	public Highlight off() {
		isOn = false;
		//remove all elements from the active list
		pes.forEach(m->{
			Optional<HighlightCommand> opt = highlightFactory.getCommandExecuter().getHighlightings().stream().filter(n->(n.getId()+"").equals(m.getId())).findAny();
			if(opt.isPresent()) {
				highlightFactory.getCommandExecuter().getHighlightings().remove(opt.get());
			}
		});
		return this;
	}
	
	public void refreshAll() {
		if(isOn) {
			pes.forEach(m->{
				Optional<HighlightCommand> opt = highlightFactory.getCommandExecuter().getHighlightings().stream().filter(n->(n.getId()+"").equals(m.getId())).findAny();
				if(opt.isPresent()) {
					//update color
					opt.get().setForegroundColorR(getForegroundColor().getRed());
					opt.get().setForegroundColorG(getForegroundColor().getGreen());
					opt.get().setForegroundColorB(getForegroundColor().getBlue());

					opt.get().setBackgroundColorR(getBackgroundColor().getRed());
					opt.get().setBackgroundColorG(getBackgroundColor().getGreen());
					opt.get().setBackgroundColorB(getBackgroundColor().getBlue());
				} else {
					addToGlobal(m);
				}
			});

		}
	}


	public boolean isRefresh() {
		return true;
	}

	public void setRefresh(boolean refresh) {}

}

enum LightType {
	Default, Blink, Swell, Fade, Flash
}