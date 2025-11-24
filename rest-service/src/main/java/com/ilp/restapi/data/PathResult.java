package com.ilp.restapi.data;

import java.util.List;

public class PathResult {

    private List<LngLatDTO> path;
    private int moves;
    private double totalDistance;

    public List<LngLatDTO> getPath() {
        return path;
    }

    public void setPath(List<LngLatDTO> path) {
        this.path = path;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}

