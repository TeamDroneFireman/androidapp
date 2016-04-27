package edu.istic.tdf.dfclient.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.istic.tdf.dfclient.R;
import edu.istic.tdf.dfclient.TdfApplication;
import edu.istic.tdf.dfclient.auth.Credentials;
import edu.istic.tdf.dfclient.dao.DaoSelectionParameters;
import edu.istic.tdf.dfclient.dao.domain.InterventionDao;
import edu.istic.tdf.dfclient.dao.handler.IDaoSelectReturnHandler;
import edu.istic.tdf.dfclient.domain.geo.Location;
import edu.istic.tdf.dfclient.domain.intervention.Intervention;
import edu.istic.tdf.dfclient.domain.intervention.SinisterCode;

public class InterventionListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @Bind(R.id.interventionCreationButton)
    Button interventionCreationBt;

    @Bind(R.id.interventions_list)
    ListView interventionsList;

    InterventionDao interventionDao;

    // for listView intervention
    private ArrayList<String> interventions = new ArrayList<String>();
    private ArrayAdapter<String> interventionsAdapter;

    // the collection of all object interventions
    ArrayList<Intervention> interventionArrayList = new ArrayList<>();


    public InterventionListFragment() {
        // Required empty public constructor
    }

    public static InterventionListFragment newInstance(InterventionDao interventionDao) {
        InterventionListFragment fragment = new InterventionListFragment();
        fragment.interventionDao = interventionDao;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intervention_list, container, false);
        ButterKnife.bind(this, view);// Inflate the layout for this fragment

        //interventionCreationBt
        interventionCreationBt.setEnabled(isCodis());

        interventionCreationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.handleInterventionCreation();
            }
        });

        //interventionsList
        interventionsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                interventions);

        interventionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.handleInterventionSelected(interventionArrayList.get(position));
                // TODO: 27/04/16 meilleur couleur
                for(int i = 0; i<interventionsList.getCount();i++){
                    TextView v = (TextView)interventionsList.getChildAt(i);
                    v.setBackgroundColor(parent.getSolidColor());
                    v.setTypeface(Typeface.DEFAULT);
                    v.setTextColor(Color.LTGRAY);
                }

                view.setBackgroundColor(Color.parseColor("#212121"));
                ((TextView)view).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView)view).setTextColor(Color.WHITE);
            }
        });



        interventionsList.setAdapter(interventionsAdapter);


        loadInterventions();

        return view;
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

    public interface OnFragmentInteractionListener {

        // When button creation intervention clicked
        void handleInterventionCreation();

        // when an interventions is selected
        void handleInterventionSelected(Intervention intervention);
    }

    private boolean isCodis(){
        Credentials credentials = ((TdfApplication)this.getActivity().getApplication()).loadCredentials();
        return credentials.isCodisUser();
    }

    public void loadInterventions(){
        interventions.clear();
        interventionArrayList.clear();

        // TODO: 27/04/16 bouchon à enlever
        Intervention interventionBouchon = new Intervention();
        interventionBouchon.setName("Bouchon");
        interventionBouchon.setCreationDate(new Date());
        interventionBouchon.setSinisterCode(SinisterCode.FDF);
        interventionBouchon.setLocation(new Location());
        interventionArrayList.add(interventionBouchon);

        interventionsAdapter.notifyDataSetChanged();

        interventionDao.findAll(new DaoSelectionParameters(), new IDaoSelectReturnHandler<List<Intervention>>() {
            @Override
            public void onRepositoryResult(List<Intervention> interventionList) {
            }

            @Override
            public void onRestResult(List<Intervention> interventionList) {
                Iterator<Intervention> interventionIterator = interventionList.iterator();
                Intervention intervention;
                while (interventionIterator.hasNext()) {
                    intervention = interventionIterator.next();
                    interventionArrayList.add(intervention);
                }

                addSortedInterventions();
            }

            @Override
            public void onRepositoryFailure(Throwable e) {
                Log.e("", "REPO FAILURE");
            }

            @Override
            public void onRestFailure(Throwable e) {
                Log.e("", "REST FAILURE");
            }
        });
    }

    private void addSortedInterventions(){
        Collections.sort(interventionArrayList, new Comparator<Intervention>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return -1 iff the first element is smaller than the second one
             * 1 iff the second element is smaller than the first one
             * 0 iff the two elements are equals
             */
            @Override
            public int compare(Intervention lhs, Intervention rhs) {

                //compare archived or not
                boolean archived1 = lhs.isArchived();
                boolean archived2 = lhs.isArchived();

                if (archived1&&!archived2){
                    return -1;
                }

                if(!archived1&&archived2){
                    return 1;
                }

                //compare date
                Date date1 = lhs.getCreationDate();
                Date date2 = rhs.getCreationDate();

                return date2.compareTo(date1);
            }
        });

        Iterator<Intervention> it = interventionArrayList.iterator();
        Intervention intervention;
        while(it.hasNext())
        {
            intervention = it.next();
            interventions.add(intervention.getName());
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interventionsAdapter.notifyDataSetChanged();
            }
        });
        // TODO: 27/04/16 en bleu le selected pour alexandre
        // TODO: 27/04/16 que ca selectionne le premier ?
    }
}
