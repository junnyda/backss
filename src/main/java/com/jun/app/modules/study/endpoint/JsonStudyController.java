package com.jun.app.modules.study.endpoint;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jun.app.modules.account.domain.entity.Account;
import com.jun.app.modules.account.support.CurrentUser;
import com.jun.app.modules.study.application.StudyService;
import com.jun.app.modules.study.domain.entity.Study;
import com.jun.app.modules.study.endpoint.form.StudyForm;
import com.jun.app.modules.study.endpoint.form.validator.StudyFormValidator;
import com.jun.app.modules.study.infra.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JsonStudyController {
    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/json-new-study")
    @ResponseBody
    public HashMap<String, Object> newStudyForm(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
    	json.put("studyForm", new StudyForm());
        return json;
    }

    @PostMapping("/json-new-study")
    @ResponseBody
    public HashMap<String, Object> newStudySubmit(@CurrentUser Account account, @Valid StudyForm studyForm, Errors errors) {
    	HashMap<String, Object> json = new HashMap<>();
        if (errors.hasErrors()) {
        	json.put("errors", errors);
        	json.put("redirect", "study/form");
        } else {
	        Study newStudy = studyService.createNewStudy(studyForm, account);
	        json.put("redirect", "/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8));
        }
        return json;
    }

    @GetMapping("/json-study/{path}")
    @ResponseBody
    public HashMap<String, Object> viewStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
    	json.put("study", studyService.getStudy(path));
        return json;
    }

    @GetMapping("/json-study/{path}/members")
    @ResponseBody
    public HashMap<String, Object> viewStudyMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
    	json.put("study", studyService.getStudy(path));
        return json;
    }

    @GetMapping("/json-study/{path}/join")
    @ResponseBody
    public HashMap<String, Object> joinStudy(@CurrentUser Account account, @PathVariable String path) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyRepository.findStudyWithMembersByPath(path);
        studyService.addMember(study, account);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/members");
        return json;
    }

    @GetMapping("/json-study/{path}/leave")
    @ResponseBody
    public HashMap<String, Object> leaveStudy(@CurrentUser Account account, @PathVariable String path) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyRepository.findStudyWithMembersByPath(path);
        studyService.removeMember(study, account);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/members");
        return json;
    }
}