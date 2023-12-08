/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Algorithms;

import main.Data.*;

import java.util.*;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;

//Test!!!
import java.io.FileWriter;
//Test!!!

/**
 *
 * @author zeckzer
 */
public class EdgeAndBlockSetInit {

    /// Input
    private List<Chromosome> sequenceVerticesDataForSugiyama;
    private double drawingThicknessFactor;
    private List<VertexSugiyama> vertexList;

    /// Local
    private Map<VertexSugiyama, Map<VertexSugiyama, EdgeSugiyama>> edgeExistensCheck = new HashMap<>();
    private Map<VertexSugiyama, Set<VertexSugiyama>> verticesAfterThisVertex;
    private Map<VertexSugiyama, Set<VertexSugiyama>> verticesBeforeThisVertex;

    /// Test
    private int edgeCountForwardDAG = 0;
    private int edgeCountBackwardDAG = 0;
    private int edgeCountAllDAG = 0;
    private int edgeCountForwardExclusiveDAG = 0;
    private int edgeCountBackwardExclusiveDAG = 0;
    private int getEdgeCountAllExclusiveDAG = 0;
    private int edgeCountAll = 0;
    private int byPathCount = 0;

    /// Output
    private List<EdgeSugiyama> edgeList = new LinkedList<>();
    private BlockSet guideSequenceBlockSet;

    public EdgeAndBlockSetInit(
        List<Chromosome> sequenceVerticesDataForSugiyama,
        double drawingThicknessFactor
    ) {
        this.sequenceVerticesDataForSugiyama = sequenceVerticesDataForSugiyama;
        this.drawingThicknessFactor = drawingThicknessFactor;
    }

    //Create Edges and BlockSets --> on the fly destroy cycles
    //Part of preparation step
    public List<EdgeSugiyama> cycleDestroyerAndInitializeEdgesAndBlockSets(/*###START### - FOR BROKEN DATA*/ List<VertexSugiyama> vertexList/*###END### - FOR BROKEN DATA*/) {
    	this.vertexList = vertexList;
        //first Chromosome in sequenceVerticesDataForSugiyama is always the Guide Sequence
        Set<VertexSugiyama> alreadySeenVertices = new HashSet<>();
        Set<VertexSugiyama> oldAlreadySeenVertices = new HashSet<>();
        
        verticesAfterThisVertex = new HashMap<>(vertexList.size());
        verticesBeforeThisVertex = new HashMap<>(vertexList.size());

        Chromosome guideSequenceChr = sequenceVerticesDataForSugiyama.get(0);
        List<VertexSugiyama> guideSequencePath = guideSequenceChr.getSugiyamaVertices();
        
        //System.out.println(guideSequenceChr.toString2());
                        
        //Special case --> Guide Sequence Path
        VertexSugiyama currentVertex;
        VertexSugiyama nextVertex = null;
        List<EdgeSugiyama> byPathEdges = new ArrayList<>();
        for (int vertexIndex = 0; vertexIndex < guideSequencePath.size() - 1; vertexIndex++) {
            currentVertex = guideSequencePath.get(vertexIndex);
            nextVertex = guideSequencePath.get(vertexIndex + 1);
            alreadySeenVertices.add(currentVertex);
            oldAlreadySeenVertices.add(currentVertex);
            
            if (alreadySeenVertices.contains(nextVertex)) {
            	System.out.println("Die Kante von " + currentVertex.getText() + " zu " + nextVertex.getText() + " schließt einen Zyklus! Scheiße!!!");
            	
            }
            
            alreadySeenVertices.add(nextVertex);
            oldAlreadySeenVertices.add(nextVertex);
            //edge creation
            edgeCreation(currentVertex, nextVertex, EdgeDirection.FORWARD, guideSequenceChr, byPathEdges);
        }

        //Test!!!
        byPathCount++;
        //Test!!!

        //create Guide Sequence BlockSet
        guideSequenceBlockSet = new BlockSet(guideSequencePath, byPathEdges);
        /*String text = "";
        for (VertexSugiyama v : guideSequencePath) {
        	text += v.getVertexText() + ", ";
        }
        System.out.println(text);*/

        //all other Chromosome Paths
        List<Chromosome> otherSequenceVerticesDataForSugiyama = sequenceVerticesDataForSugiyama.subList(1, sequenceVerticesDataForSugiyama.size());
        for (Chromosome currentChromosomePath : otherSequenceVerticesDataForSugiyama) {
        	//System.out.println(currentChromosomePath.toString2());

            List<VertexSugiyama> path = currentChromosomePath.getSugiyamaVertices();
            
            System.out.println(currentChromosomePath.getComposedName());
            /*text = "";
            for (VertexSugiyama v : path) {
            	text += v.getVertexText() + ", ";
            }
            System.out.println(text);*/

            //Split original Path in shorter Paths (byPaths) between already seen Vertices
            List<List<VertexSugiyama>> byPaths = new ArrayList<>();
            for (int vertexIndex = 0; vertexIndex <= path.size() - 1; vertexIndex++) {
            	
            	currentVertex = path.get(vertexIndex);
            	//System.out.println(path.get(vertexIndex).getText());
                
                if (!alreadySeenVertices.contains(currentVertex)) {
                    alreadySeenVertices.add(currentVertex);
                    if (byPaths.size() > 0) {
                        byPaths.get(byPaths.size() - 1).add(currentVertex);
                        //System.out.println("New + >0: " + currentVertex.getText());
                    }
                } else {
                    if (vertexIndex > 0) {
                        byPaths.get(byPaths.size() - 1).add(currentVertex);
                        //System.out.println("Old + >0: " + currentVertex.getText());
                    }
                    if (vertexIndex < path.size() - 1) {
                        List<VertexSugiyama> newByPath = new ArrayList<>();
                        newByPath.add(currentVertex);
                        byPaths.add(newByPath);
                        //System.out.println("Old + <end + newBP: " + currentVertex.getText());
                    }
                }
            }

            //Test!!!
            byPathCount += byPaths.size();
            //Test!!!

            //###START### - FOR BROKEN DATA
            List<List<VertexSugiyama>> newByPaths = new ArrayList<>();
            for (List<VertexSugiyama> byPath : byPaths) {
            	if (!oldAlreadySeenVertices.contains(byPath.get(0)) && !oldAlreadySeenVertices.contains(byPath.get(byPath.size() - 1))) {
            		/*String text = "FIRST & LAST VERTEX OF BYPATH IS NOT PART OF GS!!! --> PROBLEM: ";
                    for (VertexSugiyama v : byPath) {
                        text += v.getId() + ", ";
                    }
                    System.out.println(text);*/
            		vertexList.removeAll(byPath.subList(1, byPath.size() - 1));
            	} else if (!oldAlreadySeenVertices.contains(byPath.get(0))) {
            		/*String text = "FIRST VERTEX OF BYPATH IS NOT PART OF GS!!! --> PROBLEM: ";
                    for (VertexSugiyama v : byPath) {
                        text += v.getId() + ", ";
                    }
                    System.out.println(text);*/
            		vertexList.removeAll(byPath.subList(0, byPath.size() - 1));
            	} else if (!oldAlreadySeenVertices.contains(byPath.get(byPath.size() - 1))) {
            		/*String text = "LAST VERTEX OF BYPATH IS NOT PART OF GS!!! --> PROBLEM: ";
                    for (VertexSugiyama v : byPath) {
                        text += v.getId() + ", ";
                    }
                    System.out.println(text);*/
            		vertexList.removeAll(byPath.subList(1, byPath.size()));
            	} else {
            		for (VertexSugiyama v : byPath) {
                		oldAlreadySeenVertices.add(v);
                	}
            		newByPaths.add(byPath);
            	}
            }
            
            byPaths = newByPaths;
            
            /*for (List<VertexSugiyama> byPath : byPaths.subList(0, byPaths.size() - 1)) {
            	for (VertexSugiyama v : byPath) {
            		oldAlreadySeenVertices.add(v);
            	}
            }
            List<VertexSugiyama> lastByPath = byPaths.get(byPaths.size() - 1);
            
            if (!oldAlreadySeenVertices.contains(lastByPath.get(lastByPath.size() - 1))) {
            	System.out.println("LAST VERTEX OF LAST BYPATH IS NOT PART OF GS!!! --> PROBLEM");
            	byPaths = byPaths.subList(0, byPaths.size() - 1);
            	vertexList.removeAll(lastByPath.subList(1, lastByPath.size()));
            } else {
            	for (VertexSugiyama v : lastByPath) {
            		oldAlreadySeenVertices.add(v);
            	}
            }*/
            //###END### - FOR BROKEN DATA
            
            //process every byPath in the current Chromosome Path
            for (List<VertexSugiyama> byPath : byPaths) {
                byPathEdges = new ArrayList<>();

                    /*String text = "byPath: ";
                    for (VertexSugiyama v : byPath) {
                        text += v.getId() + ", ";
                    }
                    System.out.println(text);*/
                //process each Vertex in the current byPath
                EdgeDirection direction = EdgeDirection.FORWARD;
                for (int vertexIndex = 0; vertexIndex < byPath.size() - 1; vertexIndex++) {
                    VertexSugiyama startVertex = byPath.get(vertexIndex);
                    VertexSugiyama endVertex = byPath.get(vertexIndex + 1);
                    VertexSugiyama firstVertexByPath = byPath.get(0);
                    VertexSugiyama lastVertexByPath = byPath.get(byPath.size() - 1);

                    //if the current byPath would close a cycle --> the byPath (each edge) is turned arround to create a acyclic Graph
                    if (!verticesAfterThisVertex.containsKey(lastVertexByPath) || !verticesAfterThisVertex.get(lastVertexByPath).contains(firstVertexByPath)) {
                        direction = EdgeDirection.FORWARD;
                        
                        /*String text = "FORWARD ";
                        for (VertexSugiyama v : byPath) {
                        	text = text + v.getId() + ", ";
                        }
                        System.out.println(text);*/
                        
                        //edge creation
                        edgeCreation(startVertex, endVertex, direction, currentChromosomePath, byPathEdges);
                    } //byPath has not to be turned arround
                    else {
                        direction = EdgeDirection.BACKWARD;
                        
                        /*String text = "BACKWARD ";
                        for (VertexSugiyama v : byPath) {
                        	text = text + v.getId() + ", ";
                        }
                        System.out.println(text);*/
                        
                        //edge creation
                        edgeCreation(startVertex, endVertex, direction, currentChromosomePath, byPathEdges);
                    }
                }

                //create BlockSet
                if (byPathEdges.size() > 0) {
                    BlockSet blockSet;
                    if (direction.equals(EdgeDirection.FORWARD)) {
                        List<VertexSugiyama> reverseSubByPath = new ArrayList<>(byPath.subList(1, byPath.size() - 1));
                        Collections.reverse(reverseSubByPath);
                        blockSet = new BlockSet(reverseSubByPath, byPathEdges, byPath.get(0), byPath.get(byPath.size() - 1));
                    } else {
                        blockSet = new BlockSet(byPath.subList(1, byPath.size() - 1), byPathEdges, byPath.get(0), byPath.get(byPath.size() - 1));
                    }
                }
            }
        }

        //Test!!!
        printEdgeCount();
        //Test!!!
        
        return this.edgeList;
    }
    
    //Create Edges
    //Part of preparation step
    private void edgeCreation(
        VertexSugiyama startVertex,
        VertexSugiyama endVertex,
        EdgeDirection direction,
        Chromosome chromosomePath,
        List<EdgeSugiyama> byPathEdges
    ) {

        //Test!!!
        edgeCountAll++;
        //Test!!!

        // default is not turned

//        if (startVertex.getId().equals(148) || endVertex.getId().equals(148)) {
//            System.out.println(startVertex.getId() + "-->" + endVertex.getId() + " | " + direction);
//        }
        VertexSugiyama startVertexCheck = startVertex;
        VertexSugiyama endVertexCheck = endVertex;

        if (endVertex.getId() < startVertex.getId()) {
            startVertexCheck = endVertex;
            endVertexCheck = startVertex;
        }

//        System.out.println(startVertex.getId() + " - " +  endVertex.getId());
        //check if the edge (also backwards) already exists
        if (edgeExistensCheck.containsKey(startVertexCheck) && edgeExistensCheck.get(startVertexCheck).containsKey(endVertexCheck)) {
            //System.out.println("existiert schon " + startVertexCheck.getId() + "-->" + endVertexCheck.getId());
            EdgeSugiyama edge = edgeExistensCheck.get(startVertexCheck).get(endVertexCheck);
            edge.addChromosomePath(chromosomePath, direction);

            //Test!!!
            if (direction.equals(EdgeDirection.BACKWARD)) {
                edgeCountBackwardExclusiveDAG++;
            } else {
                edgeCountForwardExclusiveDAG++;
            }
            getEdgeCountAllExclusiveDAG++;
            //Test!!!

        } //edge is new --> creation
        else {
            //System.out.println("neu " + startVertexCheck.getId() + "-->" + endVertexCheck.getId());
            EdgeSugiyama edge;
            if (direction.equals(EdgeDirection.BACKWARD)) {
                //edge is turned
                edge = new EdgeSugiyama(endVertex, startVertex, drawingThicknessFactor);
                edge.setDirection(EdgeDirection.BACKWARD);
                edge.createChromosomePaths(chromosomePath);
                byPathEdges.add(edge);

                //Test!!!
                edgeCountBackwardDAG++;
                //Test!!!

                updateTransitiveShell(endVertex, startVertex);
            } else {
                //edge is not turned
                edge = new EdgeSugiyama(startVertex, endVertex, drawingThicknessFactor);
                edge.createChromosomePaths(chromosomePath);
                byPathEdges.add(0, edge);

                //Test!!!
                edgeCountForwardDAG++;
                //Test!!!

                updateTransitiveShell(startVertex, endVertex);
            }

            //Test!!!
            edgeCountAllDAG++;
            //Test!!!

            edgeList.add(edge);
            Map<VertexSugiyama, EdgeSugiyama> inVertices;
            if (!edgeExistensCheck.containsKey(startVertexCheck)) {
                inVertices = new HashMap<>();
            } else {
                inVertices = edgeExistensCheck.get(startVertexCheck);
            }
            inVertices.put(endVertexCheck, edge);
            edgeExistensCheck.put(startVertexCheck, inVertices);
        }
    }

    //Update the transitive Shell --> which vertices are after/before other vertices --> this is important for the cycle removing
    //Part of preparation step
    private void updateTransitiveShell(
        VertexSugiyama currentVertex,
        VertexSugiyama nextVertex
    ) {
        Set<VertexSugiyama> afterVertices;
        Set<VertexSugiyama> beforeVertices;

        //create keys if necessary
        if (!verticesAfterThisVertex.containsKey(currentVertex)) {
            verticesAfterThisVertex.put(currentVertex, new HashSet<>(vertexList.size()));
        }
        if (!verticesAfterThisVertex.containsKey(nextVertex)) {
            verticesAfterThisVertex.put(nextVertex, new HashSet<>(vertexList.size()));
        }
        if (!verticesBeforeThisVertex.containsKey(currentVertex)) {
            verticesBeforeThisVertex.put(currentVertex, new HashSet<>(vertexList.size()));
        }
        if (!verticesBeforeThisVertex.containsKey(nextVertex)) {
            verticesBeforeThisVertex.put(nextVertex, new HashSet<>(vertexList.size()));
        }

        //Both initialize
        afterVertices = verticesAfterThisVertex.get(currentVertex);
        afterVertices.add(nextVertex);
        afterVertices.addAll(verticesAfterThisVertex.get(nextVertex));

        beforeVertices = verticesBeforeThisVertex.get(nextVertex);
        beforeVertices.add(currentVertex);
        beforeVertices.addAll(verticesBeforeThisVertex.get(currentVertex));

        //After part
        for (VertexSugiyama vertex : verticesBeforeThisVertex.get(currentVertex)) {
            afterVertices = verticesAfterThisVertex.get(vertex);
            if (!afterVertices.contains(nextVertex)) {
                afterVertices.addAll(verticesAfterThisVertex.get(currentVertex));
            }
        }

        //Before part
        for (VertexSugiyama vertex : verticesAfterThisVertex.get(nextVertex)) {
            beforeVertices = verticesBeforeThisVertex.get(vertex);
            if (!beforeVertices.contains(currentVertex)) {
                beforeVertices.addAll(verticesBeforeThisVertex.get(nextVertex));
            }
        }

    }

    BlockSet getGuideSequenceBlockSet() {
        return guideSequenceBlockSet;
    }

    private void printEdgeCount() {
//        System.out.println("#Edge - DAG - Forward: " + edgeCountForwardDAG);
//        System.out.println("#Edge - DAG - Backward: " + edgeCountBackwardDAG);
//        System.out.println("---------------------------------------------------");
//        System.out.println("#Edge - DAG - ALL: " + edgeCountAllDAG);
//        System.out.println("===================================================");
//        System.out.println();
//        System.out.println("#Edge - Exclusive DAG - Forward: " + edgeCountForwardExclusiveDAG);
//        System.out.println("#Edge - Exclusive DAG - Backward: " + edgeCountBackwardExclusiveDAG);
//        System.out.println("---------------------------------------------------");
//        System.out.println("#Edge - Exclusive DAG - ALL: " + getEdgeCountAllExclusiveDAG);
//        System.out.println("===================================================");
//        System.out.println();
//        System.out.println("#Edge - ALL - Forward: " + (edgeCountForwardDAG + edgeCountForwardExclusiveDAG));
//        System.out.println("#Edge - ALL - Backward: " + (edgeCountBackwardDAG + edgeCountBackwardExclusiveDAG));
//        System.out.println("---------------------------------------------------");
        System.out.println("#Edge - ALL: " + edgeCountAll);
        System.out.println("===================================================");
        System.out.println("#ByPath: " + byPathCount);



    }

    //Test!!!
    public StringBuffer csvLine(StringBuffer sb) {
        sb.append(edgeCountForwardDAG + ";" + edgeCountBackwardDAG + ";" + edgeCountAllDAG + ";" + (edgeCountForwardDAG + edgeCountForwardExclusiveDAG) + ";" + (edgeCountBackwardDAG + edgeCountBackwardExclusiveDAG) + ";" + edgeCountAll + ";" + byPathCount + ";");
        return sb;
    }
    //Test!!!
}