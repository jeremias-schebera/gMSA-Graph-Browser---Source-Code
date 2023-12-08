package main.websocket;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.SerializationUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.google.gson.Gson;

import main.Algorithms.DrawingController;
import main.Algorithms.GraphProjectionSugiyama;
import main.Controller.ControlWindowController;
import main.Controller.UnzipFile;
import main.Data.AfterDrawingGraphData;
import main.Data.Configuration;
import main.Data.VertexSugiyama;



@ServerEndpoint("/creationController")
public class CreationControllerServerEndpoint {

	private ControlWindowController controller;
	private DrawingController drawingController;
	private String fileString;
	
    @OnOpen
    public Session open(Session session, EndpointConfig conf) {
        System.out.println("Creation Websocket server open: " + session);
        controller = new ControlWindowController();
        
        Gson json = new Gson();
        ControllerMessage controllerMessage = new ControllerMessage("sessionID");
        controllerMessage.setSessionID(session.getId());
        String jsonString = json.toJson(controllerMessage);
        sendJSON(session, jsonString);
        
        return session;
    }

    @OnMessage
    public void receiveMessage(String msg, Session session) {
    	String dbFolderName = "";
    	
    	Gson json = new Gson();
    	ControllerMessage message = json.fromJson(msg, ControllerMessage.class);
    	
        if (message.getStatus().equals("finishedUpload")) {
//        	UnzipFile unzip = new UnzipFile(UPLOAD_DIR + "/" + message.getFileName(), UPLOAD_DIR, session.getId());
//        	try {
//    			dbFolderName = unzip.unzip();
//    			System.out.println(dbFolderName);
//    		} catch (IOException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		}
        	
        	//List<String> genomeList = controller.loadDB(UPLOAD_DIR + "/" + dbFolderName);
        	//skip upload
        	//List<String> genomeList = controller.loadDBTinkerpop(UPLOAD_DIR + "/" + "graph.db_tunicate");
        	List<String> genomeList = controller.loadDBNeo4J(dbFolderName);
        	String jsonString = json.toJson(new ControllerMessage("genomeList", genomeList));
        	sendJSON(session, jsonString);
        	
        } else if (message.getStatus().equals("genomeSelected")) {
//        	List<String> subStructureList = controller.getSubStructureList(message.getSelection());
        	List<String> subStructureList = controller.n4JGetSubStructureList(message.getSelection());
        	String jsonString = json.toJson(new ControllerMessage("subStructureList", subStructureList));
        	sendJSON(session, jsonString);
        	
        } else if (message.getStatus().equals("subStructureSelected")) {
//        	int boundaries[] = controller.getSubstructureBoundaries(message.getSelection());
        	int boundaries[] = controller.n4JGetSubstructureBoundaries(message.getSelection());
        	String jsonString = json.toJson(new ControllerMessage("boundaries", boundaries[0], boundaries[1]));
        	sendJSON(session, jsonString);
    	} else if (message.getStatus().equals("loadGS")) {
//    		int extremes[] = controller.loadGuideSequence(message.getMinValue(), message.getMaxValue());
    		int extremes[] = controller.n4JLoadGuideSequence(message.getMinValue(), message.getMaxValue()); 
    		String jsonString = json.toJson(new ControllerMessage("extremes", extremes[0], extremes[1]));
    		sendJSON(session, jsonString);
    	} else if (message.getStatus().equals("lengthFilter")) {
//    		HashMap<String, HashMap<String,Long>> subStructureMap = controller.getComparableChromosomesAndSpecies(message.getMinValue());
    		HashMap<String, HashMap<String,Long>> subStructureMap = controller.n4JGetComparableChromosomesAndSpecies(message.getMinValue());
    		CSMessageCreator csMessageCreator = new CSMessageCreator(subStructureMap);
    		String jsonString = json.toJson(new ControllerMessage("compareabelSubStructureList", csMessageCreator.createCSList()));
    		sendJSON(session, jsonString);
    		
    		String jsonStringCS = json.toJson(csMessageCreator.createCSMessages());
    		//System.out.println(jsonStringCS);
    		sendJSON(session, json.toJson(new ControllerMessage("compareabelSubStructureMap", jsonStringCS)));
    	} else if (message.getStatus().equals("drawGraph")) {
    		// minValue == space; maxValue == thickness
//    		controller.paintGraph(message.getSelectionList(), message.getBool(), message.getMaxValue(), message.getMinValue());
    		controller.n4JPaintGraph(message.getSelectionList(), message.getBool(), message.getMaxValue(), message.getMinValue());
    		
    		doDrawing(message, json, session);
    		
//    		Map<Integer, Layer> indexLayerAssociation = controller.getSugiyamaProjection().getIndexLayerAssociation();
//    		for (int layerIndex : indexLayerAssociation.keySet()) {
//    			Layer layer = indexLayerAssociation.get(layerIndex);
//    			for (VertexSugiyama vertex : layer.getVertexOrderList()) {
//    				int y =vertex.getAssociatedBlockSet().getDrawingPosition() * (100 + 20) + 100;
//    				int x = layerIndex * (100 + 20) + 100;
//    				String jsonStringVertex = json.toJson(new VertexMessage(String.valueOf(vertex.getId()), x, y));
//    				sendJSON(session, json.toJson(new ControllerMessage("addVertex", jsonStringVertex)));
//    			}
//    		}
//    		sendJSON(session, json.toJson(new ControllerMessage("allVerticesSend")));
    		
//    		for (int layerIndex : indexLayerAssociation.keySet()) {
//    			Layer layer = indexLayerAssociation.get(layerIndex);
//    			
//    			for (VertexSugiyama vertex : layer.getVertexOrderList()) {
//    				for (EdgeSugiyama edge : vertex.getOutEdgesClassifiedByVerticalDirection().get(VerticalEdgeDirection.UP)) {
//    					for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(EdgeDirection.FORWARD)) {
//	    					String jsonStringEdge = json.toJson(new EdgeMessage(String.valueOf(edge.getOutNode().getId() + "-" + edge.getInNode().getId() + "_" + chromosomePath.getName()), String.valueOf(edge.getOutNode().getId()), String.valueOf(edge.getInNode().getId())));
//	    					sendJSON(session, json.toJson(new ControllerMessage("addCyElement", jsonStringEdge)));
//    					}
//    					for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(EdgeDirection.BACKWARD)) {
//	    					String jsonStringEdge = json.toJson(new EdgeMessage(String.valueOf(edge.getInNode().getId() + "-" + edge.getOutNode().getId() + "_" + chromosomePath.getName()), String.valueOf(edge.getInNode().getId()), String.valueOf(edge.getOutNode().getId())));
//	    					sendJSON(session, json.toJson(new ControllerMessage("addCyElement", jsonStringEdge)));
//    					}
//    				}
//    				
//    				for (EdgeSugiyama edge : vertex.getOutEdgesClassifiedByVerticalDirection().get(VerticalEdgeDirection.STRAIGHT)) {
//    					for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(EdgeDirection.FORWARD)) {
//	    					String jsonStringEdge = json.toJson(new EdgeMessage(String.valueOf(edge.getOutNode().getId() + "-" + edge.getInNode().getId() + "_" + chromosomePath.getName()), String.valueOf(edge.getOutNode().getId()), String.valueOf(edge.getInNode().getId())));
//	    					sendJSON(session, json.toJson(new ControllerMessage("addCyElement", jsonStringEdge)));
//    					}
//    					for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(EdgeDirection.BACKWARD)) {
//	    					String jsonStringEdge = json.toJson(new EdgeMessage(String.valueOf(edge.getInNode().getId() + "-" + edge.getOutNode().getId() + "_" + chromosomePath.getName()), String.valueOf(edge.getInNode().getId()), String.valueOf(edge.getOutNode().getId())));
//	    					sendJSON(session, json.toJson(new ControllerMessage("addCyElement", jsonStringEdge)));
//    					}
//    				}
//    				
//    				for (EdgeSugiyama edge : vertex.getOutEdgesClassifiedByVerticalDirection().get(VerticalEdgeDirection.DOWN)) {
//    					for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(EdgeDirection.FORWARD)) {
//	    					String jsonStringEdge = json.toJson(new EdgeMessage(String.valueOf(edge.getOutNode().getId() + "-" + edge.getInNode().getId() + "_" + chromosomePath.getName()), String.valueOf(edge.getOutNode().getId()), String.valueOf(edge.getInNode().getId())));
//	    					sendJSON(session, json.toJson(new ControllerMessage("addCyElement", jsonStringEdge)));
//    					}
//    					for (Chromosome chromosomePath : edge.getAssociatedChromosomes().getChromosomePaths(EdgeDirection.BACKWARD)) {
//	    					String jsonStringEdge = json.toJson(new EdgeMessage(String.valueOf(edge.getInNode().getId() + "-" + edge.getOutNode().getId() + "_" + chromosomePath.getName()), String.valueOf(edge.getInNode().getId()), String.valueOf(edge.getOutNode().getId())));
//	    					sendJSON(session, json.toJson(new ControllerMessage("addCyElement", jsonStringEdge)));
//    					}
//    				}
//    			}
//    		}
    		
    		
//    		jsonString= jsonString.substring(0, jsonString.length() - 2);
//    		System.out.println(jsonString);
//    		sendJSON(session, json.toJson(new ControllerMessage("vertices", jsonString)));
    	} else {
        	System.out.println("Status not defined!!!");
        }
    }
    
    private void doDrawing(ControllerMessage message, Gson json, Session session) {
    	Gson json2 = new Gson();
    	DrawingParameterMessage drawingParameterMessage = json2.fromJson(message.getJSonString(), DrawingParameterMessage.class);
	
		drawingController = new DrawingController(drawingParameterMessage, controller.getConfiguration());
		drawingController.drawSugiyamaFramework(controller.getSugiyamaProjection());
				
		for (VertexMessage vertexMessage : drawingController.getVertexMessages()) {
			String jsonStringVertex = json.toJson(vertexMessage);
			sendJSON(session, json.toJson(new ControllerMessage("addVertex", jsonStringVertex)));
		}
		sendJSON(session, json.toJson(new ControllerMessage("allVerticesSend")));
				
		for (EdgeMessage edgeMessage : drawingController.getEdgeMessages()) {
			String jsonStringEdge = json.toJson(edgeMessage);
			sendJSON(session, json.toJson(new ControllerMessage("addEdge", jsonStringEdge)));
		}
		sendJSON(session, json.toJson(new ControllerMessage("allEdgesSend")));
				
		for (ArrowMessage arrowMessage : drawingController.getArrowMessages()) {
			String jsonStringArrow = json.toJson(arrowMessage);
			sendJSON(session, json.toJson(new ControllerMessage("addArrow", jsonStringArrow)));
		}
		sendJSON(session, json.toJson(new ControllerMessage("allArrowsSend")));
				
		String jsonString = json.toJson(new ControllerMessage("drawingArea", (int) Math.round(drawingController.getMaxX()), (int) Math.round(drawingController.getMaxY())));
		sendJSON(session, jsonString);
				
		sendJSON(session, json.toJson(new ControllerMessage("subStructureColor", json.toJson(new SubStructureMessage(controller.getSugiyamaProjection().getN4JSequenceVerticesDataForSugiyama())))));
    }
    
    private void sendJSON(Session session, String jsonString) {
    	try {
			session.getBasicRemote().sendText(jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @OnClose
    public void close(Session session, CloseReason reason) {
        System.out.println("creation socket closed: "+ reason.getReasonPhrase());
        controller.stopDB();
        controller.stopNeo4JDB();
    }

    @OnError
    public void error(Session session, Throwable t) {
        t.printStackTrace();

    }
}