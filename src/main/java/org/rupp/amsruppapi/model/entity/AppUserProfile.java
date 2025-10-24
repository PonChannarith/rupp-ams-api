package org.rupp.amsruppapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserProfile {
    private Long appUserProfileId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String currentAddress;
//    private String profilePicture;
    private String phoneNumber;
    private String gender;
    private String cardId;
    private String nationality;
    private Long appUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}