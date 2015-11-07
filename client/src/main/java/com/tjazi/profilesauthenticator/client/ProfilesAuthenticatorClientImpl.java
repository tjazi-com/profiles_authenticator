package com.tjazi.profilesauthenticator.client;

import com.tjazi.lib.messaging.rest.RestClient;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Krzysztof Wasiak on 07/11/2015.
 */
public class ProfilesAuthenticatorClientImpl implements ProfilesAuthenticatorClient {

    private final static Logger log = LoggerFactory.getLogger(ProfilesAuthenticatorClientImpl.class);

    private RestClient restClient;

    public ProfilesAuthenticatorClientImpl(RestClient restClient) {

        if (restClient == null) {
            String errorMessage = "restClient is null";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        this.restClient = restClient;
    }

    @Override
    public AuthenticateProfileResponseMessage authenticateProfile(String userNameEmail, String passwordHash) {

        if (userNameEmail == null || userNameEmail.isEmpty()) {
            String errorMessage = "userNameEmail is null or empty";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (passwordHash == null || passwordHash.isEmpty()) {
            String errorMessage = "passwordHash is null or empty";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setUserNameEmail(userNameEmail);
        requestMessage.setPasswordHash(passwordHash);

        return (AuthenticateProfileResponseMessage) restClient.sendRequestGetResponse(
                requestMessage, AuthenticateProfileResponseMessage.class);
    }
}
