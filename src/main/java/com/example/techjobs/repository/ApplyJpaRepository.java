package com.example.techjobs.repository;

import com.example.techjobs.entity.Apply;
import com.example.techjobs.entity.ApplyId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplyJpaRepository extends JpaRepository<Apply, ApplyId> {

  @Query(value = "SELECT COUNT(a) FROM Apply a WHERE a.job.id = :jobId")
  Integer countNumberApplyByJob(@Param(value = "jobId") Integer jobId);

  @Query(value = "SELECT COUNT(a) FROM Apply a WHERE a.user.id = :userId")
  Integer countNumberApplyByUser(@Param(value = "userId") Integer userId);

  List<Apply> findAllByJobId(Integer jobId);
}
