package main.Data;

import org.neo4j.driver.types.Node;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

public class N4JChromosome{

    private List<Node> graphDBVerticesPath;
    private Node associatedChromosomeVertex;
    private List<VertexSugiyama> sugiyamaVertices;
    private boolean isSuperGenome = false;
    private Color color;
    private String subStructureName;
    private String genomeName;

    public N4JChromosome(
    	Node associatedChromosomeVertex,
        List<Node> graphDBVerticesPath,
        Color color,
        String subStructureName,
        String genomeName
    ) {
        this.graphDBVerticesPath = graphDBVerticesPath;
        this.associatedChromosomeVertex = associatedChromosomeVertex;
        this.color = color;
        this.subStructureName = subStructureName;
        this.genomeName = genomeName;

        if (associatedChromosomeVertex == null) {
            isSuperGenome = true;
        }
    }

    public String getColorInHex() {
    	String buf = Integer.toHexString(color.getRGB());
        String hexColor = "#"+buf.substring(buf.length()-6);
        return hexColor;
    }
    
    public List<Node> getGraphDBVerticesPath() {
        return graphDBVerticesPath;
    }

    public Node getAssociatedChromosomeVertex() {
        return associatedChromosomeVertex;
    }

    public List<VertexSugiyama> getSugiyamaVertices() {
        return sugiyamaVertices;
    }

    public void setSugiyamaVertices(List<VertexSugiyama> sugiyamaVertices) {
        this.sugiyamaVertices = sugiyamaVertices;
    }

    public boolean isSuperGenome() {
        return isSuperGenome;
    }

    public Color getColor() {
        return color;
    }

    public String getComposedName() {
        return subStructureName + " (" + genomeName + ")";
    }
    
    public String getSubStructureName() {
        return subStructureName;
    }
    
    public String getGenomeName() {
        return genomeName;
    }

    @Override
    public String toString() {
        String text;
        if (!this.getAssociatedChromosomeVertex().get("name").isEmpty()) {
            text = this.getAssociatedChromosomeVertex().get("name").toString() + ": ";
        } else {
            text = "supergenom: ";
        }
        for (Node v : this.getGraphDBVerticesPath()) {
            text += v.get("id").toString() + " -> ";
        }
        return text;
    }

    public String toString2() {
        String text = "";
//        if (this.getAssociatedChromosomeVertex().property("name").isPresent()) {
//            text = this.getAssociatedChromosomeVertex().value("name").toString() + ": ";
//        } else {
//            text = "supergenom: ";
//        }
        for (VertexSugiyama v : this.getSugiyamaVertices()) {
            text += v.getText();
            if (v != this.getSugiyamaVertices().get(this.getSugiyamaVertices().size() - 1)) {
                text += " -> ";
            }
        }
        return text;
    }
}
