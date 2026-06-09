package com.decoder.course.adapter.out.persistence;
import com.decoder.course.domain.model.CourseUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface CourseUserRepository extends JpaRepository<CourseUserModel, UUID> {
    List<CourseUserModel> findByCourseId(UUID courseId);
    Optional<CourseUserModel> findByCourseIdAndUserId(UUID courseId, UUID userId);
    boolean existsByCourseIdAndUserId(UUID courseId, UUID userId);
    void deleteAllByUserId(UUID userId);
}
