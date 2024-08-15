package loginProject.domain.dto;

import loginProject.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class JoinForm {
    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String loginId;
    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    @NotBlank(message = "닉네임이 비어있습니다.")
    private String nickname;
    public User toEntity(){
        return User.builder()
                .loginId(this.loginId)
                .password(this.password)
                .nickname(this.nickname)
                .build();


    }

}
