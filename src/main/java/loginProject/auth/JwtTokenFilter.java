package loginProject.auth;

import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지않음
        if(authorizationHeader==null){
            filterChain.doFilter(request,response);
            return;
        }
        //Header의 Authorization값이 Bearer로 시작하지않으면 => 잘못된 토큰
        if(!authorizationHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        //전송받은 값에서 'Bearer' 뒷부분 (Jwt Token) 추출
        String token = authorizationHeader.split(" ")[1];

        //전송받은 Jwt Token이 만료되었으면 => 다음필터로 진행(인증x)
        if(JwtTokenUtil.isExpired(token,secretKey)){
            filterChain.doFilter(request,response);
            return ;
        }

        //Jwt Token 에서 loginId 추출
        String loginId = JwtTokenUtil.getLoginId(token,secretKey);

        //추출한 loginId로 User 찾아오기
        User loginUser = userService.getLoginUserByLoginId(loginId);

        //loginUser정보로 UserpasswordAuthenticationTOken발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser.getLoginId(),null, List.of(new SimpleGrantedAuthority(loginUser.getRole().name())));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //권한부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);

    }
}
