package com.decoder.course.domain.service;

import com.decoder.course.adapter.in.controller.dto.CourseDetailView;
import com.decoder.course.adapter.in.controller.dto.CourseListView;
import com.decoder.course.adapter.in.controller.dto.CourseRequestDto;
import com.decoder.course.adapter.out.client.AuthUserClient;
import com.decoder.course.adapter.out.messaging.CourseEventPublisher;
import com.decoder.course.adapter.out.persistence.*;
import com.decoder.course.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CourseUserRepository courseUserRepository;
    private final UserDataRepository userDataRepository;
    private final AuthUserClient authUserClient;
    private final CourseEventPublisher eventPublisher;

    @Transactional
    public CourseModel createCourse(CourseRequestDto dto) {
        var c = new CourseModel();
        c.setName(dto.name());
        c.setDescription(dto.description());
        c.setLevel(dto.level() != null ? dto.level() : CourseLevel.BEGINNER);
        c.setStatus(CourseStatus.INPROGRESS);
        if (dto.instructorId() != null) c.setInstructorId(dto.instructorId());
        return courseRepository.save(c);
    }

    public Page<CourseListView> listCourses(Pageable pageable) {
        return courseRepository.findAllProjectedBy(pageable);
    }

    public CourseDetailView getCourseDetail(UUID id) {
        return courseRepository.findProjectedById(id)
            .orElseThrow(() -> new NoSuchElementException("Course not found: " + id));
    }

    public CourseModel findById(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Course not found: " + id));
    }

    @Transactional
    public CourseModel updateCourse(UUID id, CourseRequestDto dto) {
        var c = findById(id);
        if (dto.name() != null) c.setName(dto.name());
        if (dto.description() != null) c.setDescription(dto.description());
        if (dto.level() != null) c.setLevel(dto.level());
        if (dto.status() != null) c.setStatus(dto.status());
        return courseRepository.save(c);
    }

    @Transactional
    public void deleteCourse(UUID id) { findById(id); courseRepository.deleteById(id); }

    @Transactional
    public ModuleModel createModule(UUID courseId, String title, String desc, int seq) {
        var m = new ModuleModel();
        m.setCourse(findById(courseId));
        m.setTitle(title); m.setDescription(desc); m.setSequenceNumber(seq);
        return moduleRepository.save(m);
    }

    public List<ModuleModel> listModules(UUID courseId) {
        findById(courseId);
        return moduleRepository.findByCourseIdOrderBySequenceNumber(courseId);
    }

    @Transactional
    public LessonModel createLesson(UUID moduleId, String title, String desc, String videoUrl, int seq) {
        var mod = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new NoSuchElementException("Module not found: " + moduleId));
        var l = new LessonModel();
        l.setModule(mod); l.setTitle(title); l.setDescription(desc);
        l.setVideoUrl(videoUrl); l.setSequenceNumber(seq);
        return lessonRepository.save(l);
    }

    public List<LessonModel> listLessons(UUID moduleId) {
        return lessonRepository.findByModuleIdOrderBySequenceNumber(moduleId);
    }

    @Transactional
    public CourseUserModel enrollUser(UUID courseId, UUID userId) {
        var course = findById(courseId);
        // API Composition: valida usuário via Feign (com fallback para dados replicados)
        try {
            var resp = authUserClient.getUserById(userId);
            if (resp == null || resp.getBody() == null) {
                userDataRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            }
        } catch (Exception ex) {
            userDataRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        }
        if (courseUserRepository.existsByCourseIdAndUserId(courseId, userId))
            throw new IllegalStateException("User already enrolled");
        var enrollment = new CourseUserModel();
        enrollment.setCourse(course);
        enrollment.setUserId(userId);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        var saved = courseUserRepository.save(enrollment);
        // Saga Coreografia: publica evento
        eventPublisher.publishEnrollmentCreated(saved);
        return saved;
    }

    @Transactional
    public void unenrollUser(UUID courseId, UUID userId) {
        var e = courseUserRepository.findByCourseIdAndUserId(courseId, userId)
            .orElseThrow(() -> new NoSuchElementException("Enrollment not found"));
        courseUserRepository.delete(e);
        eventPublisher.publishEnrollmentDeleted(e);
    }

    public List<CourseUserModel> listEnrollments(UUID courseId) {
        findById(courseId);
        return courseUserRepository.findByCourseId(courseId);
    }
}
