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

import javax.print.DocFlavor;
import java.util.UUID;

/**
 * Created by Krzysztof Wasiak on 08/11/2015.
 */
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

        AuthenticateProfileResponseMessage responseMessage = new AuthenticateProfileResponseMessage();

        GetProfileDetailsByUserNameEmailResponseMessage profileDetailsByUserNameEmail =
                profilesClient.getProfileDetailsByUserNameEmail(userNameEmail);

        if (profileDetailsByUserNameEmail.getResponseStatus() == GetProfileDetailsByUserNameEmailResponseStatus.PROFILE_NOT_FOUND) {
            responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.USER_PROFILE_NOT_FOUND_BY_USER_NAME_OR_EMAIL);

            return responseMessage;
        }

        if (profileDetailsByUserNameEmail.getResponseStatus() == GetProfileDetailsByUserNameEmailResponseStatus.GENERAL_ERROR) {
            responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.GENERAL_ERROR);

            return responseMessage;
        }

        UUID userProfileUuid = profileDetailsByUserNameEmail.getProfileUuid();

        UserAuthenticationResponseMessage userAuthenticationResult = securityClient.authenticateUser(userProfileUuid, passwordHash);

        UserAuthenticationResponseStatus securityAuthenticationStatus = userAuthenticationResult.getAuthenticationResponseStatus();

        if (securityAuthenticationStatus == UserAuthenticationResponseStatus.WRONG_PASSWORD) {
            responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.WRONG_PASSWORD);

            return responseMessage;
        }

        if (securityAuthenticationStatus == UserAuthenticationResponseStatus.OK) {

            // proceed with creation of the authorization token
            CreateNewAuthorizationTokenResponseMessage authorizationTokenResultMessage =
                    profilesAuthorizerClient.createNewAuthorizationToken(userProfileUuid);

            if (authorizationTokenResultMessage.getResponseStatus() == CreateNewAuthorizationTokenResponseStatus.OK) {

                responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.OK);
                responseMessage.setAuthorizationToken(authorizationTokenResultMessage.getAuthorizationToken());

                return responseMessage;
            }
        }

        // if we came as far as here that means we have some general error
        log.error("Unexpected code flow. This line shouldn't be called.");
        responseMessage.setResponseStatus(AuthenticateProfileResponseStatus.GENERAL_ERROR);

        return responseMessage;
    }
}
