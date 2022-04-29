import {sprintf} from "../../../../../node_modules/sprintf-js";	


export function format_expression(s, styleArgs){
	 let x = sprintf(s, styleArgs);
	 print(x);
    return x;
}

