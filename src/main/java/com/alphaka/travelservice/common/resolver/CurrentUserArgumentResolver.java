package com.alphaka.travelservice.common.resolver;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.exception.custom.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 현재 사용자 정보를 헤더에서 추출하여 CurrentUser 객체로 주입하는 Resolver
 */
@Slf4j
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return CurrentUser.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * 요청 헤더에서 사용자 정보를 추출하여 CurrentUser 객체로 반환
     * @param parameter - 파라미터 정보
     * @param mavContainer - 현재 요청과 뷰에 대한 모델을 포함하는 컨테이너
     * @param webRequest - 현재 요청 정보
     * @param binderFactory - 요청 데이터를 바인딩하는 데 사용되는 바인더 팩토리
     * @return CurrentUser - 현재 사용자 객체
     */
    @Override
    public CurrentUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                       NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        // 헤더에서 사용자 정보 추출
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String nicknameHeader = request.getHeader("X-User-Nickname");
        String profileImageHeader = request.getHeader("X-User-Profile");

        log.debug("Received headers - X-User-Id: {}, X-User-Role: {}, X-User-Nickname: {}, X-User-Profile: {}",
                userIdHeader, roleHeader, nicknameHeader, profileImageHeader);

        // 로그인 상태가 아닌 경우 null 반환
        if (userIdHeader == null || roleHeader == null || nicknameHeader == null || profileImageHeader == null) {
            log.info("비로그인 상태입니다.");
            return null;
        }

        try {
            Long userId = Long.valueOf(userIdHeader);
            return new CurrentUser(userId, nicknameHeader, profileImageHeader, roleHeader);
        } catch (NumberFormatException e) {
            log.error("잘못된 사용자 ID 타입. Required: {}, Provided: {}", Long.class, userIdHeader.getClass());
            throw new UnauthorizedException();
        }
    }
}
