package hello.jdbc.domain;

import lombok.Data;

@Data
//@Data = @toString + @getter + @setter + @RequiredArgsConstructor + @EqualsAndHashCode
public class Member {
//회원의 ID와 해당 회원이 소지한 금액을 표현하는 단순한 클래스, 앞서 만들어둔 member 테이블에 데이터를 저장하고 조회할 때 사용

    private String memberId;
    private int money;

    public Member(){

    }
    public Member(String memberId, int money){
        this.memberId=memberId;
        this.money=money;
    }
}
