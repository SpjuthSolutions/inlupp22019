/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inlupp22019;

import inlupp22019.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

abstract class Place extends JComponent {

    private String name;
    private int category;
    private Position thePos;
    private Polygon py = triangle();
    public boolean isMarked = false;
    private boolean whatPlace;
    private static HashSet<Place> selected = new HashSet<>();

    protected Place(String name, int category, int x, int y, boolean whatPlace) {
        this.name = name;
        this.category = category;

        this.thePos = new Position(x, y);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.whatPlace = whatPlace;

    }



    public String getCatString() {
        if (category == 0)
            return "Bus";
        else if (category == 1)
            return "Underground";
        else if (category == 2)
            return "Train";
        else
            return "none";
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getColor());

        g.fillPolygon(py);

        setVisible(true);

        setBounds(thePos.getX() - 25, thePos.getY() - 50, 50, 50);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Polygon triangle() {
        int yPoints[] = { 0, 0, 50 };
        int xPoints[] = { 0, 50, 25 };
        int nPoints = 3;
        return new Polygon(xPoints, yPoints, nPoints);
        // g.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void setMarked() {
        isMarked = true;

    }

    public void setUnMarked() {
        isMarked = false;

    }

    private Color getColor() {

        if (!isMarked) {
            if (category == 0) {
                return Color.RED;
            } else if (category == 1) {
                return Color.GREEN;
            } else if (category == 2) {
                return Color.BLUE;
            } else {
                return Color.BLACK;
            }
        } else {
            return Color.PINK;
        }

    }



    /*public boolean getWhatPlace() {
        return whatPlace;
    }
*/
    public static HashSet<Place> getSelected() {
        return selected;
    }

    protected void on() {
        isMarked = true;
        repaint();
        selected.add(Place.this);
    }

    protected void off() {
        isMarked = false;
        repaint();
        selected.remove(Place.this);
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }

    public Position getPos() {
        return thePos;
    }

    public String getCoord() {
        return thePos.toString();
    }

    public String toString() {
        return name + category;
    }

}



class NamedPlace extends Place {

    public NamedPlace(String name, int category, int x, int y, boolean whatPlace) {
        super(name, category, x, y, whatPlace);

    }
}

class DescribedPlace extends Place {

    private String description;

    public DescribedPlace(String name, int category, String description, int x, int y, boolean whatPlace) {
        super(name, category, x, y, whatPlace);
        this.description = description;
        // this.typeOfPlace = typeOfPlace;

    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getName() + getCategory() + description;

    }

}
