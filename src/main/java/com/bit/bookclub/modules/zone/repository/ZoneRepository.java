package com.bit.bookclub.modules.zone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.bookclub.modules.account.domain.entity.Zone;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCityAndProvince(String cityName, String provinceName);
}
