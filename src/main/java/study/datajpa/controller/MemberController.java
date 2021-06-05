package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.domain.dto.MemberDto;
import study.datajpa.domain.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) { //조회용으로만 쓰자(트랜잭션이 없는 범위에서 에[ㄴ티티를 조회, 엔티티를 변경해도 DB에 반영되지않음)
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
//        return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//        return page.map(member -> new MemberDto(member));
        return page.map(MemberDto::new);
    }

    @PostConstruct //스프링애플리케이션이 올라올때 이게 실행된다
    public void init() {
//        memberRepository.save(new Member("userA"));
        for (int i =0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
