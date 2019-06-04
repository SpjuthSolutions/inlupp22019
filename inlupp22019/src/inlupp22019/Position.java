/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inlupp22019;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return  x + ", " + y;
    }

    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position p = (Position) other;
            return x == p.x && y == p.y;
        } else
            return false;
    }

    public int hashCode() {
        String code = "" + x * 10 + y * 10;
        return Integer.parseInt(code);
    }

}
