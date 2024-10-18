package com.example.spring_security.repository;

import java.util.List;

import com.example.spring_security.model.Notice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NoticeRepository extends CrudRepository<Notice, Long> {

	List<Notice> findAll();

}
