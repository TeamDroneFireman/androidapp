package edu.istic.tdf.dfclient.rest.domain;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.istic.tdf.dfclient.dao.DaoSelectionParameters;
import edu.istic.tdf.dfclient.domain.intervention.Intervention;
import edu.istic.tdf.dfclient.http.TdfHttpClient;
import edu.istic.tdf.dfclient.http.configuration.TdfHttpClientConf;
import edu.istic.tdf.dfclient.http.handler.RestHttpResponseHandler;
import edu.istic.tdf.dfclient.push.PushSubscriptionData;
import edu.istic.tdf.dfclient.rest.IRestClient;
import edu.istic.tdf.dfclient.rest.RestClient;
import edu.istic.tdf.dfclient.rest.RestEndpoints;
import edu.istic.tdf.dfclient.rest.handler.IRestReturnHandler;
import edu.istic.tdf.dfclient.rest.serializer.ArrayListParameterizedType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * The REST client for Interventions
 */
public class InterventionRestClient extends RestClient<Intervention> implements IRestClient<Intervention> {

    protected static final String TAG = "InterventionRestClient";

    /**
     * The Push subscription endpoint URL
     */
    private final static String SUBSCRIPTION_URL = "register/";

    /**
     * A GSON Serializer
     */
    private Gson gson;

    public InterventionRestClient(TdfHttpClient httpClient, Gson gson) {
        super(Intervention.class, httpClient);
        this.gson = gson;
        httpClient.setConf(new TdfHttpClientConf(RestEndpoints.Intervention));
    }

    /**
     * Makes the REST request to subscribe to an intervention
     * @param registrationId The registrationId to subscribe
     * @param intervention The intervention to subscribe to
     */
    public void subscribe(final String registrationId, final Intervention intervention) {
        PushSubscriptionData data = new PushSubscriptionData();
        data.setRegistrationId(registrationId);
        data.setInterventionId(intervention.getId());

        this.httpClient.post(SUBSCRIPTION_URL, gson.toJson(data), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error when subscribing to intervention [" + intervention.getId() + "] with registrationId [" + registrationId + "]");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "Subscribed to intervention [" + intervention.getId() + "] with registrationId [" + registrationId + "]");
            }
        });
    }

    /**
     * Finds all elements by intervention id
     */
    public void findInterventionsWithAskedElement( DaoSelectionParameters selectionParameters,
                                   final IRestReturnHandler<ArrayList<Intervention>> callback) {
        // Query filters
        HashMap<String, String> queryParameters = selectionParameters.getFilters();

        // Response handler
        RestHttpResponseHandler<ArrayList<Intervention>> handler =
                new RestHttpResponseHandler<>(callback, new ArrayListParameterizedType(entityClass));

        // Make request
        httpClient.get(getResourceUri("element/notValidate"), queryParameters, new HashMap<String, String>(), handler);
    }
}
