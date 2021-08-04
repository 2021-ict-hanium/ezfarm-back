package com.ezfarm.ezfarmback.facility.service;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ezfarm.ezfarmback.facility.domain.FacilityAvg;
import com.ezfarm.ezfarmback.facility.domain.day.FacilityDayAvg;
import com.ezfarm.ezfarmback.facility.domain.day.FacilityDayAvgRepository;
import com.ezfarm.ezfarmback.facility.dto.FacilityDailyAvgRequest;
import com.ezfarm.ezfarmback.facility.dto.FacilityPeriodResponse;
import com.ezfarm.ezfarmback.facility.dto.FacilityResponse;
import com.ezfarm.ezfarmback.farm.domain.Farm;
import com.ezfarm.ezfarmback.farm.domain.FarmRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("시설 단위 테스트(Service)")
public class FacilityServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private FacilityDayAvgRepository facilityDayAvgRepository;

    FacilityService facilityService;

    Farm farm;

    @BeforeEach
    void setUp() {
        facilityService = new FacilityService(farmRepository, facilityDayAvgRepository);

        farm = Farm.builder()
            .id(1L)
            .name("테스트 농가 이름1")
            .address("서울")
            .isMain(false)
            .startDate(LocalDate.now())
            .build();
    }

    @DisplayName("검색 가능한 기간을 검색한다.")
    @Test
    void findFacilitySearchPeriod_success() {
        FacilityPeriodResponse periodResponse = new FacilityPeriodResponse("2020-1", "2021-10");
        when(farmRepository.findById(any())).thenReturn(ofNullable(farm));
        when(facilityDayAvgRepository.findMinAndMaxMeasureDateByFarm(farm)).thenReturn(
            periodResponse);

        FacilityPeriodResponse response = facilityService.findFacilitySearchPeriod(1L);

        verify(farmRepository).findById(any());
        verify(facilityDayAvgRepository).findMinAndMaxMeasureDateByFarm(any());

        Assertions.assertAll(
            () -> assertThat(response.getStartDate()).isEqualTo(periodResponse.getStartDate()),
            () -> assertThat(response.getEndDate()).isEqualTo(periodResponse.getEndDate())
        );
    }

    @DisplayName("타 농가 일 평균 데이터를 조회한다.")
    @Test
    void findFacilityDailyAvg_success() {
        FacilityDailyAvgRequest facilityDailyAvgRequest = new FacilityDailyAvgRequest("2020", "01");
        FacilityDayAvg facilityDayAvg = FacilityDayAvg.builder()
            .facilityAvg(new FacilityAvg())
            .measureDate(LocalDateTime.of(2020, 1, 1, 0, 0))
            .build();

        when(farmRepository.findById(any())).thenReturn(ofNullable(farm));
        when(facilityDayAvgRepository.findAllByFarmAndMeasureDateStartsWith(any(),
            any())).thenReturn(List.of(facilityDayAvg));

        List<FacilityResponse> response = facilityService.findFacilityDailyAvg(1L,
            facilityDailyAvgRequest);

        verify(farmRepository).findById(any());
        verify(facilityDayAvgRepository).findAllByFarmAndMeasureDateStartsWith(any(), any());
        Assertions.assertAll(
            () -> assertThat(response.size()).isEqualTo(1),
            () -> assertThat(response).extracting("measureDate")
                .contains("2020-01-01"),
            () -> assertThat(response).extracting("avgCo2")
                .contains(0.0f)
        );
    }
}