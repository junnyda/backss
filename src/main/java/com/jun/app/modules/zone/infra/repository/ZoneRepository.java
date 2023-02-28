package com.jun.app.modules.zone.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jun.app.modules.account.domain.entity.Zone;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCityAndProvince(String cityName, String provinceName);
}
