package currency.rate.service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    @NotNull
    private LocalDateTime time = LocalDateTime.now();

    @NotNull
    private HttpStatus statusCode;

    private List<String> errorMessage;

    private List<String> stackTrace;

}
