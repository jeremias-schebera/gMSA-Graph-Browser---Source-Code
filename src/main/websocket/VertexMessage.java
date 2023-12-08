package main.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Data.AdditionalVertexData;

public class VertexMessage {
	//{ group: 'nodes', data: { id: 'b'}, positions: { x:200, y:100 } },
	private String id;
	private double x;
	private double y;
	private double height;
	private double width;
	private boolean isJoined;
	private String subStructures;
	private List<HashMap<String, HashMap<String, String>>> localSeqAlignmentData;
	
	public VertexMessage(String id, double x, double y, double height, double width, boolean isJoined, String subStructures, List<AdditionalVertexData> localSeqAlignmentData) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.isJoined = isJoined;
		this.subStructures = subStructures;
		transformLocalSeqAlignmentData(localSeqAlignmentData);
	}
	
	private void transformLocalSeqAlignmentData(List<AdditionalVertexData> localSeqAlignmentData) {
		this.localSeqAlignmentData = new ArrayList<>();
		for (AdditionalVertexData additionalVertexData : localSeqAlignmentData) {
			this.localSeqAlignmentData.add(additionalVertexData.getMultipleSeqAlignmentData());
		}
	}
	
	public VertexMessage(String id, double x, double y, boolean isJoined, List<AdditionalVertexData> localSeqAlignmentData) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.isJoined = isJoined;
		transformLocalSeqAlignmentData(localSeqAlignmentData);
	}
	
	public VertexMessage(VertexMessage oldVertexMessage, List<AdditionalVertexData> localSeqAlignmentData) {
		this.id = oldVertexMessage.id;
		this.x = oldVertexMessage.x;
		this.y = oldVertexMessage.y;
		this.height = oldVertexMessage.height;
		this.width = oldVertexMessage.width;
		this.isJoined = oldVertexMessage.isJoined;
		transformLocalSeqAlignmentData(localSeqAlignmentData);
	}

	public String getID() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	
}
