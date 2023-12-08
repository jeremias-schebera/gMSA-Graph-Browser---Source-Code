package main.websocket;

import java.util.List;

public class CSMessage {
	private String id;
	private String text;
	private List<CSMessage> children;
	private int blockCount;
	
	public CSMessage(String id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public CSMessage(String id, String text, int blockCount) {
		this.id = id;
		this.text = text;
		this.blockCount = blockCount;
	}
	
	public void setChildren(List<CSMessage> children) {
		this.children = children;
	}
		
}
