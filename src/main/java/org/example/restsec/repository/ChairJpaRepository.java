package org.example.restsec.repository;

import org.example.restsec.entity.ChairEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChairJpaRepository extends JpaRepository<ChairEntity, Long> {
}