package com.flw.moka.entity.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "refunds", indexes = {
        @Index(name = "refundref_index", columnList = "refund_reference"),
        @Index(name = "transactionref_index", columnList = "transaction_reference"),
        @Index(name = "payloadref_index", columnList = "payload_reference"),
        @Index(name = "timerefunded_index", columnList = "time_refunded"),
        @Index(name = "mask_index", columnList = "mask")
})
public class Refunds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_reference", nullable = false)
    private String transactionReference;

    @Column(name = "payload_reference", nullable = true)
    private String payloadReference;

    @Column(name = "refund_reference", nullable = true)
    private String refundReference;

    @Column(name = "refund_id", nullable = true)
    private String refundId;

    @Column(name = "external_reference", nullable = true)
    private String externalReference;

    @Column(name = "mask", nullable = true)
    private String mask;

    @Column(name = "time_refunded", nullable = true)
    private String timeRefunded;

    @Column(name = "provider", nullable = true)
    private String provider;

    @Column(name = "balance", nullable = true)
    private Long balance;

    @Column(name = "refunded_amount", nullable = true)
    private Long refundedAmount;

    @Column(name = "currency", nullable = true)
    private String currency;

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "narration", nullable = true)
    private String narration;

    @Column(name = "response_message", nullable = false)
    private String responseMessage;

    @Column(name = "response_code", nullable = false)
    private String responseCode;
}
