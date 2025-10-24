package org.rupp.amsruppapi.service;

import org.rupp.amsruppapi.model.entity.AppUserProfile;

import java.util.List;

public interface AppUserProfileService {
    List<AppUserProfile> findAll();
    AppUserProfile findById(Long id);
    AppUserProfile findByAppUserId(Long appUserId);
    AppUserProfile create(AppUserProfile appUserProfile);
    AppUserProfile update(Long id, AppUserProfile appUserProfile);
    void deleteById(Long id);
}
