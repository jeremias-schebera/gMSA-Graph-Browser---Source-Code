/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.Data;

import java.util.Comparator;

/**
 *
 * @author zeckzer
 */
public class InEdgeComparator
    implements Comparator<EdgeSugiyama> {

    @Override
    public int compare(EdgeSugiyama edge1, EdgeSugiyama edge2) {
        VertexSugiyama vertex1 = edge1.getOutNode();
        VertexSugiyama vertex2 = edge2.getOutNode();

        return (vertex1.getAssociatedBlockSet().getDrawingPosition() - vertex2.getAssociatedBlockSet().getDrawingPosition());
    }
}
