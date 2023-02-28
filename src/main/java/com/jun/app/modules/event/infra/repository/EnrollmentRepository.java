package com.jun.app.modules.event.infra.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.jun.app.modules.account.domain.entity.Account;
import com.jun.app.modules.event.domain.entity.Enrollment;
import com.jun.app.modules.event.domain.entity.Event;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  boolean existsByEventAndAccount(Event event, Account account);

  Enrollment findByEventAndAccount(Event event, Account account);

  @EntityGraph("Enrollment.withEventAndStudy")
  List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account account, boolean accepted);
}