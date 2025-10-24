package org.rupp.amsruppapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.entity.AppUserProfile;
import org.rupp.amsruppapi.repository.AppUserProfileRepository;
import org.rupp.amsruppapi.service.AppUserProfileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserProfileServiceImpl implements AppUserProfileService {

    private final AppUserProfileRepository appUserProfileRepository;

    @Override
    public List<AppUserProfile> findAll() {
        return appUserProfileRepository.findAll();
    }

    @Override
    public AppUserProfile findById(Long id) {
        AppUserProfile profile = appUserProfileRepository.findById(id);
        if (profile == null) {
            throw new RuntimeException("User profile not found with ID: " + id);
        }
        return profile;
    }

    @Override
    public AppUserProfile findByAppUserId(Long appUserId) {
        AppUserProfile profile = appUserProfileRepository.findByAppUserId(appUserId);
        if (profile == null) {
            throw new RuntimeException("User profile not found for user ID: " + appUserId);
        }
        return profile;
    }

    @Override
    public AppUserProfile create(AppUserProfile appUserProfile) {
        // Check if card ID already exists
        if (appUserProfile.getCardId() != null &&
                appUserProfileRepository.countByCardId(appUserProfile.getCardId()) > 0) {
            throw new RuntimeException("Card ID already exists: " + appUserProfile.getCardId());
        }

        // Check if profile already exists for this user
        try {
            AppUserProfile existing = appUserProfileRepository.findByAppUserId(appUserProfile.getAppUserId());
            if (existing != null) {
                throw new RuntimeException("User profile already exists for user ID: " + appUserProfile.getAppUserId());
            }
        } catch (Exception e) {
            // Profile doesn't exist, continue
        }

        appUserProfileRepository.insert(appUserProfile);
        return appUserProfileRepository.findById(appUserProfile.getAppUserProfileId());
    }

    @Override
    public AppUserProfile update(Long id, AppUserProfile appUserProfile) {
        // Fetch existing profile
        AppUserProfile existing = appUserProfileRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("User profile not found with ID: " + id);
        }

        // Check if card ID is being changed and if it already exists
        if (appUserProfile.getCardId() != null &&
                !appUserProfile.getCardId().equals(existing.getCardId()) &&
                appUserProfileRepository.countByCardIdExcludingId(appUserProfile.getCardId(), id) > 0) {
            throw new RuntimeException("Card ID already exists: " + appUserProfile.getCardId());
        }

        // Update fields
        existing.setFirstName(appUserProfile.getFirstName());
        existing.setLastName(appUserProfile.getLastName());
        existing.setDateOfBirth(appUserProfile.getDateOfBirth());
        existing.setPlaceOfBirth(appUserProfile.getPlaceOfBirth());
        existing.setCurrentAddress(appUserProfile.getCurrentAddress());
        existing.setPhoneNumber(appUserProfile.getPhoneNumber());
        existing.setGender(appUserProfile.getGender());
        existing.setCardId(appUserProfile.getCardId());
        existing.setNationality(appUserProfile.getNationality());

        int rows = appUserProfileRepository.update(existing);
        if (rows == 0) {
            throw new RuntimeException("Failed to update user profile with ID: " + id);
        }

        return appUserProfileRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        AppUserProfile existing = appUserProfileRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("User profile not found with ID: " + id);
        }
        appUserProfileRepository.deleteById(id);
    }
}
