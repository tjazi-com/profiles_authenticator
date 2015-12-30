package com.tjazi.profilesauthenticator.service.core;

import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;

/**
 * Created by Krzysztof Wasiak on 08/11/2015.
 *
 * Core functionality, which is responsible for authenticating the user profile
 */
public interface ProfilesAuthenticator {

    /**
     * Authenticate profile specified in the given message
     * @param requestMessage authentication request message
     * @return Authorization token (null - if authorization has failed)
     */
    String authenticateProfile(AuthenticateProfileRequestMessage requestMessage);
}
