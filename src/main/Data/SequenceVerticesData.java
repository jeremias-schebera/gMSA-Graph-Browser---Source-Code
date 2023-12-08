/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import groovy.json.StringEscapeUtils;
import main.Controller.ControlWindowController;

/**
 *
 * @author zeckzer
 */
public class SequenceVerticesData {

    private FreeColor freeColor = new FreeColor();

    private List<Pair<Vertex, List<Vertex>>> sequenceVerticesData;
    private List<String> subStructureNames;
    private List<String> genomeNames;

    public SequenceVerticesData() {
        sequenceVerticesData = new ArrayList<>();
        subStructureNames = new ArrayList<>();
        genomeNames = new ArrayList<>();
    }

    public void add(
        Pair pair,
        String subStructureName,
        String genomeName
    ) {
        sequenceVerticesData.add(pair);
        subStructureNames.add(subStructureName);
        genomeNames.add(genomeName);
    }
    
    public void add(
            Pair pair,
            String composedName
        ) {
            sequenceVerticesData.add(pair);
            
            //if (composedName.startsWith(ControlWindowController.superGenomeIdentifiere)) {
            //	subStructureNames.add(composedName);
            //	genomeNames.add("");
            //} else {
	            String[] words = composedName.split(" ");
	            subStructureNames.add(words[0]);
	            if (words.length == 2) {
	            	genomeNames.add(words[1].substring(1, words[1].length() - 1));
		            System.out.println(words[1].substring(1, words[1].length() - 1));
	            } else {
	            	genomeNames.add(words[2].substring(1, words[2].length() - 1));
		            System.out.println(words[2].substring(1, words[2].length() - 1));
	            }
            //}
        }

    public List<Chromosome> getChromosomes() {
        List<Chromosome> chromosomes = new ArrayList<>();
        int i = 0;
        for (Pair<Vertex, List<Vertex>> chromosomePair : sequenceVerticesData) {
            chromosomes.add(new Chromosome(chromosomePair.getKey(), chromosomePair.getValue(), freeColor.getFreeColor(), subStructureNames.get(i), genomeNames.get(i)));
            i++;
            //System.out.println(chromosomePair.getKey() + "," + chromosomePair.getValue()+ "," + freeColor.getFreeColor()+ "," + subStructureNames.get(i)+ "," + genomeNames.get(i));
        }
        return chromosomes;
    }
}
