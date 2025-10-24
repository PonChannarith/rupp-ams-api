package org.rupp.amsruppapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserProfileRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String currentAddress;
    private String phoneNumber;
    private String gender;
    private String cardId;
    private String nationality;
    private Long appUserId;
}
