package rw.gradtechgroup.devmonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import rw.gradtechgroup.devmonitor.entity.Server;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerRequest {
    private UUID teamId;
    private String name;
    private String ipAddress;
    private Integer port;
    private String os;
    private Server.Environment environment;
}