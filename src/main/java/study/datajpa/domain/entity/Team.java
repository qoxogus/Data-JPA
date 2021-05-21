package study.datajpa.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") //mappedby는 FK가 없는쪽에 걸어주는게 좋다
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
