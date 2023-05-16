package com.flw.moka.entity.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "logs_archive", indexes = {
        @Index(name = "time_index", columnList = "time_in")
})
public class LogsArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_reference", nullable = false)
    private String transactionReference;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "method", nullable = false)
    private String method;

    @Lob
    @Column(name = "body", nullable = false, length = 99999999)
    private String body;

    @Lob
    @Column(name = "response", nullable = false, columnDefinition = "LONGTEXT")
    private String response;

    @Column(name = "time_in", nullable = false)
    private String timeIn;
}
