package com.bit.bookclub.modules.main.controller;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.account.repository.AccountRepository;
import com.bit.bookclub.modules.account.support.CurrentUser;
import com.bit.bookclub.modules.event.repository.EnrollmentRepository;
import com.bit.bookclub.modules.study.domain.entity.Study;
import com.bit.bookclub.modules.study.repository.StudyRepository;

@RestController
@RequiredArgsConstructor
public class JsonMainController {

	private final StudyRepository studyRepository;
	private final AccountRepository accountRepository;
	private final EnrollmentRepository enrollmentRepository;

	@GetMapping("/json")
	@ResponseBody
	public HashMap<String, Object> home(@CurrentUser Account account) {
		HashMap<String, Object> json = new HashMap<>();
		if (account != null) {
			Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
			json.put("accountLoaded", accountLoaded);
			json.put("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));
			json.put("studyList", studyRepository.findByAccount(accountLoaded.getTags(), accountLoaded.getZones()));
			json.put("studyManagerOf", studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
			json.put("studyMemberOf", studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
		} else {
			json.put("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
		}
		return json;
	}

//  @GetMapping("/login")
//  public String login() {
//    return "login";
//  }

	@GetMapping("/search/json-study")
	@ResponseBody
	public HashMap<String, Object> searchStudy(String keyword, Model model,
			@PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
		HashMap<String, Object> json = new HashMap<>();
		json.put("studyPage", studyPage);
		json.put("keyword", keyword);
		json.put("sortProperty",
				pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
		return json;
	}
}