package main.websocket;

import java.util.List;

public class ControllerMessage {
	private String status = "";
	private String sessionID = "";
	private String fileName = "";
	private List<String> selectionList;
	private String selection;
	private int minValue = 0;
	private int maxValue = 0;
	private boolean bool;
	private String jSonString = "";
	
	public ControllerMessage(String status, int minCoordinate, int maxCoordinate) {
		this.status = status;
		this.minValue = minCoordinate;
		this.maxValue = maxCoordinate;
	}
	
	public ControllerMessage(String status, List<String> selectionList) {
		this.status = status;
		this.selectionList = selectionList;
	}
	
	public ControllerMessage(String status, String jSonString) {
		this.status = status;
		this.jSonString = jSonString;
	}
	
	public ControllerMessage(String status) {
		this.status = status;
	}
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getJSonString() {
		return jSonString;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getSelection() {
		return selection;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}
	
	public boolean getBool() {
		return bool;
	}
	
	public List<String> getSelectionList() {
		return selectionList;
	}
}
