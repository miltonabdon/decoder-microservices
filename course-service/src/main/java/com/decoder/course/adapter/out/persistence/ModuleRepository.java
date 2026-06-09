package com.decoder.course.adapter.out.persistence;
import com.decoder.course.domain.model.ModuleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ModuleRepository extends JpaRepository<ModuleModel, UUID> {
    List<ModuleModel> findByCourseIdOrderBySequenceNumber(UUID courseId);
}
