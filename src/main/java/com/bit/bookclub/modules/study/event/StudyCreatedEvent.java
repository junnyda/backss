package com.bit.bookclub.modules.study.event;

import com.bit.bookclub.modules.study.domain.entity.Study;

import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private final Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
