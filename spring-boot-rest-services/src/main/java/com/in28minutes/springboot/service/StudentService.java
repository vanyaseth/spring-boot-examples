package com.in28minutes.springboot.service;

import com.in28minutes.springboot.model.Course;
import com.in28minutes.springboot.model.Student;
import com.in28minutes.springboot.repository.StudentsRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Component
public class StudentService {

	@Autowired
	private StudentsRepositoryImpl repository;

	public List<Student> retrieveAllStudents() {
		return repository.getStudents();
	}


	public Student retrieveStudent(String studentId) {
		for (Student student : repository.getStudents()) {
			if (student.getId().equals(studentId)) {
				return student;
			}
		}
		return null;
	}

	public List<Course> retrieveCourses(String studentId) {
		Student student = retrieveStudent(studentId);
		
		if(studentId.equalsIgnoreCase("Student1")){
			throw new RuntimeException("Something went wrong");
		}

		if (student == null) {
			return null;
		}

		return student.getCourses();
	}

	public Course retrieveCourse(String studentId, String courseId) {
		Student student = retrieveStudent(studentId);

		if (student == null) {
			return null;
		}

		for (Course course : student.getCourses()) {
			if (course.getId().equals(courseId)) {
				return course;
			}
		}

		return null;
	}

	private SecureRandom random = new SecureRandom();

	public Course addCourse(String studentId, Course course) {
		Student student = retrieveStudent(studentId);

		if (student == null) {
			return null;
		}

		String randomId = new BigInteger(130, random).toString(32);
		course.setId(randomId);

		student.getCourses().add(course);

		return course;
	}

	public Page<Student> findPaginated(Pageable pageable) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Student> students;
		final List<Student> studentList = repository.getStudents();
		if (studentList.size() < startItem) {
			students = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, studentList.size());
			students = studentList.subList(startItem, toIndex);
		}

		return new PageImpl<>(students, PageRequest.of(currentPage, pageSize), studentList.size());
	}
}
