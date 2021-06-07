package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.domain.entity.Member;
import study.datajpa.domain.entity.Team;

import javax.persistence.criteria.*;

public class MemberSpec {

    // 와.. 이게 뭘까 JPA Criteria................
    public static Specification<Member> teamName(final String teamName) {
        return (root, query, builder) -> {

            if (StringUtils.isEmpty(teamName)) {
                return null;
            }

            Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인
            return builder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, builder) ->
            builder.equal(root.get("username"), username);
    }
}
