package utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.stream.Collectors;


public class Validacion {
    private Validator validator;

    public Validacion() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

    }

    public String validarObjeto(Object object) {
        return validator.validate(object).stream().map(
                ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
    }
}
