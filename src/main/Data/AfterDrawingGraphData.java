package main.Data;

import java.util.*;

public class AfterDrawingGraphData {
	private HashMap<Integer, VertexSugiyama> vertices;
	private HashSet<String> selectedVertexIDs;
	
	public AfterDrawingGraphData() {
		vertices = new HashMap<Integer, VertexSugiyama>();
	}
	
	public void addVertex(VertexSugiyama vertex) {
		vertices.put(vertex.getId(), vertex);
	}
	
	public VertexSugiyama getVertex(Integer id) {
		return vertices.get(id);
	}
	
	/*public HashSet<VertexSugiyama> getVertices(List<String> ids) {
		HashSet<VertexSugiyama> returnHashSet = new HashSet<VertexSugiyama>();
		for (String stringId : ids) {
			int id = Integer.valueOf(stringId);
			returnHashSet.add(vertices.get(id));
		}
		return returnHashSet;
	}*/
	
	public void setSelectedVertexIDs(List<String> ids) {
		HashSet<VertexSugiyama> returnHashSet = new HashSet<VertexSugiyama>();
		selectedVertexIDs = new HashSet<>();
		for (String stringId : ids) {
			selectedVertexIDs.add(stringId);
		}
	}
	
	public HashSet<String> getSelectedVertexIDs() {
		return selectedVertexIDs;
	}
}
