package com.tjazi.profilesauthenticator.client;

import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Krzysztof Wasiak on 07/11/2015.
 */
public class ProfilesAuthenticatorClientImpl implements ProfilesAuthenticatorClient {

    private final static Logger log = LoggerFactory.getLogger(ProfilesAuthenticatorClientImpl.class);

    private RestTemplate restTemplate;

    private final static String PROFILES_AUTHENTICATOR_SERVICE_NAME = "profiles-authenticator-service-core";
    private final static String AUTHENTICATE_PROFILE_PATH = "http://" + PROFILES_AUTHENTICATOR_SERVICE_NAME + "/authenticator/authenticate";

    @Override
    public String authenticateProfile(String userNameEmail, String passwordHash) {

        log.debug("[ProfilesAuthenticatorClient] Got Authenticate Profile request. userNameEmail: '{}', passwordHash: '{}'",
                userNameEmail, passwordHash);

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

        return restTemplate.postForObject(AUTHENTICATE_PROFILE_PATH,
                requestMessage, String.class, (Object) null);
    }
}
