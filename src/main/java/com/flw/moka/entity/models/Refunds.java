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
        @Index(name = "refund_ref_index", columnList = "refund_reference"),
        @Index(name = "transaction_ref_index", columnList = "transaction_reference"),
        @Index(name = "payload_ref_index", columnList = "payload_reference"),
        @Index(name = "time_refunded_index", columnList = "time_refunded"),
        @Index(name = "mask_index", columnList = "mask")
})
public class Refunds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_reference", nullable = false)
    private String transactionReference;

    @Column(name = "payload_reference")
    private String payloadReference;

    @Column(name = "refund_reference")
    private String refundReference;

    @Column(name = "refund_id")
    private String refundId;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "mask")
    private String mask;

    @Column(name = "time_refunded")
    private String timeRefunded;

    @Column(name = "provider")
    private String provider;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "refunded_amount")
    private Long refundedAmount;

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
