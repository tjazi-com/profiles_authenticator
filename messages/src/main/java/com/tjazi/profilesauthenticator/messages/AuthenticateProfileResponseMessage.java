package com.tjazi.profilesauthenticator.messages;

import java.util.UUID;

/**
 * Created by Krzysztof Wasiak on 04/11/2015.
 */
public class AuthenticateProfileResponseMessage {

    /**
     * Status of the authentication
     */
    private AuthenticateProfileResponseStatus responseStatus;

    /**
     * Token, which could be used to authorize all calls against authorizer
     */
    private String authorizationToken;

    public AuthenticateProfileResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(AuthenticateProfileResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
