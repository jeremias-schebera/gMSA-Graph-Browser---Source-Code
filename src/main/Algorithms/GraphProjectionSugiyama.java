package main.Algorithms;

import main.Algorithms.compactification.Compactification;
import main.Data.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GraphProjectionSugiyama implements Serializable{

    /// Graph
    private List<VertexSugiyama> vertexList;
    private List<EdgeSugiyama> edgeList;
    private BlockSet guideSequenceBlockSet;
    private List<BlockSet> blockSetList;
    private List<Chromosome> sequenceVerticesDataForSugiyama;
    
    private List<N4JChromosome> n4JSequenceVerticesDataForSugiyama;

    /// Layer
    private LayerAssignment layerAssignment;
    private Map<Integer, Layer> indexLayerAssociation;
    private int maxLayer;
    private int longestAlignmentBlock;
    private int shortestAlignmentBlock;
    
    //Test!!!
//    StringBuffer csvText;
    //Test!!!

    /// Drawing
//    private Map<Integer, VertexSugiyama> drawPositionVertexAssociation = new HashMap<>();
//    private int minimumSeparation = 100;
//    private List<List<VertexSugiyama>> paths = new ArrayList<>();
//    private HashMap<Integer, VertexSugiyama> vertexOrderInGuideSequence = new HashMap<>();
//    private int indexSCC;
//    private Stack<VertexSugiyama> stackSCC = new Stack<>();
    private double drawingThicknessFactor;

    /// edge routing
//    private EdgeRoutingOLD edgeRoutingOLD;
    private EdgeRouting edgeRouting;

    public GraphProjectionSugiyama(
        Configuration configuration
    ) {
        this.longestAlignmentBlock = 0;
        this.shortestAlignmentBlock = Integer.MAX_VALUE;
        this.drawingThicknessFactor = configuration.getDrawingThicknessFactor();

    }

    public List<Chromosome> getSequenceVerticesDataForSugiyama() {
    	return sequenceVerticesDataForSugiyama;
    }
    
    public List<N4JChromosome> getN4JSequenceVerticesDataForSugiyama() {
    	return n4JSequenceVerticesDataForSugiyama;
    }
    
    public void computeLayout(
        Configuration configuration
    ) {

        //Test!!!
//        csvText = configuration.getCsvText();
        //Test!!!

        // Preparation Step + Cylce removement
        long timeStart = System.currentTimeMillis();
        //createGraph(configuration);
        n4JCreateGraph(configuration);
        long timeEnd = System.currentTimeMillis();
        System.out.println("Time - Vertex- & Edge Creation + Cycle Removal: " + (timeEnd - timeStart));

        // Layering and Dummy Vertex creation
        timeStart = System.currentTimeMillis();
        assignLayers();
        timeEnd = System.currentTimeMillis();
        System.out.println("Time - Layering: " + (timeEnd - timeStart));

        // Edge Crossing reduction
        timeStart = System.currentTimeMillis();
        reduceEdgeCrossings(configuration);
        timeEnd = System.currentTimeMillis();
        System.out.println("Time - Global-Sifting + Preprocessing: " + (timeEnd - timeStart));
//        projection.yCoordinateAssignmentWithBrandesAndKoepf();

        //Assign BlockSets horizontal Position (Compactification)
        timeStart = System.currentTimeMillis();
        assignVerticalCoordinate();
        timeEnd = System.currentTimeMillis();
        System.out.println("Time - Compactification: " + (timeEnd - timeStart));

        // Pre processing for Edge Routing
        timeStart = System.currentTimeMillis();
        preProcessingEdgeRouting();
        timeEnd = System.currentTimeMillis();
        System.out.println("Time - Postprocessing: " + (timeEnd - timeStart));

        /// Edge routing
        timeStart = System.currentTimeMillis();
        edgeRouting = new EdgeRouting(configuration, indexLayerAssociation, maxLayer);
        edgeRouting.computeRouting();
        timeEnd = System.currentTimeMillis();
        System.out.println("Time - Edge routing: " + (timeEnd - timeStart));

        //Test!!!
        int edgeCount = 0;
        for (EdgeSugiyama e : edgeList) {
            edgeCount += e.getN4JAssociatedChromosomes().getChromosomePaths(EdgeDirection.FORWARD).size();
            edgeCount += e.getN4JAssociatedChromosomes().getChromosomePaths(EdgeDirection.BACKWARD).size();
        }
//        csvText.append(edgeCount + ";" + (timeEnd - timeStart) + ";");
        //Test!!!
    }

    public void computeLocalGraphLayout(Configuration configuration) {
    	/// Edge routing
    	System.out.println(configuration.getDrawingThicknessFactor());
        edgeRouting = new EdgeRouting(configuration, indexLayerAssociation, maxLayer);
        edgeRouting.computeRouting();
    }
    
    private void n4JCreateGraph(
            Configuration configuration
        ) {
            System.out.println("################### START Cycle Removal ###################");
            //long timeStart = System.currentTimeMillis();

            int vertexCountOriginal = 0;
            n4JSequenceVerticesDataForSugiyama = configuration.getN4JSequenceVerticesData().getChromosomes();
            for (N4JChromosome chromosome : n4JSequenceVerticesDataForSugiyama) {
                //System.out.println(chromosome.toString());
                vertexCountOriginal += chromosome.getGraphDBVerticesPath().size();
            }
            System.out.println("Original Number of Vertices from DB (unjoined): " + vertexCountOriginal);

            N4JVertexListInit vertexListInit = new N4JVertexListInit(configuration.getIsJoinEnabled(),
                    configuration.getN4JAlignmentBlockAssociation(),
                    n4JSequenceVerticesDataForSugiyama, configuration.getN4JAdditionalVertexData());
            vertexList = vertexListInit.initializeVerticesAndMarkJoinableVertices();

            /*System.out.println("");
            for (Chromosome chromosome : sequenceVerticesDataForSugiyama) {
                System.out.println(chromosome.toString2());
            }*/

            N4JEdgeAndBlockSetInit edgeAndBlockSetInit = new N4JEdgeAndBlockSetInit(n4JSequenceVerticesDataForSugiyama,
                    drawingThicknessFactor);

            edgeList = edgeAndBlockSetInit.cycleDestroyerAndInitializeEdgesAndBlockSets(/*###START### - FOR BROKEN DATA*/vertexList/*###END### - FOR BROKEN DATA*/);

            //long timeEnd = System.currentTimeMillis();
            //System.out.println("Time - Cycle Removal: " + (timeEnd - timeStart));
            System.out.println("################### END Cycle Removal ###################");

            //Test!!!
//            csvText.append(sequenceVerticesDataForSugiyama.size() + ";");
//            csvText.append(vertexList.size() + ";");
//            csvText = edgeAndBlockSetInit.csvLine(csvText);
//            csvText.append((timeEnd - timeStart) + ";");
//            System.out.println(csvText.toString());
            //Test!!!


            guideSequenceBlockSet = edgeAndBlockSetInit.getGuideSequenceBlockSet();
        }
    
    private void createGraph(
        Configuration configuration
    ) {
        System.out.println("################### START Zyklenentfernung ###################");
        long timeStart = System.currentTimeMillis();

        int vertexCountOriginal = 0;
        sequenceVerticesDataForSugiyama = configuration.getSequenceVerticesData().getChromosomes();
        for (Chromosome chromosome : sequenceVerticesDataForSugiyama) {
            //System.out.println(chromosome.toString());
            vertexCountOriginal += chromosome.getGraphDBVerticesPath().size();
        }
        System.out.println("Original Number of Vertices from DB (unjoined): " + vertexCountOriginal);

        VertexListInit vertexListInit = new VertexListInit(configuration.getIsJoinEnabled(),
                configuration.getAlignmentBlockAssociation(),
                sequenceVerticesDataForSugiyama, configuration.getAdditionalVertexData());
        vertexList = vertexListInit.initializeVerticesAndMarkJoinableVertices();

        /*System.out.println("");
        for (Chromosome chromosome : sequenceVerticesDataForSugiyama) {
            System.out.println(chromosome.toString2());
        }*/

        EdgeAndBlockSetInit edgeAndBlockSetInit = new EdgeAndBlockSetInit(sequenceVerticesDataForSugiyama,
                drawingThicknessFactor);

        edgeList = edgeAndBlockSetInit.cycleDestroyerAndInitializeEdgesAndBlockSets(/*###START### - FOR BROKEN DATA*/vertexList/*###END### - FOR BROKEN DATA*/);

        long timeEnd = System.currentTimeMillis();
        System.out.println("ZEIT - Zyklenentfernung: " + (timeEnd - timeStart));
        System.out.println("################### ENDE Zyklenentfernung ###################");

        //Test!!!
//        csvText.append(sequenceVerticesDataForSugiyama.size() + ";");
//        csvText.append(vertexList.size() + ";");
//        csvText = edgeAndBlockSetInit.csvLine(csvText);
//        csvText.append((timeEnd - timeStart) + ";");
//        System.out.println(csvText.toString());
        //Test!!!


        guideSequenceBlockSet = edgeAndBlockSetInit.getGuideSequenceBlockSet();
    }

    //Test!!!
//    public StringBuffer getCSVLine() {
//        return csvText;
//    }
    //Test!!!

    private void assignLayers() {
        System.out.println("################### START Layering ###################");
        //long timeStart = System.currentTimeMillis();
        
        //System.out.println("init");
        layerAssignment = new LayerAssignment(vertexList, edgeList, drawingThicknessFactor);
        //System.out.println("algo");
        layerAssignment.assignLayerWithLongestPathAndImprovement();

        //long timeEnd = System.currentTimeMillis();

        //System.out.println("ZEIT - Layering: " + (timeEnd - timeStart));
        System.out.println("################### END Layering ###################");

        //Test!!!
//        csvText = layerAssignment.csvLine(csvText);
//        csvText.append(vertexList.size() + ";" + edgeList.size() + ";" + (timeEnd - timeStart) + ";");
        //Test!!!

        indexLayerAssociation = layerAssignment.getIndexLayerAssociation();
        maxLayer = layerAssignment.getMaxLayer();
        longestAlignmentBlock = layerAssignment.getLongestAlignmentBlock();
        shortestAlignmentBlock = layerAssignment.getShortestAlignmentBlock();
    }

    private void reduceEdgeCrossings(Configuration configuration) {
        System.out.println("################### START Crossing Minimization ###################");
        //long timeStart = System.currentTimeMillis();

        GlobalSifting globalSifting = new GlobalSifting(indexLayerAssociation, maxLayer);
        //         projection.reorderVerticesWithBarycenterHeuristic();
        globalSifting.createInitialBlockSetList();
        //        projection.deleteEmptyBlocks();

        blockSetList = globalSifting.globalSifting(10);

        //long timeEnd = System.currentTimeMillis();

        //System.out.println("Time - Crossing Minimization: " + (timeEnd - timeStart));
        System.out.println("################### END Crossing Minimization ###################");

        //Test!!!
//        csvText.append(blockSetList.size() + ";" + (timeEnd - timeStart) + ";");
        //Test!!!
    }

    private void assignVerticalCoordinate() {
        System.out.println("################### START Vertical Coordinate ###################");
        //long timeStart = System.currentTimeMillis();

        Compactification compactification = new Compactification(maxLayer, guideSequenceBlockSet, blockSetList);
        compactification.calculateVerticalDrawingPositions();

        //long timeEnd = System.currentTimeMillis();

        //System.out.println("Time - Vertical Coordinate: " + (timeEnd - timeStart));
        System.out.println("################### END Vertical Coordinate ###################");

        //Test!!!
//        csvText = compactification.csvLine(csvText);
//        csvText.append((timeEnd - timeStart) + ";");
        //Test!!!
    }

    /** Post-Processing
    
     */
    private void preProcessingEdgeRouting() {
        //Preparation for drawing
        //        turnAroundEdgesToOriginal();
        System.out.println("################### START Postprocessing ###################");

        //long timeStart = System.currentTimeMillis();

        turnAroundLayering();

        //Test!!!
        int vertexNumber = vertexList.size();
        vertexNumber = layerAssignment.deleteDummyEdgesAndCreateDrawingEdges(vertexNumber);
        //Test!!!

        //long timeEnd = System.currentTimeMillis();

        //System.out.println("Time - Postprocessing: " + (timeEnd - timeStart));
        System.out.println("################### END Postprocessing ###################");

        //Test!!!
//        csvText = layerAssignment.csvLine2(csvText);
//        csvText.append(edgeList.size() + ";" + vertexNumber + ";" + (timeEnd - timeStart) + ";");
        //Test!!!
    }

    private void turnAroundLayering() {
        int newLayerIndex = maxLayer;
        Map<Integer, Layer> newIndexLayerAssociation = new HashMap<>();
        for (int layerIndex = 0; layerIndex <= maxLayer; layerIndex++) {
            Layer layer = indexLayerAssociation.get(layerIndex);
            layer.setIndex(newLayerIndex);
            newIndexLayerAssociation.put(newLayerIndex, layer);
            newLayerIndex--;
        }
        indexLayerAssociation = newIndexLayerAssociation;
    }

    /* currently unused
    public void updateDrawPositions(Integer insertionPosition, VertexSugiyama vertex) {
        int maxPosition = Collections.max(drawPositionVertexAssociation.keySet());
        if (insertionPosition > maxPosition) {
            drawPositionVertexAssociation.put(maxPosition + 1, vertex);
            vertex.setDrawPosition(maxPosition + 1);
            vertex.setPlaced(true);
        } else {
            for (Integer shiftPosition = maxPosition; shiftPosition >= insertionPosition; shiftPosition--) {
                drawPositionVertexAssociation.put(shiftPosition + 1, drawPositionVertexAssociation.get(shiftPosition));
                drawPositionVertexAssociation.get(shiftPosition + 1).setDrawPosition(shiftPosition + 1);
            }
            drawPositionVertexAssociation.put(insertionPosition, vertex);
            vertex.setDrawPosition(insertionPosition);
            vertex.setPlaced(true);
        }
    }
     */

 /* currently unused
    //Post-Processing --> Preparation for drawing
    public void calculateMaximaleNeededEdgeSpace(int spaceFactor) {
        for (VertexSugiyama vertex : vertexList) {
            if (!vertex.isDummyNode()) {
                double neededEdgeSpace = vertex.calculateMaxiumumNeededEdgeSpace(spaceFactor);
                if (neededEdgeSpace > maximaleHalfNeededEdgeSpaceOnVertex) {
                    maximaleHalfNeededEdgeSpaceOnVertex = neededEdgeSpace;
                }
            }
        }
    }
     */
    public Map<Integer, Layer> getIndexLayerAssociation() {
        return indexLayerAssociation;
    }

    public int getLongestAlignmentBlock() {
        return longestAlignmentBlock;
    }

    public int getShortestAlignmentBlock() {
        return shortestAlignmentBlock;
    }
    
    //+++++ Local Alignment Graph +++++
    
    public void updateGraphForLocalView(HashSet<String> selectedVertexIDs) {
        for (int layerIndex = 0; layerIndex <= maxLayer; layerIndex++) {
            Layer layer = indexLayerAssociation.get(layerIndex);
            List<VertexSugiyama> newVertexOrderList = new ArrayList<VertexSugiyama>();
            for (VertexSugiyama vertex : layer.getVertexOrderList()) {
            	if (selectedVertexIDs.contains(String.valueOf(vertex.getId()))) {
            		newVertexOrderList.add(vertex);
            	}
            }
            layer.setVertexOrderList(newVertexOrderList);
        }
    }
    
    //+++++ Local Alignment Graph +++++
}
