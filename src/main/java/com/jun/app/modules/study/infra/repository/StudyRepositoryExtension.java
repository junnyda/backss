package com.jun.app.modules.study.infra.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.jun.app.modules.account.domain.entity.Zone;
import com.jun.app.modules.study.domain.entity.Study;
import com.jun.app.modules.tag.domain.entity.Tag;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

  Page<Study> findByKeyword(String keyword, Pageable pageable);

  List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
