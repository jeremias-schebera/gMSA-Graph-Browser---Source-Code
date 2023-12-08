/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Algorithms;

import main.Data.Chromosome;
import main.Data.N4JAlignmentBlockAssociation;
import main.Data.N4JChromosome;
import main.Data.VertexSugiyama;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.types.Node;
import main.Data.AdditionalVertexData;

/**
 *
 * @author zeckzer
 */
public class N4JVertexListInit {

    /// Input
    private N4JAlignmentBlockAssociation alignmentBlockAssociation;
    private List<N4JChromosome> sequenceVerticesDataForSugiyama;
    private Map<Node, VertexSugiyama> vertexDBSugiyamaVertexAssociation = new HashMap<>();

    /// Local
    private int joinedVertexCount = 1;
    private Boolean isJoinEnabled;
    //JOIN-Test
    private HashMap<VertexSugiyama, VertexSugiyama> existingJoinedVertices = new HashMap<>();
    //JOIN-Test
    private HashMap<Node, AdditionalVertexData> additionalVertexData;

    /// Output
    private List<VertexSugiyama> vertexList;
    //also Output:
        //List of SugiyamaVertex sequences

    N4JVertexListInit(
        Boolean isJoinEnabled,
        N4JAlignmentBlockAssociation alignmentBlockAssociation,
        List<N4JChromosome> sequenceVerticesDataForSugiyama,
        HashMap<Node, AdditionalVertexData> additionalVertexData
    ) {
        this.isJoinEnabled = isJoinEnabled;
        this.alignmentBlockAssociation = alignmentBlockAssociation;
        this.sequenceVerticesDataForSugiyama = sequenceVerticesDataForSugiyama;
        this.additionalVertexData = additionalVertexData;
    }

    /** Create SugiyamaVertices and join trivial SugiyamaVertices together if possible
     * Part of preparation step
     */
    List<VertexSugiyama> initializeVerticesAndMarkJoinableVertices() {
        vertexList = new ArrayList<>();

        //Join trivial SugiyamaVertices-sequences
        if (isJoinEnabled) {
            //Creation and mark joinable
            boolean isGuideSequence = true;
            for (N4JChromosome currentChromosomePath : sequenceVerticesDataForSugiyama) {
                VertexSugiyama currentDrawingVertex;

                //JOIN-Test
                int index = 0;
                //JOIN-Test

                for (Node currentDBVertex : currentChromosomePath.getGraphDBVerticesPath()) {
                    if (!vertexDBSugiyamaVertexAssociation.containsKey(currentDBVertex)) {
                        int length = alignmentBlockAssociation.getLength(currentDBVertex);
                        currentDrawingVertex = new VertexSugiyama(currentDBVertex, isGuideSequence, length, currentChromosomePath, additionalVertexData.get(currentDBVertex));
                        vertexDBSugiyamaVertexAssociation.put(currentDBVertex, currentDrawingVertex);
                        vertexList.add(currentDrawingVertex);

                        //JOIN-Test
                        if (index > 0) {
                            currentDrawingVertex.setIdPredecessorForJoining(currentChromosomePath.getGraphDBVerticesPath().get(index - 1).get("id").asInt());
                            currentDrawingVertex.addNeighborIDs(currentChromosomePath.getGraphDBVerticesPath().get(index - 1).get("id").asInt());
                        } else {
                            currentDrawingVertex.setIdPredecessorForJoining(-1);
                            currentDrawingVertex.addNeighborIDs(-1);
                        }
                        if (index < currentChromosomePath.getGraphDBVerticesPath().size() - 1) {
                            currentDrawingVertex.setIdSucessorForJoining(currentChromosomePath.getGraphDBVerticesPath().get(index + 1).get("id").asInt());
                            currentDrawingVertex.addNeighborIDs(currentChromosomePath.getGraphDBVerticesPath().get(index + 1).get("id").asInt());
                        } else {
                            currentDrawingVertex.setIdSucessorForJoining(-1);
                            currentDrawingVertex.addNeighborIDs(-1);
                        }
                        //JOIN-Test
                    } else {
                        currentDrawingVertex = vertexDBSugiyamaVertexAssociation.get(currentDBVertex);
                        
                        currentDrawingVertex.addN4JSubStructure(currentChromosomePath);
                        
                        //JOIN-Test
                        if (index > 0) {
                            //currentDrawingVertex.setIdPredecessorForJoining(currentChromosomePath.getGraphDBVerticesPath().get(index - 1).value("id"));
                            currentDrawingVertex.addNeighborIDs(currentChromosomePath.getGraphDBVerticesPath().get(index - 1).get("id").asInt());
                        } else {
                            //currentDrawingVertex.setIdPredecessorForJoining(-1);
                            currentDrawingVertex.addNeighborIDs(-1);
                        }
                        if (index < currentChromosomePath.getGraphDBVerticesPath().size() - 1) {
                            //currentDrawingVertex.setIdSucessorForJoining(currentChromosomePath.getGraphDBVerticesPath().get(index + 1).value("id"));
                            currentDrawingVertex.addNeighborIDs(currentChromosomePath.getGraphDBVerticesPath().get(index + 1).get("id").asInt());
                        } else {
                            //currentDrawingVertex.setIdSucessorForJoining(-1);
                            currentDrawingVertex.addNeighborIDs(-1);
                        }
                        //JOIN-Test
                        
                        //JOIN-Test
//                        if (index > 0 && index < currentChromosomePath.getGraphDBVerticesPath().size() - 1) {
//                            if ((int) currentChromosomePath.getGraphDBVerticesPath().get(index - 1).value("id") == currentDrawingVertex.getIdSucessorForJoining() && (int) currentChromosomePath.getGraphDBVerticesPath().get(index + 1).value("id") == currentDrawingVertex.getIdPredecessorForJoining()) {
//                                index++;
//                                continue;
//                            }
//                        }

                        int idPredecessorToCompare = -1;
                        if (index > 0) {
                        	idPredecessorToCompare = currentChromosomePath.getGraphDBVerticesPath().get(index - 1).get("id").asInt();
                        }
                        int idSucessorToCompare = -1;
                        if (index < currentChromosomePath.getGraphDBVerticesPath().size() - 1) {
                            idSucessorToCompare = currentChromosomePath.getGraphDBVerticesPath().get(index + 1).get("id").asInt();
                        }
                        
                        if (currentDrawingVertex.getIdPredecessorForJoining() != idPredecessorToCompare) {
                            currentDrawingVertex.setJoinable(false);
                        }

                        
                        if (currentDrawingVertex.getIdSucessorForJoining() != idSucessorToCompare) {
                            currentDrawingVertex.setJoinable(false);
                        }
                        
                        /*if (currentDrawingVertex.getIdPredecessorForJoining() == idSucessorToCompare && currentDrawingVertex.getIdSucessorForJoining() == idPredecessorToCompare) {
                        	currentDrawingVertex.setJoinable(true);
                        }*/
                        //JOIN-Test
                        //currentDrawingVertex.setJoinable(false);
                    }

                    //JOIN-Test
                    index++;
                    //JOIN-Test
                }
                isGuideSequence = false;
            }

            //Joining them together, if more then 1 in a row and update ChromosomePath
            for (N4JChromosome currentChromosomePath : sequenceVerticesDataForSugiyama) {
            	//String text = "";
                VertexSugiyama currentDrawingVertex;
                List<VertexSugiyama> verticesToJoin = new ArrayList<>();
                List<VertexSugiyama> newPath = new LinkedList<>();
                for (Node currentDBVertex : currentChromosomePath.getGraphDBVerticesPath()) {
                    currentDrawingVertex = vertexDBSugiyamaVertexAssociation.get(currentDBVertex);
                    //text += currentDrawingVertex.getVertexText() + " (" + currentDrawingVertex.getNeighborIDs().size() + "), ";
                    if (currentDrawingVertex.getNeighborIDs().size() == 2/*currentDrawingVertex.isJoinable()*/) {
                        verticesToJoin.add(currentDrawingVertex);
                    } else {
                        addJoinedVertex(verticesToJoin, newPath);
                        newPath.add(currentDrawingVertex);
                        verticesToJoin.clear();
                    }
                }

                // last Vertices in verticesToJoin
                addJoinedVertex(verticesToJoin, newPath);
                verticesToJoin.clear();
                currentChromosomePath.setSugiyamaVertices(newPath);
                //System.out.println(text);
            }
        } else {
            //no joining
            boolean isGuideSequence = true;
            for (N4JChromosome currentChromosomePath : sequenceVerticesDataForSugiyama) {
                VertexSugiyama currentDrawingVertex;
                List<VertexSugiyama> newPath = new LinkedList<>();
                for (Node currentDBVertex : currentChromosomePath.getGraphDBVerticesPath()) {
                    //SugiyamaVertex isn't created yet
                    if (!vertexDBSugiyamaVertexAssociation.containsKey(currentDBVertex)) {
                        int length = alignmentBlockAssociation.getLength(currentDBVertex);
                        currentDrawingVertex = new VertexSugiyama(currentDBVertex, isGuideSequence, length, currentChromosomePath, additionalVertexData.get(currentDBVertex));
                        vertexDBSugiyamaVertexAssociation.put(currentDBVertex, currentDrawingVertex);
                        vertexList.add(currentDrawingVertex);
                    } else {
                        //SugiyamaVertex is already created
                        currentDrawingVertex = vertexDBSugiyamaVertexAssociation.get(currentDBVertex);
                        currentDrawingVertex.addN4JSubStructure(currentChromosomePath);
                    }
                    newPath.add(currentDrawingVertex);
                }
                isGuideSequence = false;	
                currentChromosomePath.setSugiyamaVertices(newPath);
            }
        }

        System.out.println("Draw Vertex-Number: " + vertexList.size());

        //Set Vertex-Text
//        int vertexNumber = 1;
//        for (N4JChromosome c : sequenceVerticesDataForSugiyama) {
//        	String text = "";
//            //for (VertexSugiyama v : c.getSugiyamaVertices()) {
//        	//System.out.println("---");
//        	for (int i = 0; i < c.getSugiyamaVertices().size() - 1; i++) {
//        		VertexSugiyama v0 = c.getSugiyamaVertices().get(i);
//        		VertexSugiyama v1 = c.getSugiyamaVertices().get(i + 1);
//        		//System.out.println(v0.getId() + " -> " + v1.getId() + ";");
//                //if (v.getText() == null) {
//                    //v.setText("v" + convertIntToSubscriptUnicode(vertexNumber));
//                    //v.setId(vertexNumber);
//                    //vertexNumber++;
//                //}
//            }
//        }

        return vertexList;
    }

    private void addJoinedVertex(
        List<VertexSugiyama> verticesToJoin,
        List<VertexSugiyama> newPath
    ) {
        if (verticesToJoin.size() > 1) {
            //JOIN-Test
            VertexSugiyama newJoinedVertex;
            
            if (!existingJoinedVertices.containsKey(verticesToJoin.get(0)) && !existingJoinedVertices.containsKey(verticesToJoin.get(verticesToJoin.size() - 1))) {
                newJoinedVertex = new VertexSugiyama(joinedVertexCount, verticesToJoin, vertexList, verticesToJoin.get(0).getId());
                joinedVertexCount++;
                vertexList.add(newJoinedVertex);
                existingJoinedVertices.put(verticesToJoin.get(0), newJoinedVertex);
            } else if (existingJoinedVertices.containsKey(verticesToJoin.get(verticesToJoin.size() - 1))) {
            	newJoinedVertex = existingJoinedVertices.get(verticesToJoin.get(verticesToJoin.size() - 1));
            } else {
                newJoinedVertex = existingJoinedVertices.get(verticesToJoin.get(0));
            }
            newPath.add(newJoinedVertex);
            //JOIN-Test

            //VertexSugiyama newJoinedVertex = new VertexSugiyama(joinedVertexCount, verticesToJoin, vertexList, verticesToJoin.get(0).getId());
            //joinedVertexCount++;
            //vertexList.add(newJoinedVertex);
            //newPath.add(newJoinedVertex);
        } else if (verticesToJoin.size() == 1) {
            newPath.add(verticesToJoin.get(0));
        }
    }

    private String convertIntToSubscriptUnicode(int number) {
        String subscriptUnicode = "";

//        System.out.println(number);
//        System.out.println(String.valueOf(number).toCharArray());
        for (char digit : String.valueOf(number).toCharArray()) {
//            System.out.println(digit);
            switch(digit){
                case '0':
                    subscriptUnicode = subscriptUnicode + ("\u2080");
                    break;
                case '1':
                    subscriptUnicode = subscriptUnicode + ("\u2081");
                    break;
                case '2':
                    subscriptUnicode = subscriptUnicode + ("\u2082");
                    break;
                case '3':
                    subscriptUnicode = subscriptUnicode + ("\u2083");
                    break;
                case '4':
                    subscriptUnicode = subscriptUnicode + ("\u2084");
                    break;
                case '5':
                    subscriptUnicode = subscriptUnicode + ("\u2085");
                    break;
                case '6':
                    subscriptUnicode = subscriptUnicode + ("\u2086");
                    break;
                case '7':
                    subscriptUnicode = subscriptUnicode + ("\u2087");
                    break;
                case '8':
                    subscriptUnicode = subscriptUnicode + ("\u2088");
                    break;
                case '9':
                    subscriptUnicode = subscriptUnicode + ("\u2089");
                    break;
            }
        }
        return subscriptUnicode;
    }

}
