package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.domain.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository //스프링빈으로 등록해서 사용
@RequiredArgsConstructor
public class MemberQueryRepository { //이 클래스를 인젝션 받아서 쓰면된다

    private final EntityManager em;

    List<Member> findAllMembers() {
        return em.createQuery("select m from Member m") //쿼리가 복잡하다고 가정
                .getResultList();
    }
}
