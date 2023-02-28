package com.bit.bookclub.modules.account.predicates;

import com.bit.account.domain.entity.QAccount;
import com.bit.bookclub.modules.account.domain.entity.Zone;
import com.bit.bookclub.modules.tag.domain.entity.Tag;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
