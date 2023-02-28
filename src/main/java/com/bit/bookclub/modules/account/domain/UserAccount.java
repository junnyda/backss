package com.bit.bookclub.modules.account.domain;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.bit.bookclub.modules.account.domain.entity.Account;

import java.util.List;

public class UserAccount extends User {
    @Getter
    private final Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}