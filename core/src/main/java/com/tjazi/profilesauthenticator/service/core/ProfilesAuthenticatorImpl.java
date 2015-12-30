package com.tjazi.profilesauthenticator.service.core;

import com.tjazi.profiles.client.ProfilesClient;
import com.tjazi.profiles.messages.GetProfileDetailsByUserNameEmailResponseMessage;
import com.tjazi.profiles.messages.GetProfileDetailsByUserNameEmailResponseStatus;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;
import com.tjazi.profilesauthorizer.client.ProfilesAuthorizerClient;
import com.tjazi.security.client.SecurityClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Krzysztof Wasiak on 08/11/2015.
 */

@Service
public class ProfilesAuthenticatorImpl implements ProfilesAuthenticator {

    private final static Logger log = LoggerFactory.getLogger(ProfilesAuthenticatorImpl.class);

    @Autowired
    private ProfilesClient profilesClient;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private ProfilesAuthorizerClient profilesAuthorizerClient;

    @Override
    public String authenticateProfile(AuthenticateProfileRequestMessage requestMessage) {

        this.assertRequestMessage(requestMessage);

        GetProfileDetailsByUserNameEmailResponseMessage profileDetailsByUserNameEmail =
                profilesClient.getProfileDetailsByUserNameEmail(requestMessage.getUserNameEmail());

        GetProfileDetailsByUserNameEmailResponseStatus profileDetailsResponse = profileDetailsByUserNameEmail.getResponseStatus();

        if (profileDetailsResponse != GetProfileDetailsByUserNameEmailResponseStatus.OK) {
            log.error("Got {} status from the profiles-core service; reporting authentication process as failed", profileDetailsResponse);
            return null;
        }

        UUID profileUuid = profileDetailsByUserNameEmail.getProfileUuid();

        boolean authenticationResult =
                securityClient.authenticateUser(profileUuid, requestMessage.getPasswordHash());

        if (!authenticationResult) {
            log.error("Authentication at security-service-core has failed; reporting authentication process as failed");
            return null;
        }

        String authorizationToken = this.generateAuthorizationToken();

        // stage 3: save authorization token
        boolean authorizationTokenPersistenceResult =
                profilesAuthorizerClient.saveAuthorizationToken(profileUuid, authorizationToken);

        if (!authorizationTokenPersistenceResult) {
            log.error("Persistence of the authorization token has failed; reporting authentication process as failed");
            return null;
        }

        log.debug("Authentication process for profile UUID: {} succeeded; new authorization token: {}",
                profileUuid, authorizationToken);
        return authorizationToken;
    }

    private void assertRequestMessage(AuthenticateProfileRequestMessage requestMessage) {

        if (requestMessage == null) {
            String errorMessage = "requestMessage is null";

            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }

        String userNameEmail = requestMessage.getUserNameEmail();

        if (userNameEmail == null || userNameEmail.isEmpty()) {
            String errorMessage = "requestMessage.UserNameEmail is null or empty";

            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }

        String passwordHash = requestMessage.getPasswordHash();

        if (passwordHash == null || passwordHash.isEmpty()) {
            String errorMessage = "requestMessage.PasswordHash is null or empty";

            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
    }

    private String generateAuthorizationToken() {
        return UUID.randomUUID().toString();
    }
}
