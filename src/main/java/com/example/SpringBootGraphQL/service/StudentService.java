package com.example.SpringBootGraphQL.service;

import com.example.SpringBootGraphQL.entity.Student;
import com.example.SpringBootGraphQL.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {
    private StudentRepository studentRepository;

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public void saveAllStudents(List<Student> studentList) {
        studentRepository.saveAll(studentList);
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public Student findStudentByEmail(String email) {
        return studentRepository.findStudentByEmail(email);
    }
}
