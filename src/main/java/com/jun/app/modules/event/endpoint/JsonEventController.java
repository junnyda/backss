package com.jun.app.modules.event.endpoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jun.app.modules.account.domain.entity.Account;
import com.jun.app.modules.account.support.CurrentUser;
import com.jun.app.modules.event.application.EventService;
import com.jun.app.modules.event.domain.entity.Enrollment;
import com.jun.app.modules.event.domain.entity.Event;
import com.jun.app.modules.event.endpoint.form.EventForm;
import com.jun.app.modules.event.infra.repository.EnrollmentRepository;
import com.jun.app.modules.event.infra.repository.EventRepository;
import com.jun.app.modules.event.validator.EventValidator;
import com.jun.app.modules.study.application.StudyService;
import com.jun.app.modules.study.domain.entity.Study;
import com.jun.app.modules.study.infra.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/json-study/{path}")
@RequiredArgsConstructor
public class JsonEventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    @ResponseBody
    public HashMap<String, Object> newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdateStatus(account, path);
        json.put("study", study);
        json.put("account", account);
        json.put("eventForm", new EventForm());
        return json;
    }

    @PostMapping("/new-event")
    @ResponseBody
    public HashMap<String, Object> createNewEvent(@CurrentUser Account account, @PathVariable String path, @Valid EventForm eventForm, Errors errors, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
        	json.put("errors", errors);
        	json.put("account", account);
            json.put("study", study);
        } else {
        	Event event = eventService.createEvent(study, eventForm, account);
        	json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        }
        return json;
    }

    @GetMapping("/events/{id}")
    @ResponseBody
    public HashMap<String, Object> getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
        json.put("event", event);
        json.put("study", studyRepository.findStudyWithManagersByPath(path));
        return json;
    }

    @GetMapping("/events")
    @ResponseBody
    public HashMap<String, Object> viewStudyEvents(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudy(path);
    	json.put("account", account);
        json.put("study", study);
        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        }
        json.put("newEvents", newEvents);
        json.put("oldEvents", oldEvents);
        return json;
    }

    @GetMapping("/events/{id}/edit")
    @ResponseBody
    public HashMap<String, Object> updateEventForm(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        json.put("study", studyService.getStudyToUpdate(account, path));
    	json.put("account", account);
        json.put("event", event);
        json.put("eventForm", EventForm.from(event));
        return json;
    }

    @PostMapping("/events/{id}/edit")
    @ResponseBody
    public HashMap<String, Object> updateEventSubmit(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event, @Valid EventForm eventForm, Errors errors, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdate(account, path);
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);
        if (errors.hasErrors()) {
        	json.put("errors", errors);
        	json.put("account", account);
        	json.put("study", study);
            json.put("event", event);
        } else {
	        eventService.updateEvent(event, eventForm);
	        json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        }
        return json;
    }

    @DeleteMapping("/events/{id}")
    @ResponseBody
    public HashMap<String, Object> deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdateStatus(account, path);
        eventService.deleteEvent(event);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/events");
        return json;
    }

    @PostMapping("/events/{id}/enroll")
    @ResponseBody
    public HashMap<String, Object> enroll(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToEnroll(path);
        eventService.enroll(event, account);
        json.put("redirect","/study/" + study.getEncodedPath() + "/events/" + event.getId());
        return json;
    }

    @PostMapping("/events/{id}/leave")
    @ResponseBody
    public HashMap<String, Object> leave(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToEnroll(path);
        eventService.leave(event, account);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        return json;
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/accept")
    @ResponseBody
    public HashMap<String, Object> acceptEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        return json;
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    @ResponseBody
    public HashMap<String, Object> rejectEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        return json;
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    @ResponseBody
    public HashMap<String, Object> checkInEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.checkInEnrollment(event, enrollment);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        return json;
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    @ResponseBody
    public HashMap<String, Object> cancelCheckInEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.cancelCheckinEnrollment(event, enrollment);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        return json;
    }
}