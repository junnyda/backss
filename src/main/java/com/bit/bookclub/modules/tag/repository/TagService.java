package com.bit.bookclub.modules.tag.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bit.bookclub.modules.tag.domain.entity.Tag;
import com.bit.bookclub.modules.tag.infra.repository.TagRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        return tagRepository.findByTitle(tagTitle).orElseGet(
                () -> tagRepository.save(Tag.builder()
                        .title(tagTitle)
                        .build())
        );
    }
}
