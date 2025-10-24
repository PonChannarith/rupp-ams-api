package org.rupp.amsruppapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {
    private Long subjectId;
    private String subjectName;
    private String subjectDescription;
    private String groupLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
