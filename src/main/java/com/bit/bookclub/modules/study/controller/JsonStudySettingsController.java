package com.bit.bookclub.modules.study.controller;

import java.util.HashMap;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.account.domain.entity.Zone;
import com.bit.bookclub.modules.account.support.CurrentUser;
import com.bit.bookclub.modules.study.domain.entity.Study;
import com.bit.bookclub.modules.study.form.StudyDescriptionForm;
import com.bit.bookclub.modules.study.repository.StudyRepository;
import com.bit.bookclub.modules.study.service.StudyService;
import com.bit.bookclub.modules.tag.domain.entity.Tag;
import com.bit.bookclub.modules.tag.infra.repository.TagRepository;
import com.bit.bookclub.modules.tag.repository.TagService;
import com.bit.bookclub.modules.zone.repository.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/json-study/{path}/settings")
@RequiredArgsConstructor
public class JsonStudySettingsController {
    private final StudyService studyService;
    private final TagService tagService;
    private final StudyRepository studyRepository;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    @ResponseBody
    public HashMap<String, Object> viewStudySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Study study = studyService.getStudyToUpdate(account, path);
        json.put("account", account);
        json.put("study", study);
        json.put("studyDescriptionForm", StudyDescriptionForm.builder()
                .shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription())
                .build());
        return json;
    }

    @PostMapping("/description")
    @ResponseBody
    public HashMap<String, Object> updateStudy(@CurrentUser Account account, @PathVariable String path, @Valid StudyDescriptionForm studyDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
        if (errors.hasErrors()) {
        	json.put("errors", errors);
            json.put("account", account);
            json.put("study", study);
        } else {
	        studyService.updateStudyDescription(study, studyDescriptionForm);
	        json.put("message", "스터디 소개를 수정했습니다.");
	        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/description");
        }
        return json;
    }

    @GetMapping("/banner")
    @ResponseBody
    public HashMap<String, Object> studyImageForm(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
    	json.put("account", account);
        json.put("study", study);
        return json;
    }

    @PostMapping("/banner")
    @ResponseBody
    public HashMap<String, Object> updateBanner(@CurrentUser Account account, @PathVariable String path, String image, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyImage(study, image);
        json.put("message", "스터디 이미지를 수정하였습니다.");
        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/banner");
        return json;
    }

    @PostMapping("/banner/enable")
    @ResponseBody
    public HashMap<String, Object> enableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
        studyService.enableStudyBanner(study);
        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/banner");
        return json;
    }

    @PostMapping("/banner/disable")
    @ResponseBody
    public HashMap<String, Object> disableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
        studyService.disableStudyBanner(study);
        json.put("redirect","/study/" + study.getEncodedPath() + "/settings/banner");
        return json;
    }

    @GetMapping("/tags")
    @ResponseBody
    public HashMap<String, Object> studyTagsForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
    	json.put("account", account);
        json.put("study", study);
        json.put("tags", study.getTags().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList()));
        json.put("whitelist", objectMapper.writeValueAsString(tagRepository.findAll().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList())));
        return json;
    }

//    @PostMapping("/tags/add")
//    @ResponseStatus(HttpStatus.OK)
//    public void addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
//        Study study = studyService.getStudyToUpdateTag(account, path);
//        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
//        studyService.addTag(study, tag);
//    }

//    @PostMapping("/tags/remove")
//    @ResponseStatus(HttpStatus.OK)
//    public void removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
//        Study study = studyService.getStudyToUpdateTag(account, path);
//        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));
//        studyService.removeTag(study, tag);
//    }

    @GetMapping("/zones")
    @ResponseBody
    public HashMap<String, Object> studyZonesForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
    	json.put("account", account);
        json.put("study", study);
        json.put("zones", study.getZones().stream()
                .map(Zone::toString)
                .collect(Collectors.toList()));
        json.put("whitelist", objectMapper.writeValueAsString(zoneRepository.findAll().stream()
                .map(Zone::toString)
                .collect(Collectors.toList())));
        return json;
    }

//    @PostMapping("/zones/add")
//    @ResponseStatus(HttpStatus.OK)
//    public void addZones(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
//        Study study = studyService.getStudyToUpdateZone(account, path);
//        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));
//        studyService.addZone(study, zone);
//    }

//    @PostMapping("/zones/remove")
//    @ResponseStatus(HttpStatus.OK)
//    public void removeZones(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
//        Study study = studyService.getStudyToUpdateZone(account, path);
//        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));
//        studyService.removeZone(study, zone);
//    }

    @GetMapping("/study")
    @ResponseBody
    public HashMap<String, Object> studySettingForm(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdate(account, path);
    	json.put("account", account);
        json.put("study", study);
        return json;
    }

    @PostMapping("/study/publish")
    @ResponseBody
    public HashMap<String, Object> publishStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.publish(study);
        json.put("message", "스터디를 공개했습니다.");
        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/study");
        return json;
    }

    @PostMapping("/study/close")
    @ResponseBody
    public HashMap<String, Object> closeStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.close(study);
        json.put("message", "스터디를 종료했습니다.");
        json.put("redirect","/study/" + study.getEncodedPath() + "/settings/study");
        return json;
    }

    @PostMapping("/recruit/start")
    @ResponseBody
    public HashMap<String, Object> startRecruit(@CurrentUser Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
        if (!study.isEnableToRecruit()) {
        	json.put("result", !study.isEnableToRecruit());
            json.put("message", "1시간 안에 인원 모집 설정을 여러 번 변경할 수 없습니다.");
            json.put("redirect","/study/" + study.getEncodedPath() + "/settings/study");
        } else {
	        studyService.startRecruit(study);
	        json.put("message", "인원 모집을 시작합니다.");
	        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/study");
        }
        return json;
    }

    @PostMapping("/recruit/stop")
    @ResponseBody
    public HashMap<String, Object> stopRecruit(@CurrentUser Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
    	json.put("result", !study.isEnableToRecruit());
        if (!study.isEnableToRecruit()) {
            json.put("message", "1시간 안에 인원 모집 설정을 여러 번 변경할 수 없습니다.");
            json.put("redirect","/study/" + study.getEncodedPath() + "/settings/study");
        } else {
	        studyService.stopRecruit(study);
	        json.put("message", "인원 모집을 종료합니다.");
	        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/study");
        }
        return json;
    }

    @PostMapping("/study/path")
    @ResponseBody
    public HashMap<String, Object> updateStudyPath(@CurrentUser Account account, @PathVariable String path, @RequestParam String newPath, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
    	json.put("result", !studyService.isValidPath(newPath));
        if (!studyService.isValidPath(newPath)) {
        	json.put("account", account);
            json.put("study", study);
            json.put("studyPathError", "사용할 수 없는 스터디 경로입니다.");
            json.put("redirect", "study/settings/study");
        } else {
	        studyService.updateStudyPath(study, newPath);
	        json.put("message", "스터디 경로를 수정하였습니다.");
	        json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/study");
        }
        return json;
    }

    @PostMapping("/study/title")
    @ResponseBody
    public HashMap<String, Object> updateStudyTitle(@CurrentUser Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
    	json.put("result", !studyService.isValidTitle(newTitle));
        if (!studyService.isValidTitle(newTitle)) {
        	json.put("account", account);
            json.put("study", study);
            json.put("studyTitleError", "스터디 이름을 다시 입력하세요.");
            json.put("redirect", "study/settings/study");
        } else {
        	studyService.updateStudyTitle(study, newTitle);
        	json.put("message", "스터디 이름을 수정했습니다.");
        	json.put("redirect", "/study/" + study.getEncodedPath() + "/settings/study");
        }
        return json;
    }

    @PostMapping("/study/remove")
    @ResponseBody
    public HashMap<String, Object> removeStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.remove(study);
        json.put("redirect", "/");
        return json;
    }
}
