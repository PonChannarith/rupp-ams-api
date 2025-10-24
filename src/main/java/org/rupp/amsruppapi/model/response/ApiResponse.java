package org.rupp.amsruppapi.model.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private HttpStatus status;
    private T payload;
    private LocalDateTime localDateTime;
}
