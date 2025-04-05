package com.zoi.drive.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    SUCCESS(1, "success"),
    PENDING(0, "pending"),
    FAILURE(-1, "failure");

    private final Integer id;
    private final String name;

    PaymentStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

}
