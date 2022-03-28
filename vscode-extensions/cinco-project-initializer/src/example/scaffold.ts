import * as path from 'path'
import { createWriteToFile } from '../helper/toolHelper';
import { ScaffoldData } from "./common-types";

export function initializeScaffold(
    workspaceFsPath: string,
    data: ScaffoldData,
): void {
    const modelDirectory = path.join(workspaceFsPath, 'model');
    createWriteToFile(
        modelDirectory,
        `${data.modelName}.cpd`,
        generateCPD(data.modelName),
    );
    createWriteToFile(
        modelDirectory,
        `${data.modelName}.mgl`,
        generateMGL(data),
    );
    createWriteToFile(
        modelDirectory,
        `${data.modelName}.style`,
        generateStyle(),
    );
}

function generateCPD(modelName: string): string {
    return (
`cincoProduct ${modelName}Tool {
    mgl "model/${modelName}.mgl"
}
`
    );
}

function generateMGL(data: ScaffoldData): string {
    return (
`id ${data.packageName}.mglid
stylePath "model/${data.modelName}.style"

graphModel ${data.modelName}GraphModel {
	diagramExtension "${data.modelName.toLowerCase()}"
	
	containableElements(SomeNode)
}  

node SomeNode {
	style labeledCircle("\${label}")
	
	incomingEdges (*)
	outgoingEdges (*)
	attr EString as label
}	
	
edge Transition {
	style simpleArrow
}
`
    );
}

function generateStyle(): string {
    return(
`appearance default {
	lineWidth 2
	background (229,229,229)
}

nodeStyle labeledCircle (1){
	ellipse {
		appearance default
		size(40,40)
		text {
			position ( CENTER, MIDDLE )
			value "%s"
		}
	}
}

edgeStyle simpleArrow {
	appearance default
	
	decorator {
		location (1.0) // at the end of the edge
		ARROW
		appearance default 
	}
}
`
    );
}
