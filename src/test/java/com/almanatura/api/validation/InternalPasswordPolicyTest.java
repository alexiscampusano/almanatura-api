package com.almanatura.api.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class InternalPasswordPolicyTest {

    @Test
    void validPassword_accepted() {
        assertThat(InternalPasswordPolicy.isValid("GoodInternal9!")).isTrue();
        assertThat(InternalPasswordPolicy.violationMessage("GoodInternal9!")).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "short1!A",
                "aaaaaaaaaaa1!",
                "AAAAAAAAAAA1!",
                "NoDigitsHere!!",
                "NoSpecial1234Ab"
            })
    void invalidPassword_rejected(String weak) {
        assertThat(InternalPasswordPolicy.isValid(weak)).isFalse();
        assertThat(InternalPasswordPolicy.violationMessage(weak)).isNotBlank();
    }

    @Test
    void disallowedCharacter_rejected() {
        assertThat(InternalPasswordPolicy.isValid("GoodInternal9! ")).isFalse();
        assertThat(InternalPasswordPolicy.violationMessage("GoodInternal9! "))
                .containsIgnoringCase("allowed special");
    }

    @Test
    void validateOrThrow_propagatesReason() {
        assertThatThrownBy(() -> InternalPasswordPolicy.validateOrThrow("weak"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("length");
    }
}
