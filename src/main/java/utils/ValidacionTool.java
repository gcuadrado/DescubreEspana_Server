package utils;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;


public class ValidacionTool {
    @Inject
    private Validator validator;


    public String validarObjeto(Object object) {
        return validator.validate(object).stream().map(
                ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
    }
}
