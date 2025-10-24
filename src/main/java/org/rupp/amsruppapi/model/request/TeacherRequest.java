package org.rupp.amsruppapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {
    private String employeeCode;
    private LocalDate hireDate;
    private String status;
    private Long userId;
}
