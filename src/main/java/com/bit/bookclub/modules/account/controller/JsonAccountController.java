package com.bit.bookclub.modules.account.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bit.bookclub.modules.account.domain.entity.Account;
import com.bit.bookclub.modules.account.form.SignUpForm;
import com.bit.bookclub.modules.account.repository.AccountRepository;
import com.bit.bookclub.modules.account.service.AccountService;
import com.bit.bookclub.modules.account.support.CurrentUser;
import com.bit.bookclub.modules.account.validator.SignUpFormValidator;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JsonAccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/json-sign-up")
    @ResponseBody
    public HashMap<String, Object> signUpForm(Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        json.put("signUpForm", new SignUpForm());
        return json;
    }

    @PostMapping("/json-sign-up")
    @ResponseBody
    public HashMap<String, Object> signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
    	HashMap<String, Object> json = new HashMap<>();
        if (errors.hasErrors()) {
        	json.put("errors", errors);
        	json.put("redirect", "account/sign-up");
        } else {
        	Account account = accountService.signUp(signUpForm);
        	accountService.login(account);
        	json.put("account", account);
        	json.put("redirect", "/");
        }
        return json;
    }

    @GetMapping("/json-check-email-token")
    @ResponseBody
    public HashMap<String, Object> verifyEmail(String token, String email, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Account account = accountService.findAccountByEmail(email);
        if (account == null) {
        	json.put("error", "wrong.email");
            return json;
        }
        if (!token.equals(account.getEmailToken())) {
        	json.put("error", "wrong.token");
            return json;
        }
        accountService.verify(account);
        json.put("numberOfUsers", accountRepository.count());
        json.put("nickname", account.getNickname());
        return json;
    }

    @GetMapping("/json-check-email")
    @ResponseBody
    public HashMap<String, Object> checkMail(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
    	json.put("email", account.getEmail());
        return json;
    }

    @GetMapping("/json-resend-email")
    @ResponseBody
    public HashMap<String, Object> resendEmail(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        if (!account.enableToSendEmail()) {
        	json.put("error", "?????? ???????????? 5?????? ??? ?????? ????????? ??? ????????????.");
        	json.put("email", account.getEmail());
            return json;
        }
        accountService.sendVerificationEmail(account);
        json.put("result", true);
        json.put("redirect", "/");
        return json;
    }

    @GetMapping("/json-profile/{nickname}")
    @ResponseBody
    public HashMap<String, Object> viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
    	HashMap<String, Object> json = new HashMap<>();
        Account accountByNickname = accountService.getAccountBy(nickname);
        json.put("accountByNickname", accountByNickname);
        json.put("isOwner", accountByNickname.equals(account));
        return json;
    }

//    @GetMapping("/email-login")
//    public String emailLoginForm() {
//        return "account/email-login";
//    }

    @PostMapping("/json-email-login")
    @ResponseBody
    public HashMap<String, Object> sendLinkForEmailLogin(String email, Model model, RedirectAttributes attributes) {
    	HashMap<String, Object> json = new HashMap<>();
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
        	json.put("error", "????????? ????????? ????????? ????????????.");
            return json;
        }
        if (!account.enableToSendEmail()) {
        	json.put("error", "?????? ?????? ???????????????. 5??? ?????? ?????? ???????????????.");
            return json;
        }
        accountService.sendLoginLink(account);
        json.put("message", "????????? ????????? ????????? ???????????? ?????????????????????.");
        json.put("redirect", "/email-login");
        return json;
    }

    @GetMapping("/json-login-by-email")
    @ResponseBody
    public HashMap<String, Object> loginByEmail(String token, String email, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.isValid(token)) {
        	json.put("error", "???????????? ??? ????????????.");
            return json;
        }
        accountService.login(account);
        json.put("result", true);
        return json;
    }
}