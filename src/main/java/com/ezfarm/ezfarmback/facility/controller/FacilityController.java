package com.ezfarm.ezfarmback.facility.controller;

import com.ezfarm.ezfarmback.facility.dto.FacilityDailyAvgResponse;
import com.ezfarm.ezfarmback.facility.dto.FacilityMonthlyAvgResponse;
import com.ezfarm.ezfarmback.facility.dto.FacilityPeriodResponse;
import com.ezfarm.ezfarmback.facility.dto.FacilityWeekAvgResponse;
import com.ezfarm.ezfarmback.facility.service.FacilityService;
import com.ezfarm.ezfarmback.farm.dto.detail.FarmDetailSearchCond;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "농가 API")
@RequiredArgsConstructor
@RequestMapping("/api/facility")
@RestController
public class FacilityController {

    private final FacilityService facilityService;

    @ApiOperation(value = "검색 가능한 기간 조회")
    @GetMapping("/search-condition/{farmId}")
    public ResponseEntity<FacilityPeriodResponse> findFacilitySearchPeriod(
        @PathVariable Long farmId) {
        FacilityPeriodResponse res = facilityService.findFacilitySearchPeriod(farmId);
        return ResponseEntity.ok(res);
    }

    @ApiOperation(value = "타 농가 상세 조회(일)")
    @GetMapping("/daily-avg/{farmId}")
    public ResponseEntity<FacilityDailyAvgResponse> findFacilityDailyAvg(@PathVariable Long farmId,
        @RequestBody FarmDetailSearchCond farmDetailSearchCond) {
        FacilityDailyAvgResponse res = facilityService.findFacilityDailyAvg(farmId,
            farmDetailSearchCond);
        return ResponseEntity.ok(res);
    }

    @ApiOperation(value = "타 농가 상세 조회(주)")
    @GetMapping("/week-avg/{farmId}")
    public ResponseEntity<FacilityWeekAvgResponse> findFacilityWeekAvg(@PathVariable Long farmId,
        @RequestBody FarmDetailSearchCond farmDetailSearchCond) {
        FacilityWeekAvgResponse res = facilityService.findFacilityWeekAvg(farmId,
            farmDetailSearchCond);
        return ResponseEntity.ok(res);
    }

    @ApiOperation(value = "타 농가 상세 조회(월)")
    @GetMapping("/monthly-avg/{farmId}")
    public ResponseEntity<FacilityMonthlyAvgResponse> findFacilityMonthlyAvg(
        @PathVariable Long farmId,
        @RequestBody FarmDetailSearchCond farmDetailSearchCond) {
        FacilityMonthlyAvgResponse res = facilityService.findFacilityMonthlyAvg(farmId,
            farmDetailSearchCond);
        return ResponseEntity.ok(res);
    }
}
