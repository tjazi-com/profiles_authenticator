package com.tjazi.profilesauthenticator.messages;

/**
 * Created by Krzysztof Wasiak on 04/11/2015.
 */
public enum AuthenticateProfileResponseStatus {

    /**
     * Default. Status is unset
     */
    UNKNOWN,

    /**
     * Authentication successful
     */
    OK,

    /**
     * User name has not been found, when looking by user name or email
     */
    USER_PROFILE_NOT_FOUND_BY_USER_NAME_OR_EMAIL,

    /**
     * The specified password hash doesn't match
     */
    WRONG_PASSWORD,

    /**
     * General error during authentication - usually on either profile or security side
     */
    GENERAL_ERROR
}
