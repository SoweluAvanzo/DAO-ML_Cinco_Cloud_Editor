export const PROJECT_NAME_REGEXP = "^[_a-zA-Z][\\w_]*$";
export function generateMGL(name: string, mslFileName: string): string {
    return `stylePath "${mslFileName}"

graphModel ${name}GraphModel {
    diagramExtension "${name.toLowerCase()}"
    
    containableElements(SomeNode)
}  

node SomeNode {
    style labeledCircle("\${label}")
    
    incomingEdges (Transition)
    outgoingEdges (Transition)
    attr string as label
}	
    
edge Transition {
    style simpleArrow
}
`;
}

export function generateMSL(): string {
return `appearance default {
    background (229,229,229)
    lineWidth 2
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
`;
}