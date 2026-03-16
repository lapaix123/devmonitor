package rw.gradtechgroup.devmonitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "server_health")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(name = "cpu_usage")
    private Double cpuUsage;

    @Column(name = "ram_usage")
    private Double ramUsage;

    @Column(name = "disk_usage")
    private Double diskUsage;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}