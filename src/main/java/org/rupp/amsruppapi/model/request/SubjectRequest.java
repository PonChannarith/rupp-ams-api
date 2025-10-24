package org.rupp.amsruppapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {
    private String subjectName;
    private String subjectDescription;
    private String groupLevel;
}
