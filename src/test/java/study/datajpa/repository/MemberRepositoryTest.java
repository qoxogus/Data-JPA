package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.dto.MemberDto;
import study.datajpa.domain.entity.Member;
import study.datajpa.domain.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan ("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findListByUsername("AAA"); //데이터가 없을 시 empty 컬렉션을 반환함
        System.out.println("result = " + result);
        Member findMember = memberRepository.findMemberByUsername("AAA"); //데이터가 없을 시 결과가 null 반환 됨
        System.out.println("findMember = " + findMember);
        Optional<Member> optionalResult = memberRepository.findOptionalByUsername("AAA"); //데이터가 있을 수도있고 없을 수도 있으면 Optional을 쓰자
        System.out.println("optionalResult.get() = " + optionalResult.get());
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); //page는 1부터가 아니라 0부터 시작이다.

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //total카운트 쿼리까지 같이 날려준다
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest); //total카운트 쿼리는 같이 날려주지 않지만  limit이 3개 이지만 한개더 가져와서 다음페이지가 있나보다 인지 한 다음 더보기를 누르거나 다음걸 미리 로딩해놓는다거나 api설계를 할 수 있음(type만 Slice로 바꾸면 가능!)
//        List<Member> page = memberRepository.findByAge(age, pageRequest); //다른 메서드들이 필요하지 않다면 List로 가져와도 된다 (정확히 원하는 값만 딱딱 끊어서 가져올 때)

        //보통 DB에선 totalCount쿼리가 성능을 대부분 다 잡아먹는다.

        //엔티티는 절대 외부로 노출시키면 안된다 DTO로 변환해서 반환하자
//        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//        //여기서 toMap은 api로 반환을 해도 된다
//
//        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
//        int resultCount = memberRepository.bulkAgePlus(20); //20살이거나 그 이상인 사람들은 +1  (현재상태에서 대상은3개)
        //벌크연산은 영속성 컨텍스트와 1차캐시를 상관하지 않고 바로 DB에 반영한다

//        em.clear();  //벌크 연산 후 이 작업들을 하지 않으면 영속성컨텍스트 1차캐시에 있는 벌크연산이 반영되지않은 데이터를 가져오게 된다
        //이 작업은 Spring Data JPA에서 대신 해준다 @Modifying옵션 클리어오토매틱컬.

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println("member = " + member);

        //then
//        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush(); //쿼리 나감
        em.clear(); //영속성 컨텍스트 클리어

        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2"); //dirty check  (update query)  member1 -> member2

        //HiberNate에서 조회만 했을 때 성능 최적화 방법 (JPA표준에선 지원하지않음)
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); //성능 최적화를 해버리기때문에 스냅샷들이랑을 만들지 않음(readOnly이기 때문에 변경이 되지 않는다고 가정하고 전부 무시해버림)
        findMember.setUsername("member2"); //update query문이 나가지 않는다

        em.flush();

        //then

    }

    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush(); //쿼리 나감
        em.clear(); //영속성 컨텍스트 클리어

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");


    }
}
