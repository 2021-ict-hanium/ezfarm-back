package com.ezfarm.ezfarmback.alert.domain;

import com.ezfarm.ezfarmback.common.BaseTimeEntity;
import com.ezfarm.ezfarmback.control.domain.FacilityType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Alert extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "alert_id")
    private Long id;

    private FacilityType facilityType;

    private AlertType alertType;

    private Boolean isChecked;

}