package org.rupp.amsruppapi.model.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppUserProfile {
    private Long appUserProfileId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String CurrentAddress;
    private String phoneNumber;
    private String gender;
    private String cardId;
    private String nationality;
    private Long appUserId;
    private LocalTime createdAt;
    private LocalDate updatedAt;
}
