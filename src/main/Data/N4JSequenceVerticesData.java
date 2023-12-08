/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.driver.types.Node;

import groovy.json.StringEscapeUtils;
import main.Controller.ControlWindowController;


public class N4JSequenceVerticesData {

    private FreeColor freeColor;

    private List<Pair<Node, List<Node>>> sequenceVerticesData;
    private List<String> subStructureNames;
    private List<String> genomeNames;
    private List<Color> colorList;
    private List<String> composedNames;

    public N4JSequenceVerticesData() {
        sequenceVerticesData = new ArrayList<>();
        subStructureNames = new ArrayList<>();
        genomeNames = new ArrayList<>();
        freeColor = new FreeColor();
        colorList = new ArrayList<>();
        composedNames = new ArrayList<>();
    }
    
    public List<Pair<Node, List<Node>>> getSequenceVerticesData() {
    	return sequenceVerticesData;
    }
    
    public Pair<Node, List<Node>> getSequenceVerticesDataEntry(int index) {
    	return sequenceVerticesData.get(index);
    }
    
    public String getGenomeNameEntry(int index) {
    	return genomeNames.get(index);
    }
    
    public List<String> getGenomeNames() {
    	return genomeNames;
    }
    
    public void add(
        Pair pair,
        String subStructureName,
        String genomeName
    ) {
        sequenceVerticesData.add(pair);
        subStructureNames.add(subStructureName);
        this.composedNames.add(subStructureName + " (" + genomeName + ")");
        genomeNames.add(genomeName);
        colorList.add(freeColor.getFreeColor());
    }
    
    public void add(
            Pair pair,
            String composedName
        ) {
    		this.composedNames.add(composedName);
            sequenceVerticesData.add(pair);
            colorList.add(freeColor.getFreeColor());
            
            //if (composedName.startsWith(ControlWindowController.superGenomeIdentifiere)) {
            //	subStructureNames.add(composedName);
            //	genomeNames.add("");
            //} else {
	            String[] words = composedName.split(" ");
	            subStructureNames.add(words[0]);
	            if (words.length == 2) {
	            	genomeNames.add(words[1].substring(1, words[1].length() - 1));
		            //System.out.println(words[1].substring(1, words[1].length() - 1));
	            } else {
	            	genomeNames.add(words[2].substring(1, words[2].length() - 1));
		            //System.out.println(words[2].substring(1, words[2].length() - 1));
	            }
            //}
        }

    public List<N4JChromosome> getChromosomes() {
        List<N4JChromosome> chromosomes = new ArrayList<>();
        int i = 0;
        for (Pair<Node, List<Node>> chromosomePair : sequenceVerticesData) {
            chromosomes.add(new N4JChromosome(chromosomePair.getKey(), chromosomePair.getValue(), colorList.get(i), subStructureNames.get(i), genomeNames.get(i)));
//            System.out.println(chromosomePair.getKey() + "," + chromosomePair.getValue()+  "," + subStructureNames.get(i)+ "," + genomeNames.get(i));
            i++;
        }
        return chromosomes;
    }
    
    public List<String> getComposedNamesWithoutSuperGenome() {
    	List<String> newList = new ArrayList<>();
    	for (String composedName: composedNames) {
    		if (!composedName.startsWith("Super_Genome")) {
    			newList.add(composedName);
    		}
    	}
    	return newList;
    }
    
    public String getGSComposedName()  {
    	return composedNames.get(0);
    }
    
    public int getIndexForComposedName(String searchedName) {
    	int foundIndex = -1; 
    	for (int i = 0; i < subStructureNames.size(); i++) {
			if (searchedName.equals(subStructureNames.get(i) + " (" + genomeNames.get(i) + ")")) {
				foundIndex = i;
				break;
			}
    	}
    	return foundIndex;
    }
    
    public void reOrderSequences(List<String> newOrder) {
    	
    	List<Integer> indexOrder = new ArrayList<>(); 
    	for (String name : newOrder) {
    		System.out.println(name);
    		for (int i = 0; i < subStructureNames.size(); i++) {
    			if (name.equals(subStructureNames.get(i) + " (" + genomeNames.get(i) + ")")) {
    				indexOrder.add(i);
    				break;
    			}
        	}
    	}
    	
    	List<String> tempGenomeNames = new ArrayList<>();
    	List<String> tempSubStructureNames = new ArrayList<>();
    	List<String> tempComposedNames = new ArrayList<>();
    	List<Pair<Node, List<Node>>> tempSequenceVerticesData  = new ArrayList<>();
    	List<Color> tempColorList = new ArrayList<>();
    	for (int i : indexOrder) {
    		tempGenomeNames.add(genomeNames.get(i));
    		tempSubStructureNames.add(subStructureNames.get(i));
    		tempComposedNames.add(composedNames.get(i));
    		tempSequenceVerticesData.add(sequenceVerticesData.get(i));
    		tempColorList.add(colorList.get(i));
    	}
    	
    	genomeNames = tempGenomeNames;
    	subStructureNames = tempSubStructureNames;
    	composedNames = tempComposedNames;
    	sequenceVerticesData = tempSequenceVerticesData;
    	colorList = tempColorList;
    }
}
