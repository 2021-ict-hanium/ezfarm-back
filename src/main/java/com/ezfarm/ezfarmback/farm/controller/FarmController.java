package com.ezfarm.ezfarmback.farm.controller;

import com.ezfarm.ezfarmback.farm.domain.Farm;
import com.ezfarm.ezfarmback.farm.domain.FarmRepository;
import com.ezfarm.ezfarmback.farm.dto.FarmRequest;
import com.ezfarm.ezfarmback.farm.service.FarmService;
import com.ezfarm.ezfarmback.security.CurrentUser;
import com.ezfarm.ezfarmback.user.domain.User;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/farm")
@RestController()
public class FarmController {

    private final FarmRepository farmRepository;

    private final FarmService farmService;

    @GetMapping
    public List<Farm> allFarm(@CurrentUser User user) {
        List<Farm> farms = farmService.viewAllFarms(user);
        return farms;
    }

    @GetMapping("/{farmId}")
    public Farm viewFarm(@CurrentUser User user, @PathVariable Long farmId) {
        Farm farm = farmService.viewFarm(user, farmId);
        return farm;
    }

    @PostMapping
    public ResponseEntity<Void> createFarm(@CurrentUser User user,
        @Valid @RequestBody FarmRequest farmRequest) {
        Long farmId = farmService.createFarm(user, farmRequest);
        return ResponseEntity.created(URI.create("/api/farm/" + farmId)).build();
    }

    @PatchMapping("/{farmId}")
    public Farm updateFarm(@CurrentUser User user, @PathVariable Long farmId,
        @Valid @RequestBody FarmRequest farmRequest) {
        Farm updateFarm = farmService.updateFarm(user, farmId, farmRequest);
        return updateFarm;
    }

    @DeleteMapping("/{farmId}")
    public List<Farm> deleteFarm(@CurrentUser User user, @PathVariable Long farmId) {
        farmService.deleteFarm(user, farmId);
        return farmRepository.findAll();
    }
}