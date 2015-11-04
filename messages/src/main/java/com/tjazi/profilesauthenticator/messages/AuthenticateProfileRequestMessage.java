package com.tjazi.profilesauthenticator.messages;

/**
 * Created by Krzysztof Wasiak on 04/11/2015.
 */
public class AuthenticateProfileRequestMessage {

    /**
     * User can authenticate by using either user name or email.
     * This field can store either.
     */
    private String userNameEmail;

    /**
     * Password hash
     */
    private String passwordHash;

    public String getUserNameEmail() {
        return userNameEmail;
    }

    public void setUserNameEmail(String userNameEmail) {
        this.userNameEmail = userNameEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
