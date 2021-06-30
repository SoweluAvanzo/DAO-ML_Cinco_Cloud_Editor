package de.jabc.cinco.meta.plugin.pyro.canvas

import style.Appearance
import style.BooleanEnum
import style.Color
import style.Font
import style.LineStyle
import style.StylePackage

class PyroAppearance {
	
	public float angle = 0.0f
	
	Color background
	Color foreground
	
	public BooleanEnum filled = BooleanEnum.UNDEF
	Font font
	public String imagePath
	Boolean lineInVisible
	LineStyle lineStyle = LineStyle.UNSPECIFIED
	
	public int lineWidth = 1
	public double transparency = 0.0
	
	new(Appearance app) {
		app.merge(this)
	}
	new() {
		
	}
	
	def Color background(){
		if(this.background===null){
			val defColor = StylePackage.eINSTANCE.styleFactory.createColor
			defColor.r = 255
			defColor.g = 255
			defColor.b = 255
			return defColor
		}
		return this.background
	}
	
	def Color foreground(){
		if(this.foreground===null){
			val defColor = StylePackage.eINSTANCE.styleFactory.createColor
			defColor.r = 0
			defColor.g = 0
			defColor.b = 0
			return defColor
		}
		return this.foreground
	}
	
	def Font font(){
		if(this.font===null){
			val defFont = StylePackage.eINSTANCE.styleFactory.createFont
			defFont.fontName = "Helvetica"
			defFont.isBold = false
			defFont.isItalic = false
			defFont.size = 12
			return defFont
		}
		return this.font
	}
	
	def LineStyle lineStyle(){
		if(this.lineStyle==LineStyle.UNSPECIFIED){
			return LineStyle.SOLID
		}
		return this.lineStyle
	}
	
	def int lineWidth(){
		if(this.lineWidth==0){
			return 2
		}
		return this.lineWidth
	}
	
	def Boolean lineInVisible(){
		if(this.lineInVisible===null){
			return false
		}
		return this.lineInVisible
	}
	
	def void merge(Appearance app,PyroAppearance pyroApp){
		if(pyroApp.angle==0 && app.angle > 0){
			pyroApp.angle = app.angle
		}
		if(pyroApp.background===null){
			pyroApp.background = app.background
		}
		if(pyroApp.foreground===null){
			pyroApp.foreground = app.foreground
		}
		if(pyroApp.filled == BooleanEnum.UNDEF) {
			pyroApp.filled = app.filled
		}
		if(pyroApp.font === null){
			pyroApp.font = app.font
		}
		if(pyroApp.imagePath === null){
			pyroApp.imagePath = app.imagePath
		}
		if(pyroApp.lineInVisible === null){
			pyroApp.lineInVisible = app.lineInVisible
		}
		if(pyroApp.lineStyle == LineStyle.UNSPECIFIED){
			pyroApp.lineStyle = app.lineStyle
		}
		if(pyroApp.lineWidth == 1 && app.lineWidth > 0){
			pyroApp.lineWidth = app.lineWidth
		}
		if(pyroApp.transparency == 0 && app.transparency > 0){
			pyroApp.transparency = app.transparency
		}
		
		if(app.parent!==null) {
			app.parent.merge(pyroApp)
		}
	}
}
