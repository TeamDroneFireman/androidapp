package edu.istic.tdf.dfclient.domain.element.mean.drone.mission;

import java.util.ArrayList;

import edu.istic.tdf.dfclient.domain.geo.GeoPoint;

/**
 * Created by btessiau on 20/04/16.
 */
public class Mission {

    public enum PathMode{

        SIMPLE("Parcours simple"),
        CYCLE("Boucle"),
        ZONE("Zone");

        private String title;

        private PathMode(String title){
            this.title = title;
        }

        @Override public String toString(){
            return title;
        }
    }

    private ArrayList<GeoPoint> pathPoints = new ArrayList<>();
    private PathMode pathMode = PathMode.SIMPLE;

    public ArrayList<GeoPoint> getPathPoints() {
        return pathPoints;
    }

    public Mission.PathMode getPathMode(){
        return pathMode;
    }

    public void setPathMode(Mission.PathMode pathMode){
        this.pathMode = pathMode;
    }

    public void setPathPoints(ArrayList<GeoPoint> pathPoints) {
        this.pathPoints = pathPoints;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "pathPoints=" + pathPoints +
                '}';
    }

    public Mission() {
    }

    public Mission(ArrayList<GeoPoint> pathPoints){
        this.pathPoints = pathPoints;
    }

    public Mission(ArrayList<GeoPoint> pathPoints, PathMode pathMode){
        this.pathPoints = pathPoints;
        this.pathMode = pathMode;
    }


}
