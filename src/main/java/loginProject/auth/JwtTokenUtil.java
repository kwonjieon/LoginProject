package loginProject.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    //jwt 토큰 발급
    public static String createToken(String loginId,String key,Long expireTimeMs){
        //claim=jwt token에 들어갈정보
        //claim에 loginId를 넣어줌으로써 나중에 loginId를 꺼낼수있음
        Claims claims = Jwts.claims();
        claims.put("loginId",loginId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expireTimeMs))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
    }
    //claim에서 loginId 꺼내기
    public static String getLoginId(String token,String secretKey){
        return extractClaims(token,secretKey).get("loginId").toString();
    }
    //발급된 token이 만료되었는지 확인
    public static boolean isExpired(String token,String secretKey){
        Date expiredDate = extractClaims(token,secretKey).getExpiration();
        //token만료나짜가 지금보다 이전인지 체크
        return expiredDate.before(new Date());
    }
    //secretKey를 사용해 token parsing
    public static Claims extractClaims(String token,String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
