package main.websocket;

import java.util.*;

public class MultipleSeqAlignmentMessage {
	private List<HashMap<String, String>> multipleSeqAlignmentList;
	private double x;
	private double y;
	
	public MultipleSeqAlignmentMessage(List<HashMap<String, String>> multipleSeqAlignmentList, double x, double y) {
		this.multipleSeqAlignmentList = multipleSeqAlignmentList;
		this.x = x;
		this.y = y;
	}
}
