import 'dart:math';
import 'dart:html';
import 'dart:convert';

// useful links:
// 	https://www.rapidtables.com/convert/color/rgb-to-hsl.html
// 	https://www.rapidtables.com/convert/color/hsl-to-rgb.html
// 	https://stackoverflow.com/questions/5623838/rgb-to-hex-and-hex-to-rgb

class RGBValue {
	int r;
	int g;
	int b;
	
	RGBValue(this.r, this.g, this.b);
	
	static RGBValue fromHexValue(String hex) {
		var regex = new RegExp(r"^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$");
        int r = 0;
        int g = 0;
        int b = 0;
        for (var match in regex.allMatches(hex)) {
		  r = int.parse(match[1], radix: 16);
          g = int.parse(match[2], radix: 16);
          b = int.parse(match[3], radix: 16);
	  	}
	  	return new RGBValue(r, g, b);
	}
	
	String toHexValue() {
		var value = ((1 << 24) + (r << 16) + (g << 8) + b).toRadixString(16);
		return value.substring(1, value.length);
	}
}

class HSLValue {
    int h = 0;
    double s = 0;
    double l = 0;
    
    HSLValue(this.h, this.s, this.l) {}

    static HSLValue fromHexValue(String hex) {       
        var rgbVal = RGBValue.fromHexValue(hex);
	  	
	  	double r2 = rgbVal.r/255;
	  	double g2 = rgbVal.g/255;
	  	double b2 = rgbVal.b/255;
	  	
	  	double cmax = max(r2, max(g2, b2));
	  	double cmin = min(r2, min(g2, b2));
	  	double delta = cmax - cmin;
	  	
	  	double l = (cmax + cmin) / 2;
      	double s = delta == 0 ? 0 : (delta / (1 - (2 * l - 1).abs()));
      	double h = 0;
      	if (delta == 0) {
      		h = 0;
      	} else if (cmax == r2) {
      		h = 60 * (((g2-b2)/delta) % 6);
      	} else if (cmax == g2) {
      		h = 60 * ((b2-r2)/delta + 2);
      	} else if (cmax == b2) {
      		h = 60 * ((r2-g2)/delta + 4);
      	}
                
       	return new HSLValue(h.round(), s, l);
    } 

    String toHexValue() {
    	
    	// convert hsl to rgb
    	double c = (1 - (2*l - 1).abs()) * s;
    	double x = c * (1 - ((h/60) % 2 - 1).abs());
    	double m = l - c/2;
    	
    	double r2 = 0.0;
    	double g2 = 0.0;
    	double b2 = 0.0;
    	
    	if (0 <= h && h < 60) {
    		r2 = c;
    		g2 = x;
    	} else if (60 <= h && h < 120) {
    		r2 = x;
    		g2 = c;
    	} else if (120 <= h && h < 180) {
    		g2 = c;
    		b2 = x;
    	} else if (180 <= h && h < 240) {
    		g2 = x;
    		b2 = c;
    	} else if (240 <= h && h < 300) {
    		r2 = x;
    		b2 = c;
    	} else if (300 <= h && h < 360) {
    		r2 = c;
    		b2 = x;
    	}
    	
    	RGBValue rgbVal = new RGBValue(((r2 + m) * 255).round(), ((g2 + m) * 255).round(), ((b2 + m) * 255).round());    	    	
        return rgbVal.toHexValue();
    }

    HSLValue lighten(int percent) {    
        l = min(l + percent/100, 1);
        return this;
    }

    HSLValue darken(int percent) {    
        l = max(l - percent/100, 0);        
        return this;
    }
}
