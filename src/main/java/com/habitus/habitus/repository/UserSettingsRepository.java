package com.habitus.habitus.repository;

import com.habitus.habitus.repository.entity.RecordInfo;
import com.habitus.habitus.repository.entity.UserSettings;
import com.habitus.habitus.security.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    List<RecordInfo> findByUser(UserInfo user);
}