package com.ezfarm.ezfarmback.facility.domain.hour;

import com.ezfarm.ezfarmback.farm.domain.Farm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long>,
    FacilityDayRepositoryCustom {

  Optional<Facility> findTop1ByFarmOrderByMeasureDateDesc(Farm mainFarm);

  List<Facility> findByMeasureDateStartsWith(String measureDate);
}
