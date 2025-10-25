package org.rupp.amsruppapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    private String studentNo;
    private String studentCardId;
    private String khmerName;
    private String englishName;
    private String gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Long userId;
}
