package com.bit.bookclub.modules.study.controller;

import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.account.domain.entity.Zone;
import com.bit.bookclub.modules.account.form.TagForm;
import com.bit.bookclub.modules.account.form.ZoneForm;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {
    private final StudyService studyService;
    private final TagService tagService;
    private final StudyRepository studyRepository;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(StudyDescriptionForm.builder()
                .shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription())
                .build());
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudy(@CurrentUser Account account, @PathVariable String path, @Valid StudyDescriptionForm studyDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }
        studyService.updateStudyDescription(study, studyDescriptionForm);
        attributes.addFlashAttribute("message", "????????? ????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String studyImageForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/banner";
    }

    @PostMapping("/banner")
    public String updateBanner(@CurrentUser Account account, @PathVariable String path, String image, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyImage(study, image);
        attributes.addFlashAttribute("message", "????????? ???????????? ?????????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.enableStudyBanner(study);
        return "redirect:/study/" + study.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.disableStudyBanner(study);
        return "redirect:/study/" + study.getEncodedPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String studyTagsForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("tags", study.getTags().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList()));
        model.addAttribute("whitelist", objectMapper.writeValueAsString(tagRepository.findAll().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList())));
        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseStatus(HttpStatus.OK)
    public void addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);
    }

    @PostMapping("/tags/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle())
                .orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ???????????????."));
        studyService.removeTag(study, tag);
    }

    @GetMapping("/zones")
    public String studyZonesForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("zones", study.getZones().stream()
                .map(Zone::toString)
                .collect(Collectors.toList()));
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zoneRepository.findAll().stream()
                .map(Zone::toString)
                .collect(Collectors.toList())));
        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseStatus(HttpStatus.OK)
    public void addZones(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
                .orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ???????????????."));
        studyService.addZone(study, zone);
    }

    @PostMapping("/zones/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeZones(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
                .orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ???????????????."));
        studyService.removeZone(study, zone);
    }

    @GetMapping("/study")
    public String studySettingForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.publish(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/close")
    public String closeStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.close(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentUser Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (!study.isEnableToRecruit()) {
            attributes.addFlashAttribute("message", "1?????? ?????? ?????? ?????? ????????? ?????? ??? ????????? ??? ????????????.");
            return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
        }
        studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "?????? ????????? ???????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentUser Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (!study.isEnableToRecruit()) {
            attributes.addFlashAttribute("message", "1?????? ?????? ?????? ?????? ????????? ?????? ??? ????????? ??? ????????????.");
            return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
        }
        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "?????? ????????? ???????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentUser Account account, @PathVariable String path, @RequestParam String newPath, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (!studyService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyPathError", "????????? ??? ?????? ????????? ???????????????.");
            return "study/settings/study";
        }
        studyService.updateStudyPath(study, newPath);
        attributes.addFlashAttribute("message", "????????? ????????? ?????????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/title")
    public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (!studyService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyTitleError", "????????? ????????? ?????? ???????????????.");
            return "study/settings/study";
        }

        studyService.updateStudyTitle(study, newTitle);
        attributes.addFlashAttribute("message", "????????? ????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.remove(study);
        return "redirect:/";
    }
}
