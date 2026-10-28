package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.AuditLog;

import java.util.List;

@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLog, Long>{
    //get full audit history for any entity
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);
}
