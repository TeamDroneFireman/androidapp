package edu.istic.tdf.dfclient.rest.service.login.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maxime on 22/04/2016.
 */
public class LoginResponse {
    /**
     * Id of the user
     */
    String userId;

    /**
     * Login token
     */
    @SerializedName("id")
    String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
