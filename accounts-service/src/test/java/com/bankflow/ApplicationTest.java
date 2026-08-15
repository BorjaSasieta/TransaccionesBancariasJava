package com.bankflow;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

    @Test
    void application_has_main_method() throws Exception {
        Method main = Application.class.getMethod("main", String[].class);
        assertThat(main).isNotNull();
    }

    @Test
    void application_is_public() {
        assertThat(Modifier.isPublic(Application.class.getModifiers())).isTrue();
    }
}