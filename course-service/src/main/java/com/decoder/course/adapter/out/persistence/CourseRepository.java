package com.decoder.course.adapter.out.persistence;

import com.decoder.course.adapter.in.controller.dto.CourseDetailView;
import com.decoder.course.adapter.in.controller.dto.CourseListView;
import com.decoder.course.domain.model.CourseModel;
import com.decoder.course.domain.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID>, JpaSpecificationExecutor<CourseModel> {

    // Listagem leve — só campos necessários
    Page<CourseListView> findAllProjectedBy(Pageable pageable);

    // Filtro por status com projection
    Page<CourseListView> findByStatus(CourseStatus status, Pageable pageable);

    // Detalhe com todos os campos
    Optional<CourseDetailView> findProjectedById(UUID id);
}
