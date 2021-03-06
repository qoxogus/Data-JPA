package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.dto.MemberDto;
import study.datajpa.domain.dto.UsernameOnlyDto;
import study.datajpa.domain.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
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

//    @Modifying(clearAutomatically = true) //이 어노테이션이 있어야 excuteUpdate()를 실행한다  (없으면 getResultList() 또는 getSingleResult()를 출력한다)  쿼리가 나가고 클리어를 자동으로 해주는 옵션    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
//    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team") //Member를 조회할때 연관된 Team을 같이 한방쿼리로 다 끌고옴 (프록시로 가져오지 않고 진짜 DB에 있는 값을 가져옴)
    List<Member> findMemberFetchJoin();
    //연관관계가 있는 member.team()같은 것을 객체 그래프 라고 하는데 연관관계를 join하면서 다 끌어오는 방식이다  (한방쿼리로 끝내버리니 이 쿼리로 인한 다른 쿼리들이 나가지 않기때문에 N + 1 상황이 일어나지 않는다)

    @Override
    @EntityGraph(attributePaths = {"team"})   // -> 이렇게 data jpa에서는 jpql없이 객체 그래프를 한번에 엮어서 가져오는게 된다 (fetch 조인)
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();  //jpql에 datajpa로 fetch join넣기

//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //named엔티티그래프
    List<Member> findEntityGraphByUsername(@Param("username") String username); //메소드 이름으로 쿼리 생성에서 fetch join

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //hibernate readOnly
    Member findReadOnlyByUsername(String username);

    //select for update (비관적인 lock) select 할 때 다른 애들 손대지 마! 하고 lock을 건다
    @Lock(LockModeType.PESSIMISTIC_WRITE) //실시간 트래픽이 많은 서비스는 Lock을 사용하지 말자
    List<Member> findLockByUsername(String username);

    //핵심비즈니스로직 리파지토리와 화면에 맞게 반환해주는 DTO리파지토리랑 나눠주는것이 좋다

    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
