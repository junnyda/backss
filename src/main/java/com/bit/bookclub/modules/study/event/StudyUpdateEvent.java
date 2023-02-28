package com.bit.bookclub.modules.study.event;

import com.bit.bookclub.modules.study.domain.entity.Study;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StudyUpdateEvent {
    private final Study study;
    private final String message;
}
