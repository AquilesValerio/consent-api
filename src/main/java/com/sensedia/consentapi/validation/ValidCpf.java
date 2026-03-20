package com.sensedia.consentapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;



import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCpf {
    String message() default "CPF inválido. Formato esperado: ###.###.###-##";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
