package rw.gradtechgroup.devmonitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "server_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CredentialType type;

    @Column(name = "encrypted_data", columnDefinition = "TEXT", nullable = false)
    private String encryptedData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum CredentialType {
        SSH, API, AGENT
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}