package org.rupp.amsruppapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    private Long classId;
    private String className;
    private String gradeLevel;
    private String academicYear;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}