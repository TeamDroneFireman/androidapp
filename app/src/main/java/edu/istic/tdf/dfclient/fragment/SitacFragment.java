package edu.istic.tdf.dfclient.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import edu.istic.tdf.dfclient.R;
import edu.istic.tdf.dfclient.UI.Tool;
import edu.istic.tdf.dfclient.domain.element.Element;
import edu.istic.tdf.dfclient.domain.element.IElement;
import edu.istic.tdf.dfclient.domain.element.Role;
import edu.istic.tdf.dfclient.drawable.PictoFactory;

public class SitacFragment extends SupportMapFragment implements OnMapReadyCallback, Observer {

    private OnFragmentInteractionListener mListener;
    private GoogleMap googleMap;

    // Liste d'association marker <--> element
    private HashMap<Marker, Element> markersList = new HashMap<>();

    public SitacFragment() {
    }

    public static SitacFragment newInstance() { return new SitacFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sitac, container, false);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        initMap();

    }

    private void initMap(){

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(hasElementSelected()){
                    Element element = createElementFromLatLng(latLng);
                    addMarker(element).showInfoWindow();
                } else {

                    cancelSelection();
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                mListener.setSelectedElement(markersList.get(marker));
                return false;
            }

        });
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mListener.getInterventionLatitude(), mListener.getInterventionLongitude()), 18));

    }

    private Element createElementFromLatLng(LatLng latLng){
        Tool tool = mListener.getSelectedTool();
        return mListener.handleElementAdded(tool.getForm(), latLng.latitude, latLng.longitude);
    }

    private void cancelSelection(){

        mListener.handleCancelSelection();
    }

    private boolean hasElementSelected(){
        return mListener.getSelectedTool() != null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable, Object data) {}

    public interface OnFragmentInteractionListener {

        public Double getInterventionLatitude();
        public Double getInterventionLongitude();

        public Tool getSelectedTool();

        public void setSelectedElement(IElement element);
        public void setSelectedElement(Element element);
        public Element handleElementAdded(PictoFactory.ElementForm form, Double latitude, Double longitude);
        public void handleCancelSelection();

    }

    private Marker getMarker(IElement element){
        if(markersList.containsValue(element)){
            for (Map.Entry<Marker, Element> entry : markersList.entrySet()) {
                Marker marker = entry.getKey();
                IElement elementValue = entry.getValue();
                if(elementValue.equals(element)){
                    return marker;
                }
            }
        }
        return null;
    }

    private Marker addMarker(Element element){

        if(element.getRole() == null ){
            element.setRole(Role.DEFAULT);
        }

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(element.getLocation().getGeopoint().getLatitude(), element.getLocation().getGeopoint().getLongitude()))
                .title(element.getName())
                .icon(BitmapDescriptorFactory.fromBitmap(
                        PictoFactory.createPicto(getContext())
                                .setLabel(element.getName())
                                .setDrawable(element.getForm().getDrawable())
                                .setColor(element.getRole().getColor())
                                .toBitmap()
                )));

        markersList.put(marker, element);
        return marker;
    }

    private void updateMarker(Marker marker, Element element){

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(PictoFactory.createPicto(getContext()).setElement(element).toBitmap()));
        marker.setPosition(new LatLng(element.getLocation().getGeopoint().getLatitude(), element.getLocation().getGeopoint().getLongitude()));
        marker.setTitle(element.getName());

        markersList.remove(marker);
        markersList.put(marker, element);

    }

    public void updateElement(Element element){
        Marker marker = getMarker(element);
        if(marker == null){
            addMarker(element);
        } else {
            updateMarker(marker, element);
        }
    }

    public void updateElements(Collection<Element> elements){
        for(Element element : elements){
            updateElement(element);
        }
    }

    public void removeElement(IElement element){
        Marker marker = getMarker(element);
        if(marker != null){
            markersList.remove(marker);
        }
    }

    public void removeElements(Collection<IElement> elements){
        for(IElement element : elements){
            removeElement(element);
        }
    }

}