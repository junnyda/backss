package com.bit.bookclub.modules.study.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.bit.bookclub.modules.account.domain.entity.Zone;
import com.bit.bookclub.modules.study.domain.entity.Study;
import com.bit.bookclub.modules.tag.domain.entity.Tag;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

  Page<Study> findByKeyword(String keyword, Pageable pageable);

  List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
