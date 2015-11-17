package com.tjazi.profilesauthenticator.service.controller;

import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseMessage;
import com.tjazi.profilesauthenticator.service.core.ProfilesAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Krzysztof Wasiak on 10/11/2015.
 */

@RestController
@RequestMapping(value = "/profilesauthenticator")
public class ProfilesAuthenticatorController {

    private static final Logger log = LoggerFactory.getLogger(ProfilesAuthenticatorController.class);

    @Autowired
    private ProfilesAuthenticator profilesAuthenticator;

    @RequestMapping(value = "/authenticateprofile", method = RequestMethod.POST)
    public AuthenticateProfileResponseMessage authenticateProfile(
            @RequestBody AuthenticateProfileRequestMessage authenticateProfileRequestMessage) {

        if (authenticateProfileRequestMessage == null) {
            String errorMessage = "authenticateProfileRequestMessage is null";

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        return profilesAuthenticator.authenticateProfile(authenticateProfileRequestMessage);
    }
}
