package com.edutech.evaluations.repository;

import com.edutech.evaluations.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByAsignatura(String asignatura);
}
