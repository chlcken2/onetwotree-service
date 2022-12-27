package HelloMyTeam.Hellomyteam.security.oauth2;


import HelloMyTeam.Hellomyteam.dto.MemberDTO;
import HelloMyTeam.Hellomyteam.entity.Member;
import HelloMyTeam.Hellomyteam.exception.OAuth2AuthenticationProcessingException;
import HelloMyTeam.Hellomyteam.security.UserPrincipal;
import HelloMyTeam.Hellomyteam.security.oauth2.user.OAuth2UserInfo;
import HelloMyTeam.Hellomyteam.security.oauth2.user.OAuth2UserInfoFactory;
import HelloMyTeam.Hellomyteam.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<Member> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        Member member;
        if(!userOptional.isPresent()) {
            member = updateExistingUser(userOptional.get(), oAuth2UserInfo);
        } else {
            member = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(member, oAuth2User.getAttributes());
    }
    private Member registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .build();
        return userRepository.save(member);
    }
    private Member updateExistingUser(Member existingUser, OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.save(existingUser);
    }
}
