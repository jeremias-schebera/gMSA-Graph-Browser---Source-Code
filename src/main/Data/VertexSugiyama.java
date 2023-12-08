package main.Data;

import main.Data.Rectangle;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.driver.types.Node;

import java.io.Serializable;
import java.util.*;
import main.Algorithms.GraphProjectionSugiyama;

public class VertexSugiyama{

    private String vertexText;
    private int id;
    private int dataBaseID;

    private Rectangle rectangle;
    private String text;
    private List<EdgeSugiyama> inEdge = new LinkedList<>();
    private List<EdgeSugiyama> outEdge = new LinkedList<>();
    private boolean isDummyNode;
    private Layer associatedLayer;
    private Block associatedBlock;

    private int sequenceLength;
    private BlockSet associatedBlockSet;
    private boolean isJoinable;
    private boolean isJoinedVertex;
    private List<VertexSugiyama> joinedVertices;

    private EdgesClassifiedByVerticalDirection outEdgesClassifiedByVerticalDirection;
    private EdgesClassifiedByVerticalDirection inEdgesClassifiedByVerticalDirection;
    
    //private Set<Chromosome> subStructures;
    private Set<N4JChromosome> n4JSubStructures;

    //JOIN-Test
    private int idPredecessorForJoining = -1;
    private int idSucessorForJoining = -1;
    private Set<Integer> neighborIDs = new HashSet<Integer>();
    //JOIN-Test
    
    private double x;
    private double y;
    private double width;
    private double height;
    
    //private AdditionalVertexData additionalVertexData;
    private List<AdditionalVertexData> joinedAdditionalVertexData;

    //"standard" Vertex Constructor
    public VertexSugiyama(
        Vertex currentDBVertex,
        boolean isGuideSequence,
        int sequenceLength,
        Chromosome subStructure,
        AdditionalVertexData additionalVertexData
    ) {
        this.id = (int) Integer.valueOf(currentDBVertex.id().toString());
        vertexText = String.valueOf(this.id);
        this.sequenceLength = sequenceLength;
        isJoinable = true;
        //this.subStructures = new HashSet();
        //subStructures.add(subStructure);
        this.joinedAdditionalVertexData = new ArrayList<>();
        this.joinedAdditionalVertexData.add(additionalVertexData);
        //this.additionalVertexData = additionalVertexData;
    }
    
  //"standard" Vertex Constructor for N4J
    public VertexSugiyama(
        Node currentDBVertex,
        boolean isGuideSequence,
        int sequenceLength,
        N4JChromosome subStructure,
        AdditionalVertexData additionalVertexData
    ) {
        this.id = currentDBVertex.get("id").asInt();
        vertexText = String.valueOf(this.id);
        this.sequenceLength = sequenceLength;
        isJoinable = true;
        this.n4JSubStructures = new HashSet();
        n4JSubStructures.add(subStructure);
        this.joinedAdditionalVertexData = new ArrayList<>();
        this.joinedAdditionalVertexData.add(additionalVertexData);
        //this.additionalVertexData = additionalVertexData;
    }

    //"dummy" Vertex Constructer
    public VertexSugiyama(
        boolean isDummyNode,
        int id
    ) {
        this.isDummyNode = isDummyNode;
        this.id = -id;
        vertexText = String.valueOf(this.id);
        isJoinedVertex = false;
        //this.subStructures = new HashSet();
        this.n4JSubStructures = new HashSet();
    }

    //"joined" Vertex Constructer
    public VertexSugiyama(
        int joinedVertexCount,
        List<VertexSugiyama> joinedVertices,
        List<VertexSugiyama> vertexList,
        int id
    ) {
        this.sequenceLength = 0;
        this.id = id;
        vertexText = "[";//joinedVertices.get(0).vertexText + " --> " + joinedVertices.get(joinedVertices.size() - 1).vertexText;
        this.joinedAdditionalVertexData = new ArrayList<>();
        for (VertexSugiyama v : joinedVertices) {
        	vertexText += v.vertexText + " - ";
        }
        vertexText += "]";
        this.joinedVertices = new ArrayList<>();
        //this.subStructures = new HashSet();
        this.n4JSubStructures = new HashSet();
        for (VertexSugiyama includedVertex : joinedVertices) {
            this.sequenceLength += includedVertex.sequenceLength;
            this.joinedVertices.add(includedVertex);
            vertexList.remove(includedVertex);
            //subStructures.addAll(includedVertex.getSubStructures());
            n4JSubStructures.addAll(includedVertex.getN4JSubStructures()); 
            joinedAdditionalVertexData.add(includedVertex.getJoinedAdditionalVertexData().get(0));
        }
        this.isJoinedVertex = true;
        isDummyNode = false;
        
    }
    
    /*public AdditionalVertexData getAdditionalVertexData() {
		return additionalVertexData;
	}*/
    
    public List<AdditionalVertexData> getJoinedAdditionalVertexData() {
		return joinedAdditionalVertexData;
	}

	//JOIN-Test
    public int getIdPredecessorForJoining() {
        return idPredecessorForJoining;
    }

    public void setIdPredecessorForJoining(int idPredecessorForJoining) {
        this.idPredecessorForJoining = idPredecessorForJoining;
    }

    public int getIdSucessorForJoining() {
        return idSucessorForJoining;
    }

    public void setIdSucessorForJoining(int idSucessorForJoining) {
        this.idSucessorForJoining = idSucessorForJoining;
    }
    
    public Set<Integer> getNeighborIDs() {
 		return neighborIDs;
 	}

 	public void addNeighborIDs(int value) {
 		this.neighborIDs.add(value);
 	}
    
    //JOIN-Test

	public void setDrawingParameters(double xCenterCoordinate, double y, double width, double height) {
    	x = xCenterCoordinate - 0.5 * width;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    }
    
//    public void addSubStructure(Chromosome subStructure) {
//    	this.subStructures.add(subStructure);
//    }
//    
//    public Set<Chromosome> getSubStructures() {
//    	return subStructures;
//    }
    
    public void addN4JSubStructure(N4JChromosome subStructure) {
    	this.n4JSubStructures.add(subStructure);
    }
    
    public Set<N4JChromosome> getN4JSubStructures() {
    	return n4JSubStructures;
    }
    
//    public String getClassString() {
//    	String classString = "";
//    	for (Chromosome subStructure : subStructures) {
//    		classString += "s" + subStructure.getSubStructureName() + "g" + subStructure.getGenomeName() + " ";
//    	}
//    	return classString.substring(0, classString.length() - 1);
//    }
    
	  public String getClassString() {
		String classString = "";
		for (N4JChromosome subStructure : n4JSubStructures) {
			classString += "s" + subStructure.getSubStructureName() + "g" + subStructure.getGenomeName() + " ";
		}
		return classString.substring(0, classString.length() - 1);
	}
    
    public double getX() {
    	return x;
    }
    
    public double getY() {
    	return y;
    }
    
    public double getWidth() {
    	return width;
    }
    
    public double getHeight() {
    	return height;
    }
    
    public int getSequenceLength() {
        return sequenceLength;
    }

    public void initializeOutEdgeClassifiedByVerticalDirection() {
        outEdgesClassifiedByVerticalDirection = new EdgesClassifiedByVerticalDirection();
    }

    public void initializeInEdgeClassifiedByVerticalDirection() {
        inEdgesClassifiedByVerticalDirection = new EdgesClassifiedByVerticalDirection();
    }

    public EdgesClassifiedByVerticalDirection getOutEdgesClassifiedByVerticalDirection() {
        return outEdgesClassifiedByVerticalDirection;
    }

    public EdgesClassifiedByVerticalDirection getInEdgesClassifiedByVerticalDirection() {
        return inEdgesClassifiedByVerticalDirection;
    }

    public boolean isJoinedVertex() {
        return isJoinedVertex;
    }

    public boolean isJoinable() {
        return isJoinable;
    }

    public void setJoinable(boolean joinable) {
        isJoinable = joinable;
    }

    public BlockSet getAssociatedBlockSet() {
        return associatedBlockSet;
    }

    public void setAssociatedBlockSet(BlockSet associatedBlockSet) {
        this.associatedBlockSet = associatedBlockSet;
    }

    public Block getAssociatedBlock() {
        return associatedBlock;
    }

    public void setAssociatedBlock(Block associatedBlock) {
        this.associatedBlock = associatedBlock;
    }

    public void setAssociatedLayer(Layer layer) {
        this.associatedLayer = layer;
    }

    public Layer getAssociatedLayer() {
        return associatedLayer;
    }

    public void addInEdge(EdgeSugiyama inEdge) {
        this.inEdge.add(inEdge);
    }

    public void removeInEdge(EdgeSugiyama inEdge) {
        this.inEdge.remove(inEdge);
    }

    public void addOutEdge(EdgeSugiyama outEdge) {
        this.outEdge.add(outEdge);
    }

    public void removeOutEdge(EdgeSugiyama outEdge) {
        this.outEdge.remove(outEdge);
    }

    public int getOutDegree() {
        return outEdge.size();
    }

    public List<EdgeSugiyama> getInEdges() {
        return inEdge;
    }

    public List<EdgeSugiyama> getOutEdges() {
        return outEdge;
    }


    public boolean isDummyNode() {
        return isDummyNode;
    }



    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVertexText() {
        return vertexText;
    }


    public Integer getId() {
        return id;
    }

    public void setId(int id) {
    	this.id = id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
}
