package com.edutech.payments.client;

import com.edutech.payments.dto.CourseDto;
import com.edutech.payments.dto.EnrollmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "courses", url = "http://localhost:8081/edutech/courses")
public interface CourseClient {

    @GetMapping("/{id}")
    CourseDto getCourseById(@PathVariable("id") Long id);

    @GetMapping("/enrollments/{id}")
    EnrollmentDto getEnrollmentDtoById(@PathVariable("id") Long id);
}
