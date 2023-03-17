import * as path from 'path'
import * as fs from 'fs';
import * as java from '../common/java'
import { MessageToClient, ScaffoldData } from "../common/model";
import { isDirectoryEmpty} from './filesystem-helper';
import * as messages from './messages'

export function initializeScaffold(
    postMessage: (message: MessageToClient) => void,
    workspaceFsPath: string,
    data: ScaffoldData,
): boolean {
    const dataValid =
        java.isValidIdentifier(data.modelName) &&
        java.isValidPackageIdentifier(data.packageName);

    if (!dataValid) {
        postMessage(messages.dataInvalid);
        return false;
    }

    if (!isDirectoryEmpty(workspaceFsPath)) {
        postMessage(messages.clearWorkspace);
        return false;
    }

    const modelDirectory = path.join(workspaceFsPath, 'model');
    fs.mkdirSync(modelDirectory, {recursive: true});
    fs.writeFileSync(
        path.join(modelDirectory, `${data.modelName}.cpd`),
        generateCPD(data.modelName),
    );
    fs.writeFileSync(
        path.join(modelDirectory, `${data.modelName}.mgl`),
        generateMGL(data),
    );
    fs.writeFileSync(
        path.join(modelDirectory, `${data.modelName}.style`),
        generateStyle(),
    );
    postMessage(messages.projectInitialized);
    return true;
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
