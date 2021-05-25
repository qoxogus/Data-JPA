package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.dto.MemberDto;
import study.datajpa.domain.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    List<Member> findListByUsername(String username); //컬렉션

    Member findMemberByUsername(String username); //단건

    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m") //쿼리가 복잡해지면 카운트 쿼리문을 분리를 해야한다 (성능문제)
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //이 어노테이션이 있어야 excuteUpdate()를 실행한다  (없으면 getResultList() 또는 getSingleResult()를 출력한다)  쿼리가 나가고 클리어를 자동으로 해주는 옵션    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}
