package com.flw.moka.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "card_params")
public class CardParams {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "tx_ref", nullable = false)
    private String transactionRef;

    @Column(name = "ex_ref", nullable = false)
    private String externalRef;

    @Column(name = "method", nullable = false)
    private String method;

    @Lob
    @Column(name = "body", nullable = false, length = 99999999)
    private String body;

    @Lob
    @Column(name = "response", nullable = false)
    private String response;

    @Column(name = "time_in", nullable = false)
    private String timeIn;

}
