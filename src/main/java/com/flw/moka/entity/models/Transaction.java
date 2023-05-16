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
@Table(name = "transactions", indexes = {
        @Index(name = "transaction_ref_index", columnList = "transaction_reference"),
        @Index(name = "refund_id_index", columnList = "refund_id"),
        @Index(name = "time_captured_index", columnList = "time_captured"),
        @Index(name = "time_voided_index", columnList = "time_voided"),
        @Index(name = "time_refunded_index", columnList = "time_refunded"),
        @Index(name = "time_in_index", columnList = "time_in"),
        @Index(name = "mask_index", columnList = "mask")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_reference", nullable = false)
    private String transactionReference;

    @Column(name = "payload_reference")
    private String payloadReference;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "mask")
    private String mask;

    @Column(name = "time_in")
    private String timeIn;

    @Column(name = "time_captured")
    private String timeCaptured;

    @Column(name = "time_voided")
    private String timeVoided;

    @Column(name = "time_refunded")
    private String timeRefunded;

    @Column(name = "refund_id")
    private String refundId;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "provider")
    private String provider;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "amount_refunded")
    private Long amountRefunded;

    @Column(name = "currency")
    private String currency;

    @Column(name = "country")
    private String country;

    @Column(name = "narration")
    private String narration;

    @Column(name = "response_message", nullable = false)
    private String responseMessage;

    @Column(name = "response_code", nullable = false)
    private String responseCode;
}
