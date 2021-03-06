package com.ezfarm.ezfarmback.farm.acceptance;


import static com.ezfarm.ezfarmback.common.db.AcceptanceStep.assertThatStatusIsOk;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.ezfarm.ezfarmback.common.db.AcceptanceStep;
import com.ezfarm.ezfarmback.common.db.CommonAcceptanceTest;
import com.ezfarm.ezfarmback.farm.acceptance.step.FarmAcceptanceStep;
import com.ezfarm.ezfarmback.farm.domain.enums.CropType;
import com.ezfarm.ezfarmback.farm.domain.enums.FarmType;
import com.ezfarm.ezfarmback.farm.dto.FarmRequest;
import com.ezfarm.ezfarmback.farm.dto.FarmResponse;
import com.ezfarm.ezfarmback.farm.dto.FarmSearchCond;
import com.ezfarm.ezfarmback.farm.dto.FarmSearchResponse;
import com.ezfarm.ezfarmback.user.dto.AuthResponse;
import com.ezfarm.ezfarmback.user.dto.LoginRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("농가 통합 테스트")
public class FarmAcceptanceTest extends CommonAcceptanceTest {

  FarmRequest vinylTomatoFarmRequest;

  FarmRequest vinylPaprikaFarmRequest;

  AuthResponse authResponse;

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    LoginRequest loginRequest = new LoginRequest("test1@email.com", "비밀번호");
    authResponse = getAuthResponse(loginRequest);

    vinylTomatoFarmRequest = FarmRequest.builder()
        .name("테스트 농가 이름1")
        .address("서울")
        .phoneNumber("010-1234-1234")
        .area("1000")
        .farmType(FarmType.VINYL.toString())
        .cropType(CropType.TOMATO.toString())
        .isMain(true)
        .build();

    vinylPaprikaFarmRequest = FarmRequest.builder()
        .name("테스트 농가 이름2")
        .address("경기")
        .phoneNumber("010-1234-1234")
        .area("1000")
        .farmType(FarmType.VINYL.toString())
        .cropType(CropType.PAPRIKA.toString())
        .isMain(false)
        .build();
  }

  @DisplayName("나의 농가를 생성한다.")
  @Test
  void createFarm() {
    ExtractableResponse<Response> createResponse = FarmAcceptanceStep.requestToCreateFarm(
        authResponse, vinylTomatoFarmRequest);
    Long farmId = FarmAcceptanceStep.getLocation(createResponse);

    ExtractableResponse<Response> findResponse = FarmAcceptanceStep.requestToFindMyFarm(
        authResponse, farmId);
    FarmResponse farmResponse = findResponse.jsonPath().getObject(".", FarmResponse.class);

    AcceptanceStep.assertThatStatusIsCreated(createResponse);
    FarmAcceptanceStep.assertThatFindMyFarm(farmResponse, vinylTomatoFarmRequest);
  }

  @DisplayName("나의 농가를 수정한다.")
  @Test
  void updateFarm() {
    FarmRequest updateRequest = FarmRequest.builder()
        .name("테스트 농가 이름3")
        .address("경기")
        .phoneNumber("01098765432")
        .cropType(CropType.PAPRIKA.toString())
        .farmType(FarmType.GLASS.toString())
        .area("20000")
        .isMain(true)
        .build();

    Long farmId = FarmAcceptanceStep.requestToCreateFarmAndGetLocation(authResponse,
        vinylTomatoFarmRequest);
    ExtractableResponse<Response> updateResponse = FarmAcceptanceStep.requestToUpdateFarm(
        authResponse, updateRequest, farmId);
    ExtractableResponse<Response> findResponse = FarmAcceptanceStep.requestToFindMyFarm(
        authResponse, farmId);
    FarmResponse farmResponse = findResponse.jsonPath().getObject(".", FarmResponse.class);

    assertThatStatusIsOk(updateResponse);
    FarmAcceptanceStep.assertThatFindMyFarm(farmResponse, updateRequest);
  }

  @DisplayName("나의 모든 농가를 조회한다.")
  @Test
  void findMyFarms() {
    FarmAcceptanceStep.requestToCreateFarm(authResponse, vinylTomatoFarmRequest);
    FarmAcceptanceStep.requestToCreateFarm(authResponse, vinylPaprikaFarmRequest);

    ExtractableResponse<Response> response = FarmAcceptanceStep.requestToFindMyFarms(authResponse);
    List<FarmResponse> farmResponses = response.jsonPath().getList(".", FarmResponse.class);

    assertThatStatusIsOk(response);
    FarmAcceptanceStep
        .assertThatFindMyFarms(farmResponses, vinylTomatoFarmRequest, vinylPaprikaFarmRequest);
  }

  @DisplayName("나의 농가를 조회한다.")
  @Test
  void findMyFarm() {
    Long farmId = FarmAcceptanceStep.requestToCreateFarmAndGetLocation(authResponse,
        vinylTomatoFarmRequest);
    ExtractableResponse<Response> response = FarmAcceptanceStep.requestToFindMyFarm(authResponse,
        farmId);
    FarmResponse farmResponse = response.jsonPath().getObject(".", FarmResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    FarmAcceptanceStep.assertThatFindMyFarm(farmResponse, vinylTomatoFarmRequest);
  }

  @DisplayName("농가를 삭제한다.")
  @Test
  void deleteFarm() {
    Long farmId = FarmAcceptanceStep
        .requestToCreateFarmAndGetLocation(authResponse, vinylTomatoFarmRequest);

    ExtractableResponse<Response> deleteResponse = FarmAcceptanceStep
        .requestToDeleteMyFarm(authResponse, farmId);

    List<FarmResponse> farmResponses = FarmAcceptanceStep.requestToFindMyFarms(authResponse)
        .jsonPath()
        .getList(".", FarmResponse.class);

    AcceptanceStep.assertThatStatusIsNoContent(deleteResponse);
    assertThat(farmResponses.size()).isEqualTo(0);
  }

  @DisplayName("타 농가를 조회한다.")
  @Test
  void findOtherFarms() {
    AuthResponse ownerAuthResponse = getAuthResponse(
        new LoginRequest("test2@email.com", "비밀번호"));
    FarmAcceptanceStep
        .requestToCreateFarm(ownerAuthResponse, vinylTomatoFarmRequest);
    FarmAcceptanceStep
        .requestToCreateFarm(ownerAuthResponse, vinylPaprikaFarmRequest);

    FarmSearchCond farmSearchCond = FarmSearchCond.builder()
        .farmType(FarmType.VINYL.toString())
        .cropType(CropType.PAPRIKA.toString())
        .build();
    ExtractableResponse<Response> response = FarmAcceptanceStep
        .requestToFindOtherFarms(authResponse, farmSearchCond);
    List<FarmSearchResponse> farmSearchResponse = response.jsonPath()
        .getList(".", FarmSearchResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    FarmAcceptanceStep.assertThatFindOtherFarms(farmSearchResponse, vinylPaprikaFarmRequest);
  }
}
