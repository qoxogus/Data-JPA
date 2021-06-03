package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.domain.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom { //MemberRepository, MemberRepositoryImpl로 이름을 맞춰줘야한다 하지만 구현체를 선언하는 MemberRepositoryCustom은 이름을 맞춰주지 않아도 상관없다

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();

    }
}
