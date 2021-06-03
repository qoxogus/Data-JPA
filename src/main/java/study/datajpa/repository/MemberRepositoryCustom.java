package study.datajpa.repository;

import study.datajpa.domain.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
