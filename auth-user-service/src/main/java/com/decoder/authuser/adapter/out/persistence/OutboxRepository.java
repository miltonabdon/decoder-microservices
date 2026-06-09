package com.decoder.authuser.adapter.out.persistence;

import com.decoder.authuser.domain.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("SELECT e FROM OutboxEvent e WHERE e.publishedAt IS NULL AND e.retryCount < 5 ORDER BY e.createdAt ASC")
    List<OutboxEvent> findUnpublished();
}
