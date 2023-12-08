package main.websocket;

public class EdgeMessage {
	//{ group: 'nodes', data: { id: 'b'}, positions: { x:200, y:100 } },
	private String points;
	private int source;
	private int target;
	private String subStructure;
	private String color;
	private double thickness;
	
	public EdgeMessage(String points, int source, int target, String subStructure, String color, double thickness) {
		this.points = points;
		this.source = source;
		this.target = target;
		this.subStructure = subStructure;
		this.color = color;
		this.thickness = thickness;
	}

	
	public String getSourceString() {
		return String.valueOf(source);
	}
	
	public String getTargetString() {
		return String.valueOf(target);
	}
}
