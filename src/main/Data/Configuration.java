/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Data;

import java.awt.Color;
import java.util.HashMap;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.driver.types.Node;

/**
 *
 * @author zeckzer
 */
public class Configuration {

    //private SequenceVerticesData sequenceVerticesData;
    //private AlignmentBlockAssociation alignmentBlockAssociation;
    private Boolean isJoinEnabled;
    private double drawingThicknessFactor;
    private int spaceFactor;
    //private HashMap<Vertex, AdditionalVertexData> additionalVertexData;
    private double neededDummyVertexWidth;
    
    private N4JSequenceVerticesData n4JSequenceVerticesData;
    private N4JAlignmentBlockAssociation n4JAlignmentBlockAssociation;
    private HashMap<Node, AdditionalVertexData> n4JAdditionalVertexData;

    //Test!!!
//    public StringBuffer csvText = new StringBuffer();
//    public void setCsvText(StringBuffer csvText) {
//        this.csvText = csvText;
//    }
//    public StringBuffer getCsvText() {
//        return csvText;
//    }
    //Test!!!
    
    private static FreeColor freeColor = new FreeColor();
    
    public Configuration() {
    	
    }
    
    public Configuration(Configuration oldConfiguration, double drawingThicknessFactor, int spaceFactor) {
    	this.drawingThicknessFactor = drawingThicknessFactor;
    	this.spaceFactor = spaceFactor;
    	//this.additionalVertexData = oldConfiguration.additionalVertexData;
    	this.isJoinEnabled = oldConfiguration.isJoinEnabled;
//    	this.alignmentBlockAssociation = oldConfiguration.alignmentBlockAssociation;
//    	this.sequenceVerticesData = oldConfiguration.sequenceVerticesData;
    	this.freeColor = oldConfiguration.freeColor;
    	//this.neededDummyVertexWidth = oldConfiguration.neededDummyVertexWidth;
    }
    
    public void setNeededDummyVertexWidth(double neededDummyVertexWidth) {
    	this.neededDummyVertexWidth = neededDummyVertexWidth;
    }
    
    public double getNeededDummyVertexWidth() {
    	return neededDummyVertexWidth;
    }
    
//    public HashMap<Vertex, AdditionalVertexData> getAdditionalVertexData() {
//		return additionalVertexData;
//	}
//
//	public void setAdditionalVertexData(HashMap<Vertex, AdditionalVertexData> additionalVertexData) {
//		this.additionalVertexData = additionalVertexData;
//	}
	
	public HashMap<Node, AdditionalVertexData> getN4JAdditionalVertexData() {
		return n4JAdditionalVertexData;
	}

	public void setN4JAdditionalVertexData(HashMap<Node, AdditionalVertexData> n4JAdditionalVertexData) {
		this.n4JAdditionalVertexData = n4JAdditionalVertexData;
	}
	
	public N4JSequenceVerticesData getN4JSequenceVerticesData() {
		return n4JSequenceVerticesData;
	}

	public void setN4JSequenceVerticesData(N4JSequenceVerticesData n4jSequenceVerticesData) {
		n4JSequenceVerticesData = n4jSequenceVerticesData;
	}

	public N4JAlignmentBlockAssociation getN4JAlignmentBlockAssociation() {
		return n4JAlignmentBlockAssociation;
	}

	public void setN4JAlignmentBlockAssociation(N4JAlignmentBlockAssociation n4jAlignmentBlockAssociation) {
		n4JAlignmentBlockAssociation = n4jAlignmentBlockAssociation;
	}

//	public SequenceVerticesData getSequenceVerticesData() {
//        return sequenceVerticesData;
//    }
//
//    public void setSequenceVerticesData(SequenceVerticesData sequenceVerticesData) {
//        this.sequenceVerticesData = sequenceVerticesData;
//    }
//
//    public AlignmentBlockAssociation getAlignmentBlockAssociation() {
//        return alignmentBlockAssociation;
//    }
//
//    public void setAlignmentBlockAssociation(AlignmentBlockAssociation alignmentBlockAssociation) {
//        this.alignmentBlockAssociation = alignmentBlockAssociation;
//    }

    public Boolean getIsJoinEnabled() {
        return isJoinEnabled;
    }

    public void setIsJoinEnabled(Boolean isJoinEnabled) {
        this.isJoinEnabled = isJoinEnabled;
    }

    public double getDrawingThicknessFactor() {
        return drawingThicknessFactor;
    }

    public void setDrawingThicknessFactor(double drawingThicknessFactor) {
        this.drawingThicknessFactor = drawingThicknessFactor;
    }

    public int getSpaceFactor() {
        return spaceFactor;
    }

    public void setSpaceFactor(int spaceFactor) {
        this.spaceFactor = spaceFactor;
    }

    public static Color getDefaultColor() {
        return freeColor.getDefaultColor();
    }
}
