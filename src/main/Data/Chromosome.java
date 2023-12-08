package main.Data;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

public class Chromosome{

    private List<Vertex> graphDBVerticesPath;
    private Vertex associatedChromosomeVertex;
    private List<VertexSugiyama> sugiyamaVertices;
    private boolean isSuperGenome = false;
    private Color color;
    private String subStructureName;
    private String genomeName;

    public Chromosome(
        Vertex associatedChromosomeVertex,
        List<Vertex> graphDBVerticesPath,
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
    
    public List<Vertex> getGraphDBVerticesPath() {
        return graphDBVerticesPath;
    }

    public Vertex getAssociatedChromosomeVertex() {
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
        if (this.getAssociatedChromosomeVertex().property("name").isPresent()) {
            text = this.getAssociatedChromosomeVertex().value("name").toString() + ": ";
        } else {
            text = "supergenom: ";
        }
        for (Vertex v : this.getGraphDBVerticesPath()) {
            text += v.value("id").toString() + " -> ";
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
