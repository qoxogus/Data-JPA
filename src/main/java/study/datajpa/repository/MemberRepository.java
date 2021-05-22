package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.dto.MemberDto;
import study.datajpa.domain.entity.Member;

import java.util.Collection;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //실무에서 간단한 정적쿼리를 해결할 때 사용

//    @Query(name = "Member.findByUsername") 생략가능  NamedQuery가 우선순위 1순위 이고 NamedQuery가 없다면 메소드 이름으로 쿼리가 생성된다
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age") //(이름이 없는 NamedQuery)애플리케이션 실행시점에 오류가 난다    (실무에서 복잡한 정적쿼리를 해결할때 자주 사용) 동적쿼리는 QueryDSL 사용
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList(); //username 타입이 String 타입이다

    @Query("select new study.datajpa.domain.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);
}
