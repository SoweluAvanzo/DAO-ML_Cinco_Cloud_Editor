/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
export const PROJECT_NAME_REGEXP = '^[_a-zA-Z][\\w_]*$';
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
