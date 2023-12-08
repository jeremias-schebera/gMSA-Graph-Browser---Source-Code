package main.Data;

import java.awt.Color;

import main.Data.VertexSugiyama;

public class Rectangle{

    private VertexSugiyama associatedVertex;
    private Color color;

    public Rectangle(
        double xCenterCoordinate,
        double yCenterCoordinate,
        double length,
        double height,
        Color color,
        VertexSugiyama associatedVertex
    ) {
        setX(xCenterCoordinate - 0.5 * length);
        setY(yCenterCoordinate - 0.5 * height);
        setWidth(length);
        setHeight(height);
//        this.setCenterX(xCenterCoordinate);
//        this.setCenterY(yCenterCoordinate);
//        this.setRadius(radius);
        this.setStroke(color);
        this.setFill(color);

        this.associatedVertex = associatedVertex;
        associatedVertex.setRectangle(this);

        this.color = color;

    }


    public VertexSugiyama getAssociatedVertex() {
        return associatedVertex;
    }

    public Color getColor() {
        return color;
    }

}
