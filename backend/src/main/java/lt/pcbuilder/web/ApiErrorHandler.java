package lt.pcbuilder.web;

import lt.pcbuilder.domain.PlanningException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(PlanningException.class)
    public ResponseEntity<Map<String,String>> planning(PlanningException e) {
        HttpStatus status = "NO_BUILD".equals(e.code()) ? HttpStatus.UNPROCESSABLE_ENTITY :
                            "PART_NOT_FOUND".equals(e.code()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("code",e.code(),"message",e.getMessage()));
    }
}
