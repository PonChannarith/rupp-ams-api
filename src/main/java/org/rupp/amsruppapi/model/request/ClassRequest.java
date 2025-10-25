package org.rupp.amsruppapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {
    private String className;
    private String gradeLevel;
    private String academicYear;
}