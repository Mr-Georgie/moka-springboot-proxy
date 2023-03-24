package com.flw.moka.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "refunds")
public class Refunds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tx_ref", nullable = false)
    private String transactionRef;

    @Column(name = "ex_ref", nullable = false)
    private String externalRef;

    @Column(name = "mask", nullable = false)
    private String mask;

    @Column(name = "time_refunded", nullable = true)
    private String timeRefunded;

    @Column(name = "provider", nullable = true)
    private String provider;

    @Column(name = "amount", nullable = true)
    private Long amount;

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