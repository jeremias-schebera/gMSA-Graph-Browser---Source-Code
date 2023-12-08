package main.websocket;

public class DrawingParameterMessage {
	private double minDrawLengthVertex;
    private double maxDrawLengthVertex;
    private double minDrawHeightVertex;
    private double minHorizontalInterSpace;
    private double minTriangleHeight;
    private double triangleLength;
    
    public DrawingParameterMessage(double minDrawLengthVertex, double maxDrawLengthVertex, double minDrawHeightVertex, double minVerticlaInterSpace, double minTriangleHeight, double triangleLength) {
    	this.minDrawLengthVertex = minDrawLengthVertex;
    	this.maxDrawLengthVertex = maxDrawLengthVertex;
        this.minDrawHeightVertex = minDrawHeightVertex;
        this.minHorizontalInterSpace = minVerticlaInterSpace;
        this.minTriangleHeight = minTriangleHeight;
        this.triangleLength = triangleLength;
        System.out.println(minDrawLengthVertex + ", " + maxDrawLengthVertex + ", " + minDrawHeightVertex + ", " + minVerticlaInterSpace + ", " + minTriangleHeight + ", " + triangleLength);
    }

	public double getMinDrawLengthVertex() {
		return minDrawLengthVertex;
	}

	public double getMaxDrawLengthVertex() {
		return maxDrawLengthVertex;
	}

	public double getMinDrawHeightVertex() {
		return minDrawHeightVertex;
	}

	public double getMinHorizontalInterSpace() {
		return minHorizontalInterSpace;
	}

	public double getMinTriangleHeight() {
		return minTriangleHeight;
	}

	public double getTriangleLength() {
		return triangleLength;
	}
    
}
