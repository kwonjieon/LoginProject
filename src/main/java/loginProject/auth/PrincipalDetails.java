package loginProject.auth;

import loginProject.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user;
    public PrincipalDetails(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collections = new ArrayList<>();
        collections.add(()->{
            return user.getRole().name();
        });
        return collections;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }


    //계정이 만료되었는지 true이면 만료x
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정이 잠겼는지 true이면 잠기지않음
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호가 만료 되었는지 true이면 만료x
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정이 활성화(사용가능)인지 true이면 활성화
    @Override
    public boolean isEnabled() {
        return true;
    }
}
