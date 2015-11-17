package unittests;

import com.tjazi.profiles.client.ProfilesClient;
import com.tjazi.profiles.messages.GetProfileDetailsByUserNameEmailResponseMessage;
import com.tjazi.profiles.messages.GetProfileDetailsByUserNameEmailResponseStatus;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileRequestMessage;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseMessage;
import com.tjazi.profilesauthenticator.messages.AuthenticateProfileResponseStatus;
import com.tjazi.profilesauthenticator.service.core.ProfilesAuthenticatorImpl;
import com.tjazi.profilesauthorizer.client.ProfilesAuthorizerClient;
import com.tjazi.profilesauthorizer.messages.CreateNewAuthorizationTokenResponseMessage;
import com.tjazi.profilesauthorizer.messages.CreateNewAuthorizationTokenResponseStatus;
import com.tjazi.security.client.SecurityClient;
import com.tjazi.security.messages.UserAuthenticationResponseMessage;
import com.tjazi.security.messages.UserAuthenticationResponseStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by Krzysztof Wasiak on 08/11/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProfilesAuthenticator_Tests {

    @Mock
    public ProfilesClient profilesClient;

    @Mock
    public SecurityClient securityClient;

    @Mock
    public ProfilesAuthorizerClient profilesAuthorizerClient;

    @InjectMocks
    public ProfilesAuthenticatorImpl profilesAuthenticator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void authenticateProfile_ExceptionOnNullInputMessage_Test() {
        thrown.expect(IllegalArgumentException.class);

        profilesAuthenticator.authenticateProfile(null);
    }

    @Test
    public void authenticateProfile_ExceptionOnNullUserNameEmail_Test() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("requestMessage.UserNameEmail is null or empty");

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setPasswordHash("password hash");
        requestMessage.setUserNameEmail(null);

        profilesAuthenticator.authenticateProfile(requestMessage);
    }

    @Test
    public void authenticateProfile_ExceptionOnNullPasswordHash_Test() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("requestMessage.PasswordHash is null or empty");

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setPasswordHash(null);
        requestMessage.setUserNameEmail("user name / email");

        profilesAuthenticator.authenticateProfile(requestMessage);
    }

    @Test
    public void authenticateProfile_RequestProfileData_NoneFound_Test() {

        final String userNameEmail = "sample user name / email";
        final String passwordHash = "sample password hash";

        final GetProfileDetailsByUserNameEmailResponseMessage profileResponseMessage =
                new GetProfileDetailsByUserNameEmailResponseMessage();

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setUserNameEmail(userNameEmail);
        requestMessage.setPasswordHash(passwordHash);

        profileResponseMessage.setResponseStatus(GetProfileDetailsByUserNameEmailResponseStatus.PROFILE_NOT_FOUND);

        when(profilesClient.getProfileDetailsByUserNameEmail(userNameEmail))
                .thenReturn(profileResponseMessage);

        // main method call
        AuthenticateProfileResponseMessage responseMessage = profilesAuthenticator.authenticateProfile(requestMessage);

        // assertion and verification
        verify(profilesClient, times(1)).getProfileDetailsByUserNameEmail(userNameEmail);

        assertEquals(AuthenticateProfileResponseStatus.USER_PROFILE_NOT_FOUND_BY_USER_NAME_OR_EMAIL,
                responseMessage.getResponseStatus());
        assertNull(responseMessage.getAuthorizationToken());
    }

    @Test
    public void authenticateProfile_RequestProfileData_GeneralError_Test() {

        final String userNameEmail = "sample user name / email";
        final String passwordHash = "sample password hash";

        final GetProfileDetailsByUserNameEmailResponseMessage profileResponseMessage =
                new GetProfileDetailsByUserNameEmailResponseMessage();

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setUserNameEmail(userNameEmail);
        requestMessage.setPasswordHash(passwordHash);

        profileResponseMessage.setResponseStatus(GetProfileDetailsByUserNameEmailResponseStatus.GENERAL_ERROR);

        when(profilesClient.getProfileDetailsByUserNameEmail(userNameEmail))
                .thenReturn(profileResponseMessage);

        // main method call
        AuthenticateProfileResponseMessage responseMessage = profilesAuthenticator.authenticateProfile(requestMessage);

        // assertion and verification
        verify(profilesClient, times(1)).getProfileDetailsByUserNameEmail(userNameEmail);

        assertEquals(AuthenticateProfileResponseStatus.GENERAL_ERROR,
                responseMessage.getResponseStatus());
        assertNull(responseMessage.getAuthorizationToken());
    }

    @Test
    public void authenticateProfile_RequestProfileData_GotSingleProfile_WrongPassword_Test() {

        final String userNameEmail = "sample user name / email";
        final String passwordHash = "sample password hash";
        final UUID profileUuid = UUID.randomUUID();

        // messages ment to be sent insite main method by mocks
        final GetProfileDetailsByUserNameEmailResponseMessage profileResponseMessage =
                new GetProfileDetailsByUserNameEmailResponseMessage();
        profileResponseMessage.setProfileUuid(profileUuid);
        profileResponseMessage.setResponseStatus(GetProfileDetailsByUserNameEmailResponseStatus.OK);

        UserAuthenticationResponseMessage securityUsertAuthenticationResponse = new UserAuthenticationResponseMessage();
        securityUsertAuthenticationResponse.setAuthenticationResponseStatus(UserAuthenticationResponseStatus.WRONG_PASSWORD);

        // </ messages ment to be sent inside main method by mocks>

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setUserNameEmail(userNameEmail);
        requestMessage.setPasswordHash(passwordHash);

        when(profilesClient.getProfileDetailsByUserNameEmail(userNameEmail))
                .thenReturn(profileResponseMessage);

        when(securityClient.authenticateUser(profileUuid, passwordHash))
                .thenReturn(securityUsertAuthenticationResponse);

        // main method call
        AuthenticateProfileResponseMessage responseMessage = profilesAuthenticator.authenticateProfile(requestMessage);

        // assertion and verification
        verify(profilesClient, times(1)).getProfileDetailsByUserNameEmail(userNameEmail);

        assertEquals(AuthenticateProfileResponseStatus.WRONG_PASSWORD,
                responseMessage.getResponseStatus());
        assertNull(responseMessage.getAuthorizationToken());
    }

    @Test
    public void authenticateProfile_RequestProfileData_GotSingleProfile_AuthenticationSuccessful_Test() {

        final String userNameEmail = "sample user name / email";
        final String passwordHash = "sample password hash";
        final UUID profileUuid = UUID.randomUUID();
        final String authorizationToken = UUID.randomUUID().toString();

        // messages ment to be sent insite main method by mocks
        final GetProfileDetailsByUserNameEmailResponseMessage profileResponseMessage =
                new GetProfileDetailsByUserNameEmailResponseMessage();
        profileResponseMessage.setProfileUuid(profileUuid);
        profileResponseMessage.setResponseStatus(GetProfileDetailsByUserNameEmailResponseStatus.OK);

        UserAuthenticationResponseMessage securityUserAuthenticationResponse = new UserAuthenticationResponseMessage();
        securityUserAuthenticationResponse.setAuthenticationResponseStatus(UserAuthenticationResponseStatus.OK);

        CreateNewAuthorizationTokenResponseMessage createNewAuthorizationTokenResponseMessage = new CreateNewAuthorizationTokenResponseMessage();
        createNewAuthorizationTokenResponseMessage.setResponseStatus(CreateNewAuthorizationTokenResponseStatus.OK);
        createNewAuthorizationTokenResponseMessage.setAuthorizationToken(authorizationToken);

        // </ messages ment to be sent inside main method by mocks>

        AuthenticateProfileRequestMessage requestMessage = new AuthenticateProfileRequestMessage();
        requestMessage.setUserNameEmail(userNameEmail);
        requestMessage.setPasswordHash(passwordHash);

        when(profilesClient.getProfileDetailsByUserNameEmail(userNameEmail))
                .thenReturn(profileResponseMessage);

        when(securityClient.authenticateUser(profileUuid, passwordHash))
                .thenReturn(securityUserAuthenticationResponse);

        when(profilesAuthorizerClient.createNewAuthorizationToken(profileUuid))
                .thenReturn(createNewAuthorizationTokenResponseMessage);

        // main method call
        AuthenticateProfileResponseMessage responseMessage = profilesAuthenticator.authenticateProfile(requestMessage);

        // assertion and verification
        verify(profilesClient, times(1)).getProfileDetailsByUserNameEmail(userNameEmail);
        verify(securityClient, times(1)).authenticateUser(profileUuid, passwordHash);
        verify(profilesAuthorizerClient, times(1)).createNewAuthorizationToken(profileUuid);

        assertEquals(AuthenticateProfileResponseStatus.OK, responseMessage.getResponseStatus());
        assertEquals(authorizationToken, responseMessage.getAuthorizationToken());
    }
}
