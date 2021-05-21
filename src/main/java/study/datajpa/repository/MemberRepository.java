package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

//    @Query(name = "Member.findByUsername") 생략가능  NamedQuery가 우선순위 1순위 이고 NamedQuery가 없다면 메소드 이름으로 쿼리가 생성된다
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age") //(이름이 없는 NamedQuery)애플리케이션 실행시점에 오류가 난다
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
}
