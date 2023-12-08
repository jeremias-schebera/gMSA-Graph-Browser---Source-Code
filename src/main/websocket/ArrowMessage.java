package main.websocket;

public class ArrowMessage {
	private String points;
	private int source;
	private int target;
	private String subStructures;
	
	public ArrowMessage(String points, int source, int target, String subStructures) {
		this.points = points;
		this.source = source;
		this.target = target;
		this.subStructures = subStructures;
	}

	
	public String getSourceString() {
		return String.valueOf(source);
	}
	
	public String getTargetString() {
		return String.valueOf(target);
	}
}
