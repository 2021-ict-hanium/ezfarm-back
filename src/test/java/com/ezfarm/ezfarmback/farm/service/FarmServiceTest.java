package com.ezfarm.ezfarmback.farm.service;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ezfarm.ezfarmback.common.Pagination;
import com.ezfarm.ezfarmback.common.exception.CustomException;
import com.ezfarm.ezfarmback.common.exception.dto.ErrorCode;
import com.ezfarm.ezfarmback.farm.domain.Farm;
import com.ezfarm.ezfarmback.farm.domain.FarmRepository;
import com.ezfarm.ezfarmback.farm.domain.enums.CropType;
import com.ezfarm.ezfarmback.farm.domain.enums.FarmType;
import com.ezfarm.ezfarmback.farm.dto.FarmRequest;
import com.ezfarm.ezfarmback.farm.dto.FarmResponse;
import com.ezfarm.ezfarmback.farm.dto.FarmSearchCond;
import com.ezfarm.ezfarmback.farm.dto.FarmSearchResponse;
import com.ezfarm.ezfarmback.user.domain.Role;
import com.ezfarm.ezfarmback.user.domain.User;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("농가 단위 테스트(Service)")
public class FarmServiceTest {

  @Mock
  FarmRepository farmRepository;

  FarmService farmService;

  User user;

  Farm farm;

  FarmRequest farmRequest;

  FarmResponse farmResponse;

  @BeforeEach
  void setUp() {
    farmService = new FarmService(farmRepository);
    user = User.builder()
        .id(1L)
        .name("테스트 이름")
        .email("test@email.com")
        .password("비밀번호")
        .role(Role.ROLE_USER)
        .build();

    farm = Farm.builder()
        .id(1L)
        .name("테스트 농가 이름1")
        .address("서울")
        .startDate(LocalDate.now())
        .farmType(FarmType.GLASS)
        .cropType(CropType.PAPRIKA)
        .user(user)
        .build();

    farmRequest = FarmRequest.builder()
        .name("테스트 농가 이름2")
        .address("경기")
        .phoneNumber("01012341234")
        .area("10000")
        .farmType(FarmType.GLASS.toString())
        .cropType(CropType.PAPRIKA.toString())
        .startDate(LocalDate.now())
        .build();

    farmResponse = FarmResponse.builder()
        .id(1L)
        .name("테스트 농가 이름1")
        .address("서울")
        .build();
  }

  @DisplayName("농가 시작일이 없는 농가를 생성한다.")
  @Test
  void createFarm_startDate_null_success() {
    farmRequest.setMain(false);

    when(farmRepository.save(any())).thenReturn(farm);

    farmService.createFarm(user, farmRequest);

    verify(farmRepository).save(any());
  }

  @DisplayName("메인 농가를 생성한다.")
  @Test
  void createMainFarm_success() {
    Farm prevMainFarm = Farm.builder().isMain(true).build();
    farmRequest.setMain(true);

    when(farmRepository.save(any())).thenReturn(farm);
    when(farmRepository.findByUserAndIsMain(any(), anyBoolean())).thenReturn(of(prevMainFarm));

    farmService.createFarm(user, farmRequest);

    assertThat(prevMainFarm.isMain()).isEqualTo(false);
    verify(farmRepository).save(any());
    verify(farmRepository).findByUserAndIsMain(any(), anyBoolean());
  }

  @DisplayName("농가 시작일이 농가 생성일 이후인 농가를 생성한다.")
  @Test
  void createFarm_startDate_notNull_success() {
    farmRequest.setStartDate(LocalDate.of(9999, 12, 12));

    when(farmRepository.save(any())).thenReturn(farm);
    farmService.createFarm(user, farmRequest);

    assertThat(farm.getUser()).isEqualTo(user);
  }

  @DisplayName("농가 시작일이 농가 생성일보다 과거이면 예외가 발생한다.")
  @Test
  void createFarm_failure_customException() {
    farmRequest.setStartDate(LocalDate.of(2000, 1, 1));

    assertThatThrownBy(() -> farmService.createFarm(user, farmRequest))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_FARM_START_DATE.getMessage());
  }

  @DisplayName("나의 모든 농가를 조회한다.")
  @Test
  void findMyFarms_success() {
    when(farmRepository.findAllByUser(any())).thenReturn(singletonList(farm));

    List<FarmResponse> responses = farmService.findMyFarms(user);

    Assertions.assertAll(
        () -> assertThat(responses.size()).isEqualTo(1),
        () -> assertThat(responses.get(0).getName()).isEqualTo(farm.getName()),
        () -> assertThat(responses.get(0).getAddress()).isEqualTo(farm.getAddress())
    );
  }

  @DisplayName("나의 농가를 조회한다")
  @Test
  void findFarm_success() {
    when(farmRepository.findById(any())).thenReturn(ofNullable(farm));

    FarmResponse response = farmService.findMyFarm(1L);

    Assertions.assertAll(
        () -> assertThat(response.getAddress()).isEqualTo(farm.getAddress()),
        () -> assertThat(response.getPhoneNumber()).isEqualTo(farm.getPhoneNumber())
    );
  }

  @DisplayName("존재하지 않는 농가일 경우 예외가 발생한다")
  @Test
  void findFarm_InvalidFarmId_failure() {
    when(farmRepository.findById(any())).thenReturn(empty());

    assertThatThrownBy(() -> farmService.findMyFarm(1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_FARM_ID.getMessage());
  }

  @DisplayName("나의 농가를 수정한다.")
  @Test
  void updateFarm_success() {
    farm.setCreatedDate(LocalDateTime.now());
    when(farmRepository.findById(any())).thenReturn(ofNullable(farm));

    farmService.updateMyFarm(user, 1L, farmRequest);

    Assertions.assertAll(
        () -> assertThat(farm.getAddress()).isEqualTo(farmRequest.getAddress()),
        () -> assertThat(farm.getName()).isEqualTo(farmRequest.getName())
    );
  }

  @DisplayName("나의 농가를 삭제한다.")
  @Test
  void deleteFarm_success() {
    when(farmRepository.findById(any())).thenReturn(ofNullable(farm));

    farmService.deleteMyFarm(user, 1L);

    verify(farmRepository).delete(farm);
  }

  @DisplayName("자신의 농가가 아닌 농가를 삭제하면 예외가 발생한다.")
  @Test
  void deleteFarm_access_denied_failure() {
    Farm anotherFarm = Farm.builder().user(User.builder().id(2L).build()).build();
    when(farmRepository.findById(any())).thenReturn(ofNullable(anotherFarm));

    assertThatThrownBy(() -> farmService.deleteMyFarm(user, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.FARM_ACCESS_DENIED.getMessage());
  }

  @DisplayName("타 농가를 조회한다.")
  @Test
  void findOtherFarms_success() {
    FarmSearchCond farmSearchCond = new FarmSearchCond();
    FarmSearchResponse farmSearchResponse = new FarmSearchResponse();

    when(farmRepository.findByNotUserAndNotFavoritesAndFarmSearchCond(any(), any(), any()))
        .thenReturn(new PageImpl<>(singletonList(farmSearchResponse)));

    farmService.findOtherFarms(user, farmSearchCond, new Pagination(0, 10));

    verify(farmRepository)
        .findByNotUserAndNotFavoritesAndFarmSearchCond(user, farmSearchCond,
            PageRequest.of(0, 10));
  }
}
