package edu.istic.tdf.dfclient.dao.domain.element;

import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import java.util.ArrayList;
import java.util.List;

import edu.istic.tdf.dfclient.dao.Dao;
import edu.istic.tdf.dfclient.dao.DaoSelectionParameters;
import edu.istic.tdf.dfclient.dao.handler.IDaoSelectReturnHandler;
import edu.istic.tdf.dfclient.database.IDbReturnHandler;
import edu.istic.tdf.dfclient.domain.Entity;
import edu.istic.tdf.dfclient.repository.Repository;
import edu.istic.tdf.dfclient.rest.RestClient;
import edu.istic.tdf.dfclient.rest.domain.element.ElementRestClient;
import edu.istic.tdf.dfclient.rest.handler.IRestReturnHandler;

/**
 * The DAO for an Element
 * @param <E> Element entity class
 * @param <R> Repository associated to the element
 * @param <C> The Rest Client associated to the element
 */
public abstract class ElementDao<E extends Entity, R extends Repository<E>, C extends ElementRestClient<E>> extends Dao<E,R,C> {
    public ElementDao(R repository, C restClient) {
        super(repository, restClient);
    }

    /**
     * Finds a collection of elements from an intervention
     * @param interventionId The id of the intervention to get elements from
     * @param selectionParameters The selection parameters, such as limit or order by
     * @param handler The handler called at return
     */
    public void findByIntervention(String interventionId, DaoSelectionParameters selectionParameters, final IDaoSelectReturnHandler<List<E>> handler) {
        // Make a first query on local data
        try {
            // TODO : Filter !!
            repository.findAll(selectionParameters.getLimit(), selectionParameters.getOffset(), new IDbReturnHandler<List<E>>() {
                @Override
                public void onSuccess(List<E> r) {
                    handler.onRepositoryResult(r);
                }

                @Override
                public void onError(Throwable error) {
                    handler.onRepositoryFailure(error);
                }
            });

        } catch(InvalidDBConfiguration e) { // TODO : More fine exception
            handler.onRepositoryFailure(e);
        }

        // Get data from REST service
        restClient.findByIntervention(interventionId, selectionParameters, new IRestReturnHandler<ArrayList<E>>() {
            @Override
            public void onSuccess(ArrayList<E> result) {
                for (E e : result) {
                    repository.persist(e);
                }
                handler.onRestResult(result);
            }

            @Override
            public void onError(Throwable error) {
                handler.onRestFailure(error);
            }
        });
    }
}
