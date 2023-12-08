package main.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class CSMessageCreator {
	private HashMap<String, HashMap<String,Long>> subStructureMap;
	private List<String> subStructureList;
	
	public CSMessageCreator(HashMap<String, HashMap<String,Long>> subStructureMap) {
		this.subStructureMap = subStructureMap;
	}
	
	public List<String> createCSList() {
		subStructureList = new ArrayList<String>();
		
		for (String genome : subStructureMap.keySet()) {
			for (String subStructure : subStructureMap.get(genome).keySet()) {
				String subStructureIdentifier = subStructure + " (" + genome + ")";
				subStructureList.add(subStructureIdentifier);
			}
		}
		
		return subStructureList;
	}
	
	public List<CSMessage> createCSMessages() {
		int i = 0;
		List<CSMessage> rootChildren = new ArrayList<CSMessage>();
		for (String genome : subStructureMap.keySet()) {
			CSMessage genomeMessage = new CSMessage(String.valueOf(i), genome);
			i++;
			List<CSMessage> genomeChildren = new ArrayList<CSMessage>();
			for (String subStructure : subStructureMap.get(genome).keySet()) {
				CSMessage subStructureMessage = new CSMessage(String.valueOf(i), subStructure, Integer.valueOf(subStructureMap.get(genome).get(subStructure).toString()));
				i++;
				genomeChildren.add(subStructureMessage);
			}
			genomeMessage.setChildren(genomeChildren);
			rootChildren.add(genomeMessage);
		}
		
		return rootChildren;
	}
}
