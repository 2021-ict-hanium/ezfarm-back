package com.ezfarm.ezfarmback.farm.dto;

import com.ezfarm.ezfarmback.farm.domain.enums.CropType;
import com.ezfarm.ezfarmback.farm.domain.enums.FarmType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FarmSearchResponse {

    private Long farmId;
    private String name;
    private String address;
    private String area;
    private FarmType farmType;
    private CropType cropType;

    @QueryProjection
    public FarmSearchResponse(Long farmId, String name, String address, String area,
        FarmType farmType, CropType cropType) {
        this.farmId = farmId;
        this.name = name;
        this.address = address;
        this.area = area;
        this.farmType = farmType;
        this.cropType = cropType;
    }
}
