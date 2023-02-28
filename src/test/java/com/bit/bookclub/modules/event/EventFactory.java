package com.bit.bookclub.modules.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.event.domain.entity.Event;
import com.bit.bookclub.modules.event.domain.entity.EventType;
import com.bit.bookclub.modules.event.form.EventForm;
import com.bit.bookclub.modules.event.repository.EventRepository;
import com.bit.bookclub.modules.event.service.EventService;
import com.bit.bookclub.modules.study.domain.entity.Study;
import com.bit.bookclub.modules.study.repository.StudyRepository;

import java.time.LocalDateTime;

@Component
public class EventFactory {
    @Autowired EventRepository eventRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired EventService eventService;

    public Event createEvent(EventType eventType, Account account, String studyPath) {
        Study study = studyRepository.findByPath(studyPath);
        LocalDateTime now = LocalDateTime.now();
        EventForm eventForm = EventForm.builder()
                .description("description")
                .eventType(eventType)
                .endDateTime(now.plusWeeks(3))
                .endEnrollmentDateTime(now.plusWeeks(1))
                .limitOfEnrollments(2)
                .startDateTime(now.plusWeeks(2))
                .title("title")
                .build();
        return eventService.createEvent(study, eventForm, account);
    }
}
