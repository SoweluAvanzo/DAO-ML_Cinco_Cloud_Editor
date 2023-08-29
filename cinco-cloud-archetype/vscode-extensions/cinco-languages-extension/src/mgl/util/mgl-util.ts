import { Edge, EdgeElementConnection } from "../../generated/ast";
import { Reference } from "langium";

export function getConnectingEdges(edgeElementConnection: EdgeElementConnection): Edge[] {
    const localConnections = edgeElementConnection.localConnection;
    const result = localConnections.flatMap((localConnection: Reference<Edge>) => {
        return localConnection.$refNode?.element as Edge;
    }).filter((entry: Edge) => entry !== undefined);
    if(result.length > 1) {
        for(const entry of result) {
            console.log(entry.name);
            console.log(entry);
        }
    }
    return result;
}