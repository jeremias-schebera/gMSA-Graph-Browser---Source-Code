package main.Data;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.driver.types.Node;

public class N4JAlignmentBlockAssociation {
	private Map<Node, Node> alignmentBlockAssociation;

    public N4JAlignmentBlockAssociation() {
        alignmentBlockAssociation = new HashMap<>();
    }

    public void add(
        Node vertex,
        Node alignmentBlock
    ) {
        alignmentBlockAssociation.put(vertex, alignmentBlock);
    }
    
    public int getLength(
    	Node vertex
    ) {
        return alignmentBlockAssociation.get(vertex).get("length").asInt();
    }
}
