package com.tjazi.profilesauthenticator.client;

import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseMessage;

/**
 * Created by Krzysztof Wasiak on 07/11/2015.
 */
public interface ProfilesAuthenticatorClient {

    /**
     * Authenticate profile
     * @param userNameEmail User name or email - authentication is possible using both
     * @param passwordHash Password hash
     * @return Message with authentication details
     */
    AuthenticateProfileResponseMessage authenticateProfile(String userNameEmail, String passwordHash);
}
