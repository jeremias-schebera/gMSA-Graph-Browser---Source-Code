package main.Algorithms;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import main.Algorithms.GraphProjectionSugiyama;
import main.Data.*;
import main.websocket.ArrowMessage;
import main.websocket.DrawingParameterMessage;
import main.websocket.EdgeMessage;
import main.websocket.VertexMessage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.tinkerpop.gremlin.structure.Vertex;


import org.mdiutil.swing.ExtensionFileFilter;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import javax.imageio.ImageIO;

public class DrawingController {

    private double maxSequenceLength;
    private double minSequenceLength;
    private double diffSequenceLength;

    private double minDrawLengthVertex;
    private double maxDrawLengthVertex;
    private double minDrawHeightVertex;
    private double minHorizontalInterSpace;
    private double minTriangleHeight;
    private double triangleLength;
    private double diffDrawLengthVertex;
    private double neededDummyVertexWidth;
    
    private double maxX = Double.MIN_VALUE;
    private double maxY = Double.MIN_VALUE;;
    
    private List<VertexMessage> vertexMessages;
    //private List<VertexMessage> gmlVertexMessages;
    private List<EdgeMessage> edgeMessages;
    private List<ArrowMessage> arrowMessages;
    
    private AfterDrawingGraphData afterDrawingGraphData;
    
    private double edgeThicknessFactor;
    private double spaceFactor;
    
    //+++TEST+++
    private boolean localAlignment = false;
    //+++TEST+++

    //private HashMap<Chromosome, List<Path>> chromosomeLineAssociation;
    
    public DrawingController(DrawingParameterMessage paramter, Configuration configuration) {
    	//System.out.println(paramter.getMinDrawLengthVertex() + " VS " + configuration.getNeededDummyVertexWidth());
    	this.minDrawLengthVertex = Math.max(paramter.getMinDrawLengthVertex(), configuration.getNeededDummyVertexWidth());
    	this.maxDrawLengthVertex = paramter.getMaxDrawLengthVertex();
    	this.minDrawHeightVertex = paramter.getMinDrawHeightVertex();
    	this.minHorizontalInterSpace = paramter.getMinHorizontalInterSpace();
    	this.minTriangleHeight = paramter.getMinTriangleHeight();
    	this.triangleLength = paramter.getTriangleLength();
    	
    	this.edgeThicknessFactor = configuration.getDrawingThicknessFactor();
    	this.spaceFactor = configuration.getSpaceFactor();
    }
    
    //+++TEST+++
    public void setLocalAlignmentTrue() {
    	localAlignment = true;
    }
    //+++TEST+++
    
    public AfterDrawingGraphData getAfterDrawingGraphData() {
		return afterDrawingGraphData;
	}

	public List<VertexMessage> getVertexMessages() {
		return vertexMessages;
	}
    
	
	
    public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public List<EdgeMessage> getEdgeMessages() {
		return edgeMessages;
	}
    
    public List<ArrowMessage> getArrowMessages() {
		return arrowMessages;
	}
        
    public void drawSugiyamaFramework(
        GraphProjectionSugiyama sugiyamaProjection
    ) {
        long timeStart = System.currentTimeMillis();
        
        afterDrawingGraphData = new AfterDrawingGraphData();

        Map<Integer, Layer> indexLayerAssociation = sugiyamaProjection.getIndexLayerAssociation();
        vertexMessages = new ArrayList<>();
        edgeMessages = new ArrayList<EdgeMessage>();
        arrowMessages = new ArrayList<ArrowMessage>();
        
        //gmlVertexMessages = new ArrayList<>();
        //chromosomeLineAssociation = new HashMap<>();
        
        drawVertices(sugiyamaProjection, indexLayerAssociation);
        drawEdges(indexLayerAssociation);

        //Eades-Drawing GML Creation
        //createEadesGML(sugiyamaProjection, indexLayerAssociation);
        
        long timeEnd = System.currentTimeMillis();
        System.out.println("Time - Final Drawing: " + (timeEnd - timeStart));
        //Test!!!
//        sugiyamaProjection.getCSVLine().append((timeEnd - timeStart) + ";");
        //Test!!!
    }

//    private void createEadesGML(
//    		GraphProjectionSugiyama sugiyamaProjection,
//            Map<Integer, Layer> indexLayerAssociation
//    ) {
//    	String gml = "graph [\n"
//    			+ "	directed 1\n"
//    			+ "	label \"Graph\"\n";
//    	for (VertexMessage v : gmlVertexMessages) {
//    		gml = gml + "node [\n";
//    		gml = gml + "id " + v.getID() + "\n"; 
//    		gml = gml + "graphics [\n";
//    		gml = gml + "x " + v.getX() + "\n"; 
//    		gml = gml + "y " + v.getY() + "\n"; 
//    		gml = gml + "w " + "50" + "\n"; 
//    		gml = gml + "h " + "50" + "\n"; 
//    		gml = gml + "]\n]\n";
//    	}
//    	for (EdgeMessage e : edgeMessages) {
//    		gml = gml + "edge [\n";
//    		gml = gml + "source " + e.getSourceString() + "\n"; 
//    		gml = gml + "target " + e.getTargetString() + "\n"; 
//    		gml = gml + "]\n";
//    	}
//    	gml = gml + "]";
//    	System.out.println(gml);
//    }
    
//    public void reDrawSugiyamaFramework(
//            Set<Vertex> chromosomesToDraw
//    ) {
////        drawPane.getChildren().clear();
//        for (Chromosome chromosome : chromosomeLineAssociation.keySet()) {
//            Color pathColor;
//            if (chromosomesToDraw.contains(chromosome.getAssociatedChromosomeVertex())) {
//                pathColor = chromosome.getColor();
//            } else {
//                pathColor = Configuration.getDefaultColor();
//            }
//
//            for (Path path : chromosomeLineAssociation.get(chromosome)) {
//                path.setStroke(pathColor);
//            }
//        }
//    }

	private void drawVertices(
        GraphProjectionSugiyama sugiyamaProjection,
        Map<Integer, Layer> indexLayerAssociation
    ) {
        diffDrawLengthVertex = maxDrawLengthVertex - minDrawLengthVertex;
        maxSequenceLength = sugiyamaProjection.getLongestAlignmentBlock();
        minSequenceLength = sugiyamaProjection.getShortestAlignmentBlock();
        diffSequenceLength = maxSequenceLength - minSequenceLength;
        double currentX = 100;

        for (int indexLayer = 0; indexLayer < indexLayerAssociation.keySet().size(); indexLayer++) {
            Layer currentLayer = indexLayerAssociation.get(indexLayer);

            double drawHeight = Math.max(minDrawHeightVertex, currentLayer.getNeededVertexHeight());
            double horizontalInterSpace = Math.max(minHorizontalInterSpace, currentLayer.getNeededInterLayerSpace());

            double percent = (currentLayer.getLongestAlignmentBlockInLayer() - minSequenceLength) / diffSequenceLength;
            double longestLength = minDrawLengthVertex + (percent * diffDrawLengthVertex);
            currentX += 0.5 * longestLength;

            currentLayer.setXPosition(currentX);
            currentLayer.setCurrentXMiddelPointPosition(currentX + 0.5 * longestLength);

            for (VertexSugiyama vertex : currentLayer.getVertexOrderList()) {
            	
            	afterDrawingGraphData.addVertex(vertex);
            	
            	double xDrawCoordinate = currentX;
                double yDrawCoordinate = vertex.getAssociatedBlockSet().getDrawingPosition() * (drawHeight + 20) + drawHeight;
                //yDrawCoordinate += 0.5 * drawHeight;
                
                
                double vertexLength;
                //Color vertexColor;
                if (vertex.isDummyNode()) {
                	yDrawCoordinate = vertex.getAssociatedBlockSet().getDrawingPosition() * (drawHeight + 20) + drawHeight;
                    yDrawCoordinate += 0.5 * drawHeight;
                    //gmlVertexMessages.add(new VertexMessage(String.valueOf(vertex.getId()), xDrawCoordinate, yDrawCoordinate, drawHeight, 0.0, vertex.isJoinedVertex(), null, null));
                    
                    postProcessEdgePlacing(vertex, yDrawCoordinate);
                    vertexLength = 0;
                } else if (vertex.isJoinedVertex()) {
                    percent = (vertex.getSequenceLength() - minSequenceLength) / diffSequenceLength;
                    vertexLength = minDrawLengthVertex + (percent * diffDrawLengthVertex);
                    vertexMessages.add(new VertexMessage(String.valueOf(vertex.getId()), xDrawCoordinate, yDrawCoordinate, drawHeight, vertexLength, vertex.isJoinedVertex(), vertex.getClassString(), vertex.getJoinedAdditionalVertexData()));//vertex.getAdditionalVertexData().getMultipleSeqAlignmentData()));
                    //gmlVertexMessages.add(new VertexMessage(String.valueOf(vertex.getId()), xDrawCoordinate, yDrawCoordinate, drawHeight, vertexLength, vertex.isJoinedVertex(), vertex.getClassString(), null));
                    
                    yDrawCoordinate = yDrawCoordinate + 0.5 * drawHeight;
                    postProcessEdgePlacing(vertex, yDrawCoordinate);
                    //vertexColor = Color.rgb(243, 117, 0);
                    //drawVertex(vertex, currentX, vertexLength, drawHeight, vertexColor);
                } else {
                    percent = (vertex.getSequenceLength() - minSequenceLength) / diffSequenceLength;
                    vertexLength = minDrawLengthVertex + (percent * diffDrawLengthVertex);
                    vertexMessages.add(new VertexMessage(String.valueOf(vertex.getId()), xDrawCoordinate, yDrawCoordinate, drawHeight, vertexLength, vertex.isJoinedVertex(), vertex.getClassString(), vertex.getJoinedAdditionalVertexData()));
                    //gmlVertexMessages.add(new VertexMessage(String.valueOf(vertex.getId()), xDrawCoordinate, yDrawCoordinate, drawHeight, vertexLength, vertex.isJoinedVertex(), vertex.getClassString(), vertex.getAdditionalVertexData().getMultipleSeqAlignmentData()));
                    yDrawCoordinate = yDrawCoordinate + 0.5 * drawHeight;
                    postProcessEdgePlacing(vertex, yDrawCoordinate);
                    //vertexColor = Color.rgb(0, 117, 243);
                    //drawVertex(vertex, currentX, vertexLength, drawHeight, vertexColor);
                }
                
                vertex.setDrawingParameters(xDrawCoordinate, yDrawCoordinate, vertexLength, drawHeight);
                
                if (xDrawCoordinate > maxX) {
                	maxX = xDrawCoordinate;
                }
                if (yDrawCoordinate > maxY) {
                	maxY = yDrawCoordinate;
                }
            }

            currentX += (0.5 * longestLength) + horizontalInterSpace;
        }
    }

    private void drawEdges(
        Map<Integer, Layer> indexLayerAssociation
    ) {
        for (int indexLayer = 0; indexLayer < indexLayerAssociation.keySet().size() - 1; indexLayer++) {
            Layer currentLayer = indexLayerAssociation.get(indexLayer);

            for (VertexSugiyama vertex : currentLayer.getVertexOrderList()) {
                for (EdgeSugiyama upEdge : vertex.getOutEdgesClassifiedByVerticalDirection().get(VerticalEdgeDirection.UP)) {
                    drawEdge(upEdge, VerticalEdgeDirection.UP);
                }

                for (EdgeSugiyama straightEdge : vertex.getOutEdgesClassifiedByVerticalDirection().get(VerticalEdgeDirection.STRAIGHT)) {
                    drawEdge(straightEdge, VerticalEdgeDirection.STRAIGHT);
                }

                for (EdgeSugiyama downEdge : vertex.getOutEdgesClassifiedByVerticalDirection().get(VerticalEdgeDirection.DOWN)) {
                    drawEdge(downEdge, VerticalEdgeDirection.DOWN);
                }
            }
        }
    }

    private void postProcessEdgePlacing(VertexSugiyama drawVertex, double yDrawCoordinate) {
      //+++TEST+++
      //if (!localAlignment) {
      //+++TEST+++
    	  
        for (EdgeSugiyama outEdge : drawVertex.getOutEdges()) {
            outEdge.increaseYStart(EdgeDirection.FORWARD, yDrawCoordinate);
            outEdge.increaseYStart(EdgeDirection.BACKWARD, yDrawCoordinate);
        }

        for (EdgeSugiyama inEdge : drawVertex.getInEdges()) {
            inEdge.increaseyEnd(EdgeDirection.FORWARD, yDrawCoordinate);
            inEdge.increaseyEnd(EdgeDirection.BACKWARD, yDrawCoordinate);
        }
        
      //+++TEST+++
      //}
      //+++TEST+++

    }

//    private void finalePlacementDummyVertex(
//            VertexSugiyama vertex,
//            double height
//    ) {
//        double yDrawCoordinate = vertex.getAssociatedBlockSet().getDrawingPosition() * (height + 20) + height;
//        yDrawCoordinate += 0.5 * height;
//
//        postProcessEdgePlacing(vertex, yDrawCoordinate);
//    }

//    private void drawVertex(
//        VertexSugiyama vertex,
//        double currentX,
//        double length,
//        double height,
//        Color color
//    ) {
//        double xDrawCoordinate = currentX;
//        double yDrawCoordinate = vertex.getAssociatedBlockSet().getDrawingPosition() * (height + 20) + height;
//        yDrawCoordinate += 0.5 * height;
//
//        postProcessEdgePlacing(vertex, yDrawCoordinate, height);
//
//        Group vertexTextGroup = new Group();
//
//        Rectangle rectangle = new Rectangle(Math.round(xDrawCoordinate),
//                                            Math.round(yDrawCoordinate),
//                                            length, height, color,
//                                            vertex);
//        vertexTextGroup.getChildren().add(rectangle);
//
//        Text text = new Text(0, 0, String.valueOf(vertex.getText()));
//        text.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
//        text.setFill(Color.WHITE);
//        length = text.getBoundsInLocal().getWidth();
//        height = text.getBoundsInLocal().getHeight();
//        text.relocate(Math.round(xDrawCoordinate - (0.5 * length)), Math.round(yDrawCoordinate - (0.5 * height)));
//        vertexTextGroup.getChildren().add(text);
//
//        vertexTextGroup.setCursor(Cursor.HAND);
//
//        drawGroup.getChildren().add(vertexTextGroup);
//    }

    private String createTriangle(double posX, double posY, double height, EdgeDirection direction) {
    	
        String pointList = posX + "," + posY + " ";
        if (direction.equals(EdgeDirection.FORWARD)) {
	        pointList += (posX - triangleLength) + "," + (posY - (0.5 * height)) + " ";
	        pointList += (posX - triangleLength) + "," + (posY + (0.5 * height)) + " ";
        } else {
        	pointList += (posX + triangleLength) + "," + (posY - (0.5 * height)) + " ";
	        pointList += (posX + triangleLength) + "," + (posY + (0.5 * height)) + " ";
        }
        pointList += posX + "," + posY;
        return pointList;
    }

    private void drawEdge(
        EdgeSugiyama edge,
        VerticalEdgeDirection verticalDirection
    ) {
        List<EdgeDirection> directionList = new ArrayList<>();
        if (verticalDirection == VerticalEdgeDirection.DOWN) {
            directionList.add(EdgeDirection.BACKWARD);
            directionList.add(EdgeDirection.FORWARD);
        } else {
            directionList.add(EdgeDirection.FORWARD);
            directionList.add(EdgeDirection.BACKWARD);
        }

//        //initialize Arrows
//        Map<EdgeDirection, Arrow> drawedArrows = new HashMap<>();
//        if (drawedArrows.isEmpty()) {
//            for (EdgeDirection direction : directionList) {
//                Arrow arrow = new Arrow();
//
//                for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(direction)) {
//                    Path curve = new Path();
//                    arrow.getCurvePaths().add(curve);
//                }
//                if (arrow.getCurvePaths().size() > 0) {
//                    drawedArrows.put(direction, arrow);
////                    System.out.println(edge.getOutNode().getId() + " -->" + edge.getInNode().getId() + "|" + direction + "|" + drawedArrows.get(direction).getCurvePaths().size());
//                }
//            }
//        }

        int edgeCount = 0;
        
        for (EdgeDirection direction : directionList) {
//            Arrow drawedArrow = drawedArrows.get(direction);
            int curveIndex = 0;

            if (!edge.getN4JAssociatedChromosomes().isEmpty(direction)) {

                VertexSugiyama vertexLeft = edge.getOutNode();
                VertexSugiyama vertexRight = edge.getInNode();

                double edgeThickness = edgeThicknessFactor * edge.getN4JAssociatedChromosomes().size(direction);
                double halfEdgeThickness = edgeThickness * 0.5;

                double yLeftConnectionPoint = edge.getyStart(direction);
                double yRightConnectionPoint = edge.getyEnd(direction);

                double xLeftConnectionPoint;
                if (vertexLeft.isDummyNode()) {
                    xLeftConnectionPoint = vertexLeft.getAssociatedLayer().getXPosition();
                } else {
                    xLeftConnectionPoint = vertexLeft.getX() + vertexLeft.getWidth(); //+ 0.5 * edge.getDrawingThicknesFactor();
                }

                double xRightConnectionPoint;
                if (vertexRight.isDummyNode()) {
                    xRightConnectionPoint = vertexRight.getAssociatedLayer().getXPosition();
                } else {
                    xRightConnectionPoint = vertexRight.getX(); //- 0.5 * edge.getDrawingThicknesFactor();
                }

                double xEdgeMiddelPoint;
                if (verticalDirection == VerticalEdgeDirection.UP) {
                    xEdgeMiddelPoint = edge.getOutNode().getAssociatedLayer().getCurrentXMiddelPointPosition() + edge.getxMiddelPoint(direction);
                } else {
                    xEdgeMiddelPoint = edge.getOutNode().getAssociatedLayer().getCurrentXMiddelPointPosition() + edge.getxMiddelPoint(direction) + edgeThickness;
                }

                for (N4JChromosome chromosomePath : edge.getN4JAssociatedChromosomes().getChromosomePaths(direction)) {

                    yLeftConnectionPoint += 0.5 * edgeThicknessFactor;
                    yRightConnectionPoint += 0.5 * edgeThicknessFactor;
                    if (verticalDirection.equals(VerticalEdgeDirection.UP)) {
                        xEdgeMiddelPoint += 0.5 * edgeThicknessFactor;
                   } else {
                       xEdgeMiddelPoint = xEdgeMiddelPoint - 0.5 * edgeThicknessFactor;
                    }

                    VertexSugiyama connectionVertex;
                    if (curveIndex == 0) {
                        double xConnectionPoint;
                        double yConnectionPoint;
                        if (direction.equals(EdgeDirection.FORWARD)) {
                            connectionVertex = vertexRight;
                        } else {
                            connectionVertex = vertexLeft;
                        }
                        if (!connectionVertex.isDummyNode()) {
                            if (direction.equals(EdgeDirection.FORWARD)) {
                                xRightConnectionPoint -= triangleLength;

                                xConnectionPoint = xRightConnectionPoint + /*0.5 * edge.getDrawingThicknesFactor() +*/ triangleLength;
                                yConnectionPoint = yRightConnectionPoint - 0.5 * edgeThicknessFactor + halfEdgeThickness;
                            } else {
                                xLeftConnectionPoint += triangleLength;

                                xConnectionPoint = xLeftConnectionPoint - /*0.5 * edge.getDrawingThicknesFactor() -*/ triangleLength;
                                yConnectionPoint = yLeftConnectionPoint - 0.5 * edgeThicknessFactor + halfEdgeThickness;
                            }

                            //Triangle for the arrow
//                            drawedArrow.getTriangle().getTransforms().clear();
//                            drawedArrow.getTriangle().getPoints().clear();

                            double arrowHeight = Math.max(minTriangleHeight, edgeThickness);

                            arrowMessages.add(new ArrowMessage(createTriangle(xConnectionPoint, yConnectionPoint, arrowHeight, direction), vertexLeft.getId(), vertexRight.getId(), edge.getN4JAssociatedChromosomes().getClassString(direction)));
                            
//                            drawedArrow.getTriangle().getPoints().addAll(createTriangle(xConnectionPoint, yConnectionPoint, arrowHeight, direction));
//                            drawedArrow.getTriangle().setStroke(Color.BLACK);
//                            drawedArrow.getTriangle().setFill(Color.BLACK);
//
//                            //rotate triangle around the focus point of the triangle --> pivot-point
//                            if (direction.equals(EdgeDirection.BACKWARD)) {
//                                Rotate rotation = new Rotate();
//                                javafx.beans.property.Property xConnectionPointProperty = new SimpleDoubleProperty(xConnectionPoint);
//                                Property yConnectionPointProperty = new SimpleDoubleProperty(yConnectionPoint);
//                                rotation.pivotXProperty().bind(xConnectionPointProperty);
//                                rotation.pivotYProperty().bind(yConnectionPointProperty);
//                                drawedArrow.getTriangle().getTransforms().addAll(rotation);
//                                rotation.setAngle(180);
//                            }
                        }
                        
                        if (vertexRight.isDummyNode() && edge.isShiftedInVertexEndPoint()) {
                        	if (direction.equals(EdgeDirection.BACKWARD) && !edge.getN4JAssociatedChromosomes().isEmpty(EdgeDirection.FORWARD)) {
                        		double forwardEdgeThickness = edgeThicknessFactor * edge.getN4JAssociatedChromosomes().size(EdgeDirection.FORWARD);
                    			xRightConnectionPoint = xRightConnectionPoint + forwardEdgeThickness + edgeThicknessFactor * spaceFactor;
                        	}
                        	//xRightConnectionPoint = xRightConnectionPoint + curveIndex * edgeThicknessFactor;
                        		
                        	xRightConnectionPoint = xRightConnectionPoint - edge.getDrawingThicknes(spaceFactor) + edgeThicknessFactor;
                        }
                        
                        if (vertexLeft.isDummyNode() && vertexLeft.getInEdges().get(0).isShiftedInVertexEndPoint()) {
                    		if (direction.equals(EdgeDirection.BACKWARD) && !edge.getN4JAssociatedChromosomes().isEmpty(EdgeDirection.FORWARD)) {
                    			double forwardEdgeThickness = edgeThicknessFactor * edge.getN4JAssociatedChromosomes().size(EdgeDirection.FORWARD);
                    			xLeftConnectionPoint = xLeftConnectionPoint + forwardEdgeThickness + edgeThicknessFactor * spaceFactor;
                    		}
                    		
                    		xLeftConnectionPoint = xLeftConnectionPoint - edge.getDrawingThicknes(spaceFactor) + edgeThicknessFactor;
                        }
                    }
                    
                    String points;
                    //start
                    points = xLeftConnectionPoint + "," + yLeftConnectionPoint + " ";
                    if (!verticalDirection.equals(VerticalEdgeDirection.STRAIGHT)) {
                    	points +=xEdgeMiddelPoint + "," + yLeftConnectionPoint + " ";
                        points +=xEdgeMiddelPoint + "," + yRightConnectionPoint + " ";
                        
                        
                        //BEZIER CURVE
//                        double xCornerOffset, yCornerOffset;
//                        if (verticalDirection.equals(VerticalEdgeDirection.UP)) {
//                        	xCornerOffset = triangleLength;
//                        	yCornerOffset = triangleLength;
//                        } else {
//                        	xCornerOffset = triangleLength;
//                        	yCornerOffset = - triangleLength;
//                        }
//                    	double xBezierLeft = xEdgeMiddelPoint - xCornerOffset;
//                    	double yBezierLeft = yLeftConnectionPoint - yCornerOffset;
//                    	double xBezierRight = xEdgeMiddelPoint + xCornerOffset;
//                    	double yBezierRight = yRightConnectionPoint + yCornerOffset;
//                    	points += "L " +xBezierLeft + "," + yLeftConnectionPoint + " ";
//                    	points += "Q " +xEdgeMiddelPoint + "," + yLeftConnectionPoint + " ";
//                    	points += +xEdgeMiddelPoint + "," + yBezierLeft + " ";
//                    	points += "L " +xEdgeMiddelPoint + "," + yBezierRight + " ";
//                        points += "Q " +xEdgeMiddelPoint + "," + yRightConnectionPoint + " ";
//                        points += +xBezierRight + "," + yRightConnectionPoint+ " ";
                    }
                    //end
                    points +=xRightConnectionPoint + "," + yRightConnectionPoint;
                    
                    if (vertexRight.isDummyNode() && edge.isShiftedInVertexEndPoint()) {
                        EdgeSugiyama nextEdge = vertexRight.getOutEdges().get(0);
                    	points += " " + xRightConnectionPoint + "," + (nextEdge.getyStart(direction) + edgeThicknessFactor * curveIndex /*+ 0.5 * nextEdge.getDrawingThicknesFactor()*/);
                    	xRightConnectionPoint = xRightConnectionPoint + edgeThicknessFactor;
                   	}
                    
                    if (vertexLeft.isDummyNode() && vertexLeft.getInEdges().get(0).isShiftedInVertexEndPoint()) {
                    	xLeftConnectionPoint = xLeftConnectionPoint + edgeThicknessFactor;
                    }
                                        
                    edgeMessages.add(new EdgeMessage(points, vertexLeft.getId(), vertexRight.getId(), "s" + chromosomePath.getSubStructureName() + "g" + chromosomePath.getGenomeName(), chromosomePath.getColorInHex(), edgeThicknessFactor));
                    
//                    Path curve = drawedArrow.getCurvePaths().get(curveIndex);
//                    curve.getElements().clear();
//                    MoveTo start = new MoveTo();
//                    start.setX(xLeftConnectionPoint);
//                    start.setY(yLeftConnectionPoint);
//
//                    LineTo lineTo3 = new LineTo();
//                    lineTo3.setX(xRightConnectionPoint);
//                    lineTo3.setY(yRightConnectionPoint);
//
//                    if (!verticalDirection.equals(VerticalEdgeDirection.STRAIGHT)) {
//                        LineTo lineTo1 = new LineTo();
//                        LineTo lineTo2 = new LineTo();
//                        lineTo1.setX(xEdgeMiddelPoint);
//                        lineTo1.setY(yLeftConnectionPoint);
//                        lineTo2.setX(xEdgeMiddelPoint);
//                        lineTo2.setY(yRightConnectionPoint);
//                        curve.getElements().add(start);
//                        curve.getElements().add(lineTo1);
//                        curve.getElements().add(lineTo2);
//                        curve.getElements().add(lineTo3);
//                    } else {
//                        curve.getElements().add(start);
//                        curve.getElements().add(lineTo3);
//                    }

//                    //Special additional connection if Dummy-Shift was necessary
//                    if (vertexRight.isDummyNode() && edge.isShiftedInVertexEndPoint()) {
//                        EdgeSugiyama nextEdge = vertexRight.getOutEdges().get(0);
//                        LineTo lineTo4 = new LineTo();
//                        lineTo4.setX(xRightConnectionPoint);
//                        lineTo4.setY(nextEdge.getyStart(direction) + 0.5 * nextEdge.getDrawingThicknesFactor());
//                        curve.getElements().add(lineTo4);
//                    }

//                    curve.setStrokeWidth(edge.getDrawingThicknesFactor());
//                    curve.setStroke(Configuration.getDefaultColor());
//
//                    if (!chromosomeLineAssociation.containsKey(chromosomePath)) {
//                        chromosomeLineAssociation.put(chromosomePath, new ArrayList<>());
//                    }
//                    chromosomeLineAssociation.get(chromosomePath).add(curve);

                    //prepare for next step
                    curveIndex++;
                    edgeCount++;

                    yLeftConnectionPoint += 0.5 *  edgeThicknessFactor;
                    yRightConnectionPoint += 0.5 *  edgeThicknessFactor;
                    if (verticalDirection.equals(VerticalEdgeDirection.UP) || verticalDirection.equals(VerticalEdgeDirection.STRAIGHT)) {
                        xEdgeMiddelPoint += 0.5 *  edgeThicknessFactor;
                    } else {
                        xEdgeMiddelPoint = xEdgeMiddelPoint - 0.5 * edgeThicknessFactor;
                    }
//                    }

                }
//                edge.getOutNode().getAssociatedLayer().setCurrentXMiddelPointPosition(edge.getOutNode().getAssociatedLayer().getCurrentXMiddelPointPosition() + edgeThickness + sugiyamaProjection.getSpaceFactor() * edge.getDrawingThicknesFactor());

//                shift += 7;
            }
        }

//        for (EdgeDirection direction : drawedArrows.keySet()) {
//            Arrow arrow = drawedArrows.get(direction);
//            for (javafx.scene.shape.Path curve : arrow.getCurvePaths()) {
//                drawGroup.getChildren().add(curve);
//            }
//            if (!arrow.getTriangle().getPoints().isEmpty()) {
//                drawGroup.getChildren().add(arrow.getTriangle());
//            }
//        }
    }
}
