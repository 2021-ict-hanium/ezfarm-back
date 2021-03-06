package com.ezfarm.ezfarmback.farm.dto;

import com.ezfarm.ezfarmback.common.validator.ValueOfEnum;
import com.ezfarm.ezfarmback.farm.domain.enums.CropType;
import com.ezfarm.ezfarmback.farm.domain.enums.FarmType;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
public class FarmRequest {

  @ApiModelProperty(value = "농가 주소", required = true)
  @NotBlank
  private String address;

  @ApiModelProperty(value = "농가 이름", required = true)
  @NotBlank
  private String name;

  @ApiModelProperty(value = "농가 전화번호", required = true)
  @NotBlank
  private String phoneNumber;

  @ApiModelProperty(value = "농가 면적", required = true)
  @NotBlank
  private String area;

  @ApiModelProperty(value = "메인 농가 여부", required = true)
  private boolean isMain;

  @ApiModelProperty(value = "농가 타입", required = true)
  @NotBlank
  @ValueOfEnum(enumClass = FarmType.class)
  private String farmType;

  @ApiModelProperty(value = "농가 작물 타입", required = true)
  @NotBlank
  @ValueOfEnum(enumClass = CropType.class)
  private String cropType;

  @ApiModelProperty(value = "재배 시작 일자")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @Builder
  public FarmRequest(String address, String name, String phoneNumber, String area, boolean isMain,
      String farmType, String cropType, LocalDate startDate) {
    this.address = address;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.area = area;
    this.isMain = isMain;
    this.farmType = farmType;
    this.cropType = cropType;
    this.startDate = startDate;
  }
}
