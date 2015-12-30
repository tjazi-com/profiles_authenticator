package com.tjazi.profilesauthenticator.client;

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
    String authenticateProfile(String userNameEmail, String passwordHash);
}
