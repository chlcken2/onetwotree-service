package hellomyteam.hellomyteam.repository;

import hellomyteam.hellomyteam.dto.TeamListDto;
import hellomyteam.hellomyteam.entity.Team;
import hellomyteam.hellomyteam.entity.TeamMemberInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query(value = "select new hellomyteam.hellomyteam.dto.TeamListDto(t.id, t.teamName, t.oneIntro, t.teamSerialNo, m.name, t.memberCount, i.imageUrl, t.location, " +
            "    leader_tmi.authority, " +
            "    member_tmi.authority)" +
            "FROM TeamMemberInfo leader_tmi " +
            "JOIN Team t ON leader_tmi.team.id = t.id " +
            "LEFT JOIN Image i ON t.id = i.team.id " +
            "JOIN Member m ON leader_tmi.member.id = m.id " +
            "LEFT JOIN TeamMemberInfo member_tmi ON member_tmi.team.id = t.id AND member_tmi.member.id = :memberId " +
            "WHERE " +
            "    leader_tmi.authority = 'LEADER' " +
            "ORDER BY " +
            "    t.id asc",
            countQuery = "SELECT COUNT(*) " +
            "FROM TeamMemberInfo leader_tmi " +
            "JOIN Team t ON leader_tmi.team.id = t.id " +
            "JOIN Member m ON leader_tmi.member.id = m.id " +
            "LEFT JOIN TeamMemberInfo member_tmi ON member_tmi.team.id = t.id AND member_tmi.member.id = :memberId " +
            "WHERE leader_tmi.authority = 'LEADER'"
    )
    Page<TeamListDto> getTeamListAsc(@Param("pageable") Pageable pageable,
                                     @Param("memberId") long memberId);

    @Query(value = "select new hellomyteam.hellomyteam.dto.TeamListDto(t.id, t.teamName, t.oneIntro, t.teamSerialNo, m.name, t.memberCount, i.imageUrl, t.location, " +
            "    leader_tmi.authority, " +
            "    member_tmi.authority)" +
            "FROM TeamMemberInfo leader_tmi " +
            "JOIN Team t ON leader_tmi.team.id = t.id " +
            "LEFT JOIN Image i ON t.id = i.team.id " +
            "JOIN Member m ON leader_tmi.member.id = m.id " +
            "LEFT JOIN TeamMemberInfo member_tmi ON member_tmi.team.id = t.id AND member_tmi.member.id = :memberId " +
            "WHERE " +
            "    leader_tmi.authority = 'LEADER' " +
            "ORDER BY " +
            "    t.id desc",
            countQuery = "SELECT COUNT(*) " +
                    "FROM TeamMemberInfo leader_tmi " +
                    "JOIN Team t ON leader_tmi.team.id = t.id " +
                    "JOIN Member m ON leader_tmi.member.id = m.id " +
                    "LEFT JOIN TeamMemberInfo member_tmi ON member_tmi.team.id = t.id AND member_tmi.member.id = :memberId " +
                    "WHERE leader_tmi.authority = 'LEADER'"
    )
    Page<TeamListDto> getTeamListDesc(@Param("pageable") Pageable pageable,
                                     @Param("memberId") long memberId);
}
