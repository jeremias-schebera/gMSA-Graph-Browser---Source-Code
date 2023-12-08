package main.Data;

import java.io.Serializable;
import java.util.HashMap;

public class AdditionalVertexData{
	private HashMap<String, HashMap<String, String>> multipleSeqAlignmentData;

	public AdditionalVertexData(HashMap<String, HashMap<String, String>> multipleSeqAlignmentData) {
		this.multipleSeqAlignmentData = multipleSeqAlignmentData;
	}
	
	public HashMap<String, HashMap<String, String>> getMultipleSeqAlignmentData() {
		return multipleSeqAlignmentData;
	}	
}


