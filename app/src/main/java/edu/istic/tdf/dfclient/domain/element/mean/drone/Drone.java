package edu.istic.tdf.dfclient.domain.element.mean.drone;

import java.util.Date;
import java.util.HashMap;

import edu.istic.tdf.dfclient.domain.geo.Location;

import edu.istic.tdf.dfclient.domain.Entity;
import edu.istic.tdf.dfclient.domain.element.Element;
import edu.istic.tdf.dfclient.domain.element.Role;
import edu.istic.tdf.dfclient.domain.element.mean.MeanState;
import edu.istic.tdf.dfclient.domain.element.mean.drone.mission.IMission;
import edu.istic.tdf.dfclient.drawable.PictoFactory;

/**
 * A mean of type drone
 */
public class Drone extends Element implements IDrone {

    /**
     * represent the list of states with the corresponding timestamp
     */
    private HashMap<MeanState, Date> states;

    /**
     * represent the current mission of the drone
     */
    private IMission mission;

    /**
     * represent the current action
     * But not use for the moment !
     */
    private String action;

    /**
     *it's the current fonctionnality of the InterventionMean: water,fire, sap...
     */
    private Role role;

    /**
     *
     * It's the name used for the GUI
     */
    private String name;

    /**
     * InterventionMean's location
     * Maybe change int the futur (the type)
     */
    private Location location;

    private PictoFactory.ElementForm form = PictoFactory.ElementForm.AIRMEAN;

    public Drone() {
        this.states.put(MeanState.ASKED, new Date());
        this.states.put(MeanState.VALIDATED, null);
        this.states.put(MeanState.ARRIVED, null);
        this.states.put(MeanState.ENGAGED, null);
        this.states.put(MeanState.RELEASED, null);
        this.role=Role.DEFAULT;
        this.name="";
    }

    @Override
    public void setState(MeanState state) {
        this.states.put(state, new Date());
    }

    @Override
    public MeanState getState() {
        MeanState currentState;
        if(this.states.get(MeanState.RELEASED) != null){
            currentState = MeanState.RELEASED;
        }else if (this.states.get(MeanState.ENGAGED) != null){
            currentState = MeanState.ENGAGED;
        }else if (this.states.get(MeanState.ARRIVED) != null){
            currentState = MeanState.ARRIVED;
        }else if (this.states.get(MeanState.VALIDATED) != null){
            currentState = MeanState.VALIDATED;
        }else{
            currentState = MeanState.ASKED;
        }
        return currentState;
    }

    @Override
    public Date getStateDate(MeanState state) {
        return this.states.get(state);
    }

    @Override
    public void setAction(String action) {
        this.action=action;
    }

    @Override
    public String getAction() {
        return this.action;
    }

    @Override
    public void setRole(Role role) {
        this.role=role;
    }

    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public void setLocation(Location location) {
        this.location=location;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PictoFactory.ElementForm getForm() {
        return form;
    }

    @Override
    public void setForm(PictoFactory.ElementForm form) {
        this.form = form;
    }

    @Override
    public IMission getMission() {
        return this.mission;
    }

    @Override
    public void setMission(IMission mission) {
        this.mission = mission;
    }

    @Override
    public boolean hasMission() {
        return (this.mission != null);
    }
}
