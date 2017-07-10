package com.skycaster.geomapper.bean;

/**
 * Created by 廖华凯 on 2017/7/10.
 */

public class Vertice {
    private double x,y;

    public Vertice(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Vertice{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
