package com.ayishamart;

import com.ayishamart.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    void passwordHashAndCheckShouldWork() {

        String password = "Test@12345";

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        assertNotEquals(
                password,
                hashedPassword
        );

        assertTrue(
                PasswordUtil.checkPassword(
                        password,
                        hashedPassword
                )
        );

        assertFalse(
                PasswordUtil.checkPassword(
                        "WrongPassword",
                        hashedPassword
                )
        );
    }
}
