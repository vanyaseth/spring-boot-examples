package com.in28minutes.springboot.controller;

import java.net.URI;
import java.util.List;

import com.in28minutes.springboot.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.in28minutes.springboot.model.Course;
import com.in28minutes.springboot.service.StudentService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class StudentController {

	@Autowired
	private StudentService studentService;

	@GetMapping("/students/{studentId}")
	public Resource<Student> retrieveStudentById(@PathVariable String studentId){
		Student student = studentService.retrieveStudent( studentId );

		Resource<Student> resource = new Resource<Student>(student);

		ControllerLinkBuilder linkTo = linkTo( methodOn( this.getClass() ).retrieveStudents( ) );
		resource.add( linkTo.withRel("all-students") );
		return resource;
	}

	@GetMapping("/students")
	public List<Student> retrieveStudents(){
		return studentService.retrieveAllStudents();
	}

	@GetMapping("/students/{studentId}/courses")
	public List<Course> retrieveCoursesForStudent(@PathVariable String studentId) {
		return studentService.retrieveCourses(studentId);
	}

	@PostMapping("/students/{studentId}/courses")
	public ResponseEntity registerStudentForCourse(
			@PathVariable String studentId, @RequestBody Course newCourse) {

		Course course = studentService.addCourse(studentId, newCourse);

		if (course == null)
			return ResponseEntity.noContent().build();

		Resource<Course> resource = new Resource<Course>(course);

		ControllerLinkBuilder linkTo = linkTo( methodOn( this.getClass() ).retrieveDetailsForCourse(studentId, course.getId() ) );
		resource.add( linkTo.withSelfRel(  ) );


		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
				"/{id}").buildAndExpand(course.getId()).toUri();

		return ResponseEntity.created(location).body(resource);
	}

	@GetMapping("/students/{studentId}/courses/{courseId}")
	public Course retrieveDetailsForCourse(@PathVariable String studentId,
			@PathVariable String courseId) {
		return studentService.retrieveCourse(studentId, courseId);
	}

}
