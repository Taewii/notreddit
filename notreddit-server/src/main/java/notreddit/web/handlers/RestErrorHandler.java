package notreddit.web.handlers;

import notreddit.data.models.responses.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class RestErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ApiResponse handleModelValidationError(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String[] errorMessages = new String[fieldErrors.size()];

        for (int i = 0; i < fieldErrors.size(); i++) {
            errorMessages[i] = fieldErrors.get(i).getDefaultMessage();
        }

        return new ApiResponse(false, String.join(System.lineSeparator(), errorMessages));
    }
}
