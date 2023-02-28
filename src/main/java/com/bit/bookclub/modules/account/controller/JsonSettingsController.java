package com.bit.bookclub.modules.account.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.account.domain.entity.Zone;
import com.bit.bookclub.modules.account.form.NicknameForm;
import com.bit.bookclub.modules.account.form.NotificationForm;
import com.bit.bookclub.modules.account.form.PasswordForm;
import com.bit.bookclub.modules.account.form.Profile;
import com.bit.bookclub.modules.account.service.AccountService;
import com.bit.bookclub.modules.account.support.CurrentUser;
import com.bit.bookclub.modules.account.validator.NicknameFormValidator;
import com.bit.bookclub.modules.account.validator.PasswordFormValidator;
import com.bit.bookclub.modules.tag.domain.entity.Tag;
import com.bit.bookclub.modules.tag.infra.repository.TagRepository;
import com.bit.bookclub.modules.zone.repository.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JsonSettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/json-profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/json-password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;
    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/json-notification";
    static final String SETTINGS_NOTIFICATION_URL = "/" + SETTINGS_NOTIFICATION_VIEW_NAME;
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/json-account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;
    static final String SETTINGS_TAGS_VIEW_NAME = "settings/json-tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;
    static final String SETTINGS_ZONE_VIEW_NAME = "settings/json-zones";
    static final String SETTINGS_ZONE_URL = "/" + SETTINGS_ZONE_VIEW_NAME;

    private final AccountService accountService;
    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void passwordFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    @ResponseBody
    public HashMap<String, Object> profileUpdateForm(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
    	json.put("ProfileFromAccount", Profile.from(account));
        return json;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    @ResponseBody
    public HashMap<String, Object> updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("errors", errors);
        if (errors.hasErrors()) {
        	json.put("account", account);
        	json.put("redirect", SETTINGS_PROFILE_VIEW_NAME);
        } else {
        	accountService.updateProfile(account, profile);
        	json.put("message", "프로필을 수정하였습니다.");
        	json.put("redirect", SETTINGS_PROFILE_URL);
        }
        return json;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    @ResponseBody
    public HashMap<String, Object> passwordUpdateForm(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        json.put("account", account);
        json.put("passwordForm", new PasswordForm());
        return json;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    @ResponseBody
    public HashMap<String, Object> updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
        if (errors.hasErrors()) {
        	json.put("account", account);
            json.put("redirect", SETTINGS_PASSWORD_VIEW_NAME);
        } else {
	        accountService.updatePassword(account, passwordForm.getNewPassword());
	        json.put("message", "패스워드를 변경했습니다.");
	        json.put("redirect", SETTINGS_PASSWORD_URL);
        }
        return json;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    @ResponseBody
    public HashMap<String, Object> notificationForm(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
    	json.put("notificationForm", NotificationForm.from(account));
        return json;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    @ResponseBody
    public HashMap<String, Object> updateNotification(@CurrentUser Account account, @Valid NotificationForm notificationForm, Errors errors, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
        if (errors.hasErrors()) {
        	json.put("account", account);
        	json.put("redirect", SETTINGS_NOTIFICATION_URL);
        } else {
	        accountService.updateNotification(account, notificationForm);
	        json.put("message", "알림설정을 수정하였습니다.");
	        json.put("redirect", SETTINGS_NOTIFICATION_URL);
        }
        return json;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    @ResponseBody
    public HashMap<String, Object> nicknameForm(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
        json.put("nicknameForm", new NicknameForm(account.getNickname()));
        return json;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    @ResponseBody
    public HashMap<String, Object> updateNickname(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
        if (errors.hasErrors()) {
        	json.put("account", account);
        	json.put("redirect", SETTINGS_ACCOUNT_VIEW_NAME);
        } else {
	        accountService.updateNickname(account, nicknameForm.getNickname());
	        json.put("message", "닉네임을 수정하였습니다.");
	        json.put("redirect", SETTINGS_ACCOUNT_URL);
        }
        return json;
    }

    @GetMapping(SETTINGS_TAGS_URL)
    @ResponseBody
    public HashMap<String, Object> updateTags(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
        Set<Tag> tags = accountService.getTags(account);
        json.put("tags", tags.stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll()
                .stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());
        String whitelist = null;
        try {
            whitelist = objectMapper.writeValueAsString(allTags);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        json.put("whitelist", whitelist);
        return json;
    }

    /*
     * @ResponseStatus 가 있으면 수정안해도 되어 주석처리했습니다.
     */
//    @PostMapping(SETTINGS_TAGS_URL + "/add")
//    @ResponseStatus(HttpStatus.OK)
//    public void addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
//        String title = tagForm.getTagTitle();
//        Tag tag = tagRepository.findByTitle(title)
//                .orElseGet(() -> tagRepository.save(Tag.builder()
//                        .title(title)
//                        .build()));
//        accountService.addTag(account, tag);
//    }

//    @PostMapping(SETTINGS_TAGS_URL + "/remove")
//    @ResponseStatus(HttpStatus.OK)
//    public void removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
//        String title = tagForm.getTagTitle();
//        Tag tag = tagRepository.findByTitle(title)
//                .orElseThrow(IllegalArgumentException::new);
//        accountService.removeTag(account, tag);
//    }

    @GetMapping(SETTINGS_ZONE_URL)
    @ResponseBody
    public HashMap<String, Object> updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("account", account);
        Set<Zone> zones = accountService.getZones(account);
        json.put("zones", zones.stream()
                .map(Zone::toString)
                .collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream()
                .map(Zone::toString)
                .collect(Collectors.toList());
        json.put("whitelist", objectMapper.writeValueAsString(allZones));
        return json;
    }

//    @PostMapping(SETTINGS_ZONE_URL + "/add")
//    @ResponseStatus(HttpStatus.OK)
//    public void addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
//        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
//                .orElseThrow(IllegalArgumentException::new);
//        accountService.addZone(account, zone);
//    }

//    @PostMapping(SETTINGS_ZONE_URL + "/remove")
//    @ResponseStatus(HttpStatus.OK)
//    public void removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
//        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
//                .orElseThrow(IllegalArgumentException::new);
//        accountService.removeZone(account, zone);
//    }
}