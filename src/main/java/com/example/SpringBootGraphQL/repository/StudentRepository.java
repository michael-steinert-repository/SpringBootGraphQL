package com.example.SpringBootGraphQL.repository;

import com.example.SpringBootGraphQL.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    public Student findStudentByEmail(String email);
}
