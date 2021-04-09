package com.example.SpringBootGraphQL.controller;

import com.example.SpringBootGraphQL.entity.Student;
import com.example.SpringBootGraphQL.service.StudentService;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;


@RestController
public class StudentController {
    private StudentService studentService;

    @Value("classpath:student.graphqls")
    private Resource schemaResource;

    private GraphQL graphQL;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        GraphQLSchema graphQLSchema = new SchemaGenerator()
                .makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    @GetMapping
    public List<Student> findAllStudents() {
        return studentService.findAllStudents();
    }

    @PostMapping
    public void saveStudent(@RequestBody Student student) {
        studentService.saveStudent(student);
    }

    @PostMapping("/saveAllStudents")
    public void saveAllStudents(@RequestBody List<Student> studentList) {
        studentService.saveAllStudents(studentList);
    }

    @PostMapping("/findAllStudentsWithGraphQL")
    public ResponseEntity<Object> findAllStudentsWithGraphQL(@RequestBody String query) {
        ExecutionResult executionResult = graphQL.execute(query);
        return new ResponseEntity<Object>(executionResult, HttpStatus.OK);
    }

    @PostMapping("/findAllStudentByEmailWithGraphQL")
    public ResponseEntity<Object> findStudentByEmailWithGraphQL(@RequestBody String query) {
        ExecutionResult executionResult = graphQL.execute(query);
        return new ResponseEntity<Object>(executionResult, HttpStatus.OK);
    }

    private RuntimeWiring buildRuntimeWiring() {
        DataFetcher<List<Student>> findAllStudentsDataFetcher = (data) -> {
            return studentService.findAllStudents();
        };

        DataFetcher<Student> findStudentByEmailDataFetcher = (data) -> {
            return studentService.findStudentByEmail(data.getArgument("email"));
        };

        return RuntimeWiring.newRuntimeWiring().type("Query", (typeRuntimeWiring) -> {
            return typeRuntimeWiring
                    .dataFetcher("findAllStudent", findAllStudentsDataFetcher)
                    .dataFetcher("findStudentByEmail", findStudentByEmailDataFetcher);
        }).build();
    }
}
