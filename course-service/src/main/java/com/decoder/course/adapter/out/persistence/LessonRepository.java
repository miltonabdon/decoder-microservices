package com.decoder.course.adapter.out.persistence;
import com.decoder.course.domain.model.LessonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface LessonRepository extends JpaRepository<LessonModel, UUID> {
    List<LessonModel> findByModuleIdOrderBySequenceNumber(UUID moduleId);
}
