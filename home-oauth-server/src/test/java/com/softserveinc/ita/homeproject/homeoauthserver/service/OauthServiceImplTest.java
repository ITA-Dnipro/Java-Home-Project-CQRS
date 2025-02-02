package com.softserveinc.ita.homeproject.homeoauthserver.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import com.softserveinc.ita.homeproject.homeoauthserver.config.JwtProvider;
import com.softserveinc.ita.homeproject.homeoauthserver.data.UserCredentials;
import com.softserveinc.ita.homeproject.homeoauthserver.data.UserCredentialsRepository;
import com.softserveinc.ita.homeproject.homeoauthserver.data.UserSession;
import com.softserveinc.ita.homeproject.homeoauthserver.data.UserSessionRepository;
import com.softserveinc.ita.homeproject.homeoauthserver.dto.AccessTokenDto;
import com.softserveinc.ita.homeproject.homeoauthserver.dto.CreateTokenDto;
import com.softserveinc.ita.homeproject.homeoauthserver.dto.RefreshTokenDto;
import com.softserveinc.ita.homeproject.homeoauthserver.dto.UserCredentialsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class OauthServiceImplTest {

    @Mock
    private static UserSessionRepository userSessionRepository;

    @Mock
    private static JwtProvider jwtProvider;

    @Mock
    private static UserCredentialsRepository userCredentialsRepository;

    @InjectMocks
    private OauthServiceImpl oauthService;

    @Test
    void generateTokenShouldReturnTokens() {

        UserCredentialsDto userCredentialsDto = new UserCredentialsDto();
        CreateTokenDto expectedCreateTokenDto = new CreateTokenDto();
        UserCredentials userCredentials = new UserCredentials();
        UserSession userSession = new UserSession();
        userCredentialsDto.setEmail("test@gmail.com");
        userCredentialsDto.setPassword("password");
        expectedCreateTokenDto.setAccessToken("123");
        expectedCreateTokenDto.setRefreshToken("456");
        userCredentials.setId(1000L);
        userCredentials.setEmail("test@gmail.com");
        userCredentials.setPassword("$2a$10$Vb7wd6xqSJ3A8LbHJgb12eeak3ptYgMFPYQNoBlSog.X5eMMXaQK2");
        userCredentials.setEnabled(true);
        userSession.setUserId(userCredentials.getId());
        userSession.setAccessToken(expectedCreateTokenDto.getAccessToken());
        userSession.setRefreshToken(expectedCreateTokenDto.getRefreshToken());
        userSession.setExpireDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        when(userCredentialsRepository.findByEmail("test@gmail.com"))
            .thenReturn(Optional.of(userCredentials));
        when(jwtProvider.generateToken(1000L, "test@gmail.com", 1))
            .thenReturn(("123"));
        when(jwtProvider.generateToken(1000L, "test@gmail.com", 7))
            .thenReturn(("456"));
        when(jwtProvider.getDate()).thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));

        CreateTokenDto actualCreateTokenDto = oauthService.generateToken(userCredentialsDto);

        assertThat(actualCreateTokenDto).usingRecursiveComparison().isEqualTo(expectedCreateTokenDto);
    }

    @Test
    void updateTokenShouldReturnToken(){
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto();
        UserSession userSession = new UserSession();
        refreshTokenDto.setRefreshToken("456");
        String accessToken = "123";
        AccessTokenDto expectedAccessTokenDto = new AccessTokenDto();
        expectedAccessTokenDto.setAccessToken(accessToken);
        userSession.setAccessToken(accessToken);
        userSession.setId(1000L);

        when(userSessionRepository.findByRefreshToken("456"))
            .thenReturn(Optional.of(userSession));
        when(jwtProvider.getEmailFromToken("456")).thenReturn("test@gmail.com");
        doNothing().when(jwtProvider).validateToken(refreshTokenDto.getRefreshToken());
        when(jwtProvider.generateToken(1000L, "test@gmail.com", 1))
            .thenReturn((accessToken));

        AccessTokenDto actualAccessTokenDto = oauthService.updateToken(refreshTokenDto);

        verify(userSessionRepository, times(1)).save(userSession);

        assertThat(actualAccessTokenDto).usingRecursiveComparison().isEqualTo(expectedAccessTokenDto);
    }

}