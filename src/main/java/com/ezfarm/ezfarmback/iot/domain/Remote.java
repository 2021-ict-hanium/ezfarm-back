package com.ezfarm.ezfarmback.iot.domain;

import com.ezfarm.ezfarmback.common.BaseTimeEntity;
import com.ezfarm.ezfarmback.farm.domain.Farm;
import com.ezfarm.ezfarmback.iot.dto.RemoteRequest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Remote extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "remote_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "farm_id")
  private Farm farm;

  @Enumerated(EnumType.STRING)
  private OnOff water;

  private String temperature;

  @Enumerated(EnumType.STRING)
  private OnOff illuminance;

  @Enumerated(EnumType.STRING)
  private OnOff co2;

  @Builder
  public Remote(Long id, Farm farm, OnOff water, String temperature,
      OnOff illuminance, OnOff co2) {
    this.id = id;
    this.farm = farm;
    this.water = water;
    this.temperature = temperature;
    this.illuminance = illuminance;
    this.co2 = co2;
  }

  public static Remote create(Farm farm) {
    return Remote.builder()
        .farm(farm)
        .co2(OnOff.OFF)
        .illuminance(OnOff.OFF)
        .temperature("0.0")
        .water(OnOff.OFF)
        .build();
  }

  public void updateRemote(RemoteRequest remoteRequest) {
    this.water = OnOff.valueOf(remoteRequest.getWater());
    this.temperature = remoteRequest.getTemperature();
    this.illuminance = OnOff.valueOf(remoteRequest.getIlluminance());
    this.co2 = OnOff.valueOf(remoteRequest.getCo2());
  }
}
