package com.decoder.course.adapter.in.controller;

import com.decoder.course.adapter.in.controller.dto.*;
import com.decoder.course.domain.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/api/courses")
    public ResponseEntity<?> createCourse(@RequestBody @Valid CourseRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @GetMapping("/api/courses")
    public ResponseEntity<?> listCourses(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(courseService.listCourses(pageable));
    }

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<?> getCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.findById(courseId));
    }

    @GetMapping("/api/courses/{courseId}/detail")
    public ResponseEntity<?> getCourseDetail(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.getCourseDetail(courseId));
    }

    @PutMapping("/api/courses/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable UUID courseId, @RequestBody CourseRequestDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, dto));
    }

    @DeleteMapping("/api/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/courses/{courseId}/modules")
    public ResponseEntity<?> createModule(@PathVariable UUID courseId, @RequestBody @Valid ModuleRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(courseService.createModule(courseId, dto.title(), dto.description(), dto.sequenceNumber()));
    }

    @GetMapping("/api/courses/{courseId}/modules")
    public ResponseEntity<?> listModules(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.listModules(courseId));
    }

    @PostMapping("/api/modules/{moduleId}/lessons")
    public ResponseEntity<?> createLesson(@PathVariable UUID moduleId, @RequestBody @Valid LessonRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(courseService.createLesson(moduleId, dto.title(), dto.description(), dto.videoUrl(), dto.sequenceNumber()));
    }

    @GetMapping("/api/modules/{moduleId}/lessons")
    public ResponseEntity<?> listLessons(@PathVariable UUID moduleId) {
        return ResponseEntity.ok(courseService.listLessons(moduleId));
    }

    @PostMapping("/api/courses/{courseId}/users/{userId}/subscription")
    public ResponseEntity<?> enroll(@PathVariable UUID courseId, @PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.enrollUser(courseId, userId));
    }

    @DeleteMapping("/api/courses/{courseId}/users/{userId}/subscription")
    public ResponseEntity<Void> unenroll(@PathVariable UUID courseId, @PathVariable UUID userId) {
        courseService.unenrollUser(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/courses/{courseId}/users")
    public ResponseEntity<?> listEnrollments(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.listEnrollments(courseId));
    }
}
