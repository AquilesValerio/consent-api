package com.sensedia.consentapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    private static final String CPF_PATTERN = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";



    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return false;
        if (!cpf.matches(CPF_PATTERN)) return false;
        return isValidCpfDigits(cpf.replaceAll("[^\\d]", ""));
    }

    private boolean isValidCpfDigits(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * (10 - i);
        int first = 11 - (sum % 11);
        if (first >= 10) first = 0;
        if (first != (cpf.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * (11 - i);
        int second = 11 - (sum % 11);
        if (second >= 10) second = 0;
        return second == (cpf.charAt(10) - '0');
    }
}
