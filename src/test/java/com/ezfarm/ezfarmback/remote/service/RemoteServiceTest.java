package com.ezfarm.ezfarmback.remote.service;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ezfarm.ezfarmback.common.exception.CustomException;
import com.ezfarm.ezfarmback.common.exception.dto.ErrorCode;
import com.ezfarm.ezfarmback.farm.domain.Farm;
import com.ezfarm.ezfarmback.farm.domain.FarmRepository;
import com.ezfarm.ezfarmback.remote.domain.OnOff;
import com.ezfarm.ezfarmback.remote.domain.Remote;
import com.ezfarm.ezfarmback.remote.domain.RemoteRepository;
import com.ezfarm.ezfarmback.remote.dto.RemoteRequest;
import com.ezfarm.ezfarmback.remote.dto.RemoteResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("제어 단위 테스트(Service)")
class RemoteServiceTest {

    @Mock
    FarmRepository farmRepository;

    @Mock
    RemoteRepository remoteRepository;

    Remote defaultRemote;

    Remote remote;

    RemoteService remoteService;

    RemoteRequest remoteRequest;

    RemoteResponse remoteResponse;

    Farm farm;

    @BeforeEach
    void setUp() {
        remoteService = new RemoteService(farmRepository, remoteRepository);

        remoteResponse = new RemoteResponse(1L, OnOff.ON, 37.5f, OnOff.OFF, OnOff.ON,
            LocalDateTime.now(), LocalDateTime.now());

        remoteRequest = new RemoteRequest(1L, OnOff.ON, 37.5f, OnOff.OFF, OnOff.ON);

        farm = Farm.builder().build();
        defaultRemote = Remote.builder()
            .id(1L)
            .farm(farm)
            .co2(OnOff.OFF)
            .illuminance(OnOff.OFF)
            .temperature(0.0f)
            .water(OnOff.OFF)
            .build();
        remote = Remote.builder()
            .id(1L)
            .farm(farm)
            .co2(OnOff.OFF)
            .illuminance(OnOff.OFF)
            .temperature(40.5f)
            .water(OnOff.ON)
            .build();
    }

    @DisplayName("농가 제어값을 조회한다.")
    @Test
    void findRemote() {
        when(farmRepository.findById(any())).thenReturn(ofNullable(farm));
        when(remoteRepository.findByFarm(any())).thenReturn(ofNullable(remote));

        RemoteResponse remoteResponse = remoteService.findRemote(1L);

        Assertions.assertAll(
            () -> assertThat(remoteResponse.getTemperature()).isEqualTo(remote.getTemperature()),
            () -> assertThat(remoteResponse.getIlluminance()).isEqualTo(remote.getIlluminance()),
            () -> assertThat(remoteResponse.getCo2()).isEqualTo(remote.getCo2()),
            () -> assertThat(remoteResponse.getWater()).isEqualTo(remote.getWater())
        );
    }

    @DisplayName("농가 제어값을 생성한 후 조회한다.")
    @Test
    void findRemote_after_Creation() {
        when(farmRepository.findById(any())).thenReturn(ofNullable(farm));
        when(remoteRepository.findByFarm(any())).thenReturn(Optional.empty());
        when(remoteRepository.save(any())).thenReturn(defaultRemote);

        RemoteResponse response = remoteService.findRemote(1L);

        Assertions.assertAll(
            () -> assertThat(response.getTemperature()).isEqualTo(defaultRemote.getTemperature()),
            () -> assertThat(response.getIlluminance()).isEqualTo(defaultRemote.getIlluminance()),
            () -> assertThat(response.getCo2()).isEqualTo(defaultRemote.getCo2()),
            () -> assertThat(response.getWater()).isEqualTo(defaultRemote.getWater())
        );
    }

    @DisplayName("농가 제어값을 조회 시 존재하지 않는 농가면 예외가 발생한다.")
    @Test
    void findRemote_failure() {
        when(farmRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> remoteService.findRemote(1L))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_FARM_ID.getMessage());
    }

    @DisplayName("농가 제어를 요청한다.")
    @Test
    void updateRemote() {
        when(remoteRepository.findById(any())).thenReturn(ofNullable(remote));
        remoteService.updateRemote(remoteRequest);
        Assertions.assertAll(
            () -> AssertionsForClassTypes.assertThat(remote.getTemperature())
                .isEqualTo(remoteRequest.getTemperature()),
            () -> assertThat(remote.getIlluminance()).isEqualTo(remoteRequest.getIlluminance()),
            () -> assertThat(remote.getCo2()).isEqualTo(remoteRequest.getCo2()),
            () -> assertThat(remote.getWater()).isEqualTo(remoteRequest.getWater())
        );
    }

    @DisplayName("농가 제어 엔티티가 존재하지 않을 시 예외가 발생한다.")
    @Test
    void updateRemote_failure() {
        when(remoteRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> remoteService.updateRemote(remoteRequest))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}