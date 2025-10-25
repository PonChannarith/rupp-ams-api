package org.rupp.amsruppapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long studentId;
    private String studentNo;
    private String studentCardId;
    private String khmerName;
    private String englishName;
    private String gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
