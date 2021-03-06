package edu.istic.tdf.dfclient.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import edu.istic.tdf.dfclient.R;
import edu.istic.tdf.dfclient.TdfApplication;
import edu.istic.tdf.dfclient.domain.element.Element;
import edu.istic.tdf.dfclient.domain.element.ElementType;
import edu.istic.tdf.dfclient.domain.element.IElement;
import edu.istic.tdf.dfclient.domain.element.mean.IMean;
import edu.istic.tdf.dfclient.domain.element.mean.MeanState;
import edu.istic.tdf.dfclient.domain.element.mean.drone.Drone;
import edu.istic.tdf.dfclient.domain.element.mean.interventionMean.InterventionMean;
import edu.istic.tdf.dfclient.domain.geo.Location;
import edu.istic.tdf.dfclient.drawable.PictoFactory;

public class MeansTableFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TableLayout meanTab;

    private List<IMean> means=new ArrayList<>();

    private Spinner spinner;

    private Button validation;

    private HashMap<String,IElement> elementsMean=new HashMap<>();

    private HashMap<String,TableRow> link=new HashMap<>();

    private final int NBCOLUMS=7;
    private List meanList;

    private boolean isCodis;

    public MeansTableFragment() {
        // Required empty public constructor
    }

    public static MeansTableFragment newInstance() {
        MeansTableFragment fragment = new MeansTableFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.isCodis = ((TdfApplication)this.getActivity().getApplication()).loadCredentials().isCodisUser();

        View view=inflater.inflate(R.layout.fragment_means_table, container, false);
        meanTab=(TableLayout)view.findViewById(R.id.meanTab);

        spinner=(Spinner)view.findViewById(R.id.spinner);
        validation=(Button)view.findViewById(R.id.meanTableValidationbtn);

        return view;
    }

    /**
     * initialize the two components spinner and validation
     */
    public void initComponentForAddNewAskedMean()
    {
        if(mListener.isInterventionArchived() || isCodis){
            spinner.setVisibility(View.INVISIBLE);
            validation.setVisibility(View.INVISIBLE);

        }else{

            final List meanList = getDefaultMeanList();
            ArrayAdapter adapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_item,meanList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            validation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Element element;
                    IMean mean;
                    if(spinner.getSelectedItem().toString().equals("DRONE"))
                    {
                        mean = new Drone();
                        mean.setForm(PictoFactory.ElementForm.AIRMEAN_PLANNED);
                    }
                    else
                    {
                        mean = new InterventionMean();
                        mean.setForm(PictoFactory.ElementForm.MEAN_PLANNED);
                        mean.setAction("Action par défaut");
                    }

                    element = (Element)mean;
                    element.setName(spinner.getSelectedItem().toString());
                    ((IMean)element).setState(MeanState.ASKED);
                    element.setLocation(new Location());
                    element.setForm(element.getForm());
                    element.setRoleFromMeanType(spinner.getSelectedItem().toString());
                    mListener.handleValidation(element);
                }
            });
        }
    }

    @Deprecated
    private void loadMeans(/*List<List<String>> means*/) {

        for(int i=0;i<means.size();i++){
            addElment(means.get(i));
        }
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


    public void updateElement(IElement element) {
        if (element.getType()== ElementType.AIRMEAN || element.getType()==ElementType.MEAN){

            if(link.containsKey(element.getId())){
                updateElem((IMean) element);
            }else{
                addElment((IMean) element);
                //means.add((IMean)element);
                elementsMean.put(element.getId(),element);
            }
        }
    }

    public void updateElements(Collection<Element> element) {
        Iterator<Element> iterator= element.iterator();
        while (iterator.hasNext()){
            updateElement(iterator.next());
        }
    }


    private void addElment(IMean element) {

        TableRow tableRow=new TableRow(meanTab.getContext());

        addTextViews(element, tableRow);

        link.put(element.getId(), tableRow);
        meanTab.addView(tableRow);
    }

    private void updateElem(IMean element) {
        TableRow tableRow =link.get(element.getId());
        tableRow.removeAllViews();

        addTextViews(element, tableRow);
        elementsMean.put(element.getId(), element);
    }

    private void addTextViews(IMean element,TableRow tableRow) {
        LinearLayout relativeLayout=new LinearLayout(tableRow.getContext());

        TextView name = new TextView(meanTab.getContext());
        name.setText(element.getName());
        name.setGravity(Gravity.CENTER_HORIZONTAL);
        name.setTextSize(20);
        name.setTypeface(null, Typeface.BOLD);
        tableRow.addView(name);

        tableRow.setBackgroundColor(element.getRole().getLightColor());
        HashMap<MeanState, Date> currentStates = element.getStates();

        Date d = currentStates.get(MeanState.ASKED);
        addMeanState(tableRow, d);
        d = currentStates.get(MeanState.VALIDATED);
        addMeanState(tableRow, d);
        addCancelButton(relativeLayout, element, d, currentStates.get(MeanState.RELEASED));
        addValidationButtonForCodis(relativeLayout, element, d, currentStates.get(MeanState.RELEASED));
        d = currentStates.get(MeanState.ARRIVED);
        addMeanState(tableRow, d);
        d = currentStates.get(MeanState.ENGAGED);
        addMeanState(tableRow, d);
        d = currentStates.get(MeanState.RELEASED);
        addMeanState(tableRow, d);
        addDeleteButton(relativeLayout,element,d,currentStates.get(MeanState.VALIDATED));
        tableRow.addView(relativeLayout);
    }

    private void addDeleteButton(final LinearLayout relativeLayout, final IMean element, Date released, Date valided) {
        if(!mListener.isInterventionArchived() && !isCodis&& released==null && valided!=null) {
            Button deleteButton = new Button(relativeLayout.getContext());
            deleteButton.setText(R.string.supprimer_button_mean_table);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(relativeLayout.getContext())
                            .setTitle(R.string.supprimer_button_mean_table)
                            .setMessage(R.string.popup_supprimer_button_mean_table)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeElement((Element) element);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });
            relativeLayout.addView(deleteButton);
        }
    }

    private void addCancelButton(LinearLayout relativeLayout, final IElement element, Date validated, Date released) {
        if(!mListener.isInterventionArchived() && !isCodis&&validated==null &&released==null){
            Button cancelButton=new Button(relativeLayout.getContext());
            cancelButton.setText("Annuler");
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IMean mean = (IMean) element;
                    mean.setState(MeanState.RELEASED);
                    mListener.handleValidation((Element) mean);
                }
            });

            relativeLayout.addView(cancelButton);
        }
    }

    private void addValidationButtonForCodis(final LinearLayout relativeLayout, final IElement element, Date valided, Date released) {
        if(!mListener.isInterventionArchived() && isCodis && valided==null && released==null){
            Button validationButton=new Button(relativeLayout.getContext());
            validationButton.setText("Valider");
            validationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IMean mean = (IMean) element;
                    mean.setState(MeanState.VALIDATED);
                    mListener.handleValidation((Element) mean);
                }
            });

            Button refuseButton=new Button(relativeLayout.getContext());
            refuseButton.setText("Refuser");
            refuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(relativeLayout.getContext())
                            .setTitle("Refuser")
                            .setMessage("Attention !\n Êtes vous sûr de vouloir refuser cette demande?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    IMean mean = (IMean) element;
                                    mean.setState(MeanState.RELEASED);
                                    mListener.handleValidation((Element) mean);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            relativeLayout.addView(validationButton);

            relativeLayout.addView(refuseButton);
        }
    }

    private void addMeanState(TableRow tableRow,Date d){
        TextView textView=new TextView(meanTab.getContext());
        if(d!=null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
            textView.setText(sdf.format(d));
            textView.setTextSize(20);
        }
        else
        {
            textView.setText("");
        }

        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textView);
    }

    @Deprecated
    private TableRow createEmptyRow(){
        TableRow tableRow=new TableRow(meanTab.getContext());
        addTextViewsEditable(tableRow);

        return tableRow;
    }

    @Deprecated
    private void addTextViewsEditable(TableRow tableRow) {
        for(int i=0;i<NBCOLUMS;i++){
            TextView textView=new TextView(meanTab.getContext());
            textView.setText("");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            tableRow.addView(textView);
            if(i==0) {
                textView.setEnabled(true);
            }
        }
    }

    public List getDefaultMeanList() {
        //TODO recup dans la base la liste des moyens
        List<String> list=new ArrayList<>();
        list.add("FPT");
        list.add("VSAV");
        list.add("EPA");
        list.add("VLCG");
        list.add("CCF");
        list.add("CCGC");
        list.add("VLHR");
        list.add("DRONE");
        return list;
    }


    public interface OnFragmentInteractionListener {

        void handleValidation(Element element);

        /**
         *
         * @return true iff the current intervention is archived
         */
        boolean isInterventionArchived();
    }

    private void removeElement(Element element){
        if (element.isMeanFromMeanTable()) {
            ((IMean) element).setState(MeanState.RELEASED);
            mListener.handleValidation(element);
        }
    }

    public void removeElementFromUi(Collection<Element> element){

    }

    private void removeElements(Collection<Element> elements){
        Iterator<Element> it=elements.iterator();
        while(it.hasNext()){
            removeElement(it.next());
        }
    }
}