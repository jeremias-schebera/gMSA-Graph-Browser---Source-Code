package main.websocket;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import main.Data.Chromosome;
import main.Data.N4JChromosome;
import main.Data.VertexSugiyama;

public class SubStructureMessage {
	private List<String> subStructureJSclasses;
	
	public SubStructureMessage(List<N4JChromosome> chromosomes) {
		subStructureJSclasses = new ArrayList<String>();
		Gson json = new Gson();
		
		for (N4JChromosome chr : chromosomes) {
			List<VertexSugiyama> vertexSeq = chr.getSugiyamaVertices();
			String startVertexID = String.valueOf(vertexSeq.get(0).getId());
			String endVertexID = String.valueOf(vertexSeq.get(vertexSeq.size() - 1).getId());
			//System.out.println(chr.getSubStructureName() + ", " + chr.getComposedName() + ", " + chr.getGenomeName());
			subStructureJSclasses.add(json.toJson(new SubStructureJSclass(chr.getComposedName(), chr.getSubStructureName(), chr.getColorInHex(), startVertexID, endVertexID)));
		}
	}

}
