package rw.gradtechgroup.devmonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import rw.gradtechgroup.devmonitor.entity.TeamMember;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberRequest {
    private String email;
    private TeamMember.TeamRole role;
}