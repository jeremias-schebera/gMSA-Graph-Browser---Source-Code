/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zeckzer
 */
public class N4JChromosomePaths {

    private Map<EdgeDirection, List<N4JChromosome>> chromosomePaths;
    private String forwardClasses;
    private String backwardClasses;

    public N4JChromosomePaths() {
        chromosomePaths = new HashMap<>();
        chromosomePaths.put(EdgeDirection.BACKWARD, new ArrayList<>());
        chromosomePaths.put(EdgeDirection.FORWARD, new ArrayList<>());
        forwardClasses = "";
        backwardClasses = "";
    }

    public void add(
            EdgeDirection direction,
            N4JChromosome chromosome
    ) {
        chromosomePaths.get(direction).add(chromosome);
        if (direction.equals(EdgeDirection.FORWARD)) {
        	forwardClasses += "s" + chromosome.getSubStructureName() + "g" + chromosome.getGenomeName() + " ";
        } else {
        	backwardClasses += "s" + chromosome.getSubStructureName() + "g" + chromosome.getGenomeName() + " ";
        }
    }

    public boolean isEmpty(
            EdgeDirection direction
    ) {
        return chromosomePaths.get(direction).isEmpty();
    }

    public int size(
            EdgeDirection direction
    ) {
        return chromosomePaths.get(direction).size();
    }

    public List<N4JChromosome> getChromosomePaths(
            EdgeDirection direction
    ) {
        return chromosomePaths.get(direction);
    }
    
    public String getClassString(EdgeDirection direction) {
    	if (direction.equals(EdgeDirection.FORWARD)) {
    		return forwardClasses.substring(0, forwardClasses.length() - 1);
    	} else {
    		return backwardClasses.substring(0, backwardClasses.length() - 1);
    	}
    }
}
