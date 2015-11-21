package com.tjazi.profilesauthenticator.service.core;

import com.tjazi.profiles.client.ProfilesClient;
import com.tjazi.profiles.messages.GetProfileDetailsByUserNameEmailResponseMessage;
import com.tjazi.profiles.messages.GetProfileDetailsByUserNameEmailResponseStatus;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseMessage;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseStatus;
import com.tjazi.profilesauthorizer.client.ProfilesAuthorizerClient;
import com.tjazi.profilesauthorizer.messages.CreateNewAuthorizationTokenResponseMessage;
import com.tjazi.profilesauthorizer.messages.CreateNewAuthorizationTokenResponseStatus;
import com.tjazi.security.client.SecurityClient;
import com.tjazi.security.messages.UserAuthenticationResponseMessage;
import com.tjazi.security.messages.UserAuthenticationResponseStatus;
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
    public AuthenticateProfileResponseMessage authenticateProfile(AuthenticateProfileRequestMessage requestMessage) {

        this.validateRequestMessage(requestMessage);

        GetProfileDetailsByUserNameEmailResponseMessage profileDetailsByUserNameEmail =
                profilesClient.getProfileDetailsByUserNameEmail(requestMessage.getUserNameEmail());

        GetProfileDetailsByUserNameEmailResponseStatus profileDetailsResponse = profileDetailsByUserNameEmail.getResponseStatus();
        AuthenticateProfileResponseMessage responseMessage = new AuthenticateProfileResponseMessage();

        switch (profileDetailsResponse) {
            case PROFILE_NOT_FOUND:
                responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.USER_PROFILE_NOT_FOUND_BY_USER_NAME_OR_EMAIL);
                return responseMessage;

            case GENERAL_ERROR:
                responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.GENERAL_ERROR);
                return responseMessage;

            case OK:
                return this.authenticateProfile(
                        profileDetailsByUserNameEmail.getProfileUuid(),
                        requestMessage.getPasswordHash());

            default:
                String errorMessage = "Got GetProfileDetailsByUserNameEmailResponseStatus of unknown value: " + profileDetailsResponse;
                log.error(errorMessage);
                throw new Error(errorMessage);
        }
    }

    private AuthenticateProfileResponseMessage authenticateProfile(UUID profileUuid, String passwordHash) {

        UserAuthenticationResponseMessage userAuthenticationResult = securityClient.authenticateUser(profileUuid, passwordHash);

        UserAuthenticationResponseStatus securityAuthenticationStatus = userAuthenticationResult.getAuthenticationResponseStatus();

        switch (securityAuthenticationStatus) {
            case WRONG_PASSWORD:
                AuthenticateProfileResponseMessage responseMessage = new AuthenticateProfileResponseMessage();
                responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.WRONG_PASSWORD);
                return responseMessage;

            case OK:
                return this.createAuthorizationToken(profileUuid);

            default:
                String errorMessage = "Got UserAuthenticationResponseStatus of unknown value: " + securityAuthenticationStatus;
                log.error(errorMessage);
                throw new Error(errorMessage);
        }
    }

    private AuthenticateProfileResponseMessage createAuthorizationToken(UUID profileUuid) {

        AuthenticateProfileResponseMessage responseMessage = new AuthenticateProfileResponseMessage();

        // proceed with creation of the authorization token
        CreateNewAuthorizationTokenResponseMessage authorizationTokenResultMessage =
                profilesAuthorizerClient.createNewAuthorizationToken(profileUuid);

        if (authorizationTokenResultMessage.getResponseStatus() == CreateNewAuthorizationTokenResponseStatus.OK) {

            responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.OK);
            responseMessage.setAuthorizationToken(authorizationTokenResultMessage.getAuthorizationToken());

            return responseMessage;
        }

        return responseMessage;
    }

    private void validateRequestMessage(AuthenticateProfileRequestMessage requestMessage) {

        if (requestMessage == null) {
            String errorMessage = "requestMessage is null";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String userNameEmail = requestMessage.getUserNameEmail();

        if (userNameEmail == null || userNameEmail.isEmpty()) {
            String errorMessage = "requestMessage.UserNameEmail is null or empty";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String passwordHash = requestMessage.getPasswordHash();

        if (passwordHash == null || passwordHash.isEmpty()) {
            String errorMessage = "requestMessage.PasswordHash is null or empty";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

    }
}
