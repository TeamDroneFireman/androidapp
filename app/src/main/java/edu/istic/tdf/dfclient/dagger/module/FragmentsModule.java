package edu.istic.tdf.dfclient.dagger.module;

import dagger.Module;
import dagger.Provides;
import edu.istic.tdf.dfclient.fragment.LoginFragment;
import edu.istic.tdf.dfclient.rest.service.login.LoginRestService;

/**
 * Created by maxime on 24/04/2016.
 *
 * Dagger modules for injecting fragments with their dependencies
 */
@Module
public class FragmentsModule {

    @Provides
    LoginFragment provideLoginFragment(LoginRestService loginRestService) {
        return LoginFragment.newInstance(loginRestService);
    }
}