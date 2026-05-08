package com.almanatura.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almanatura.api.config.AppProperties;

/** Unit tests for {@link DniCipherService} AES-256-GCM encryption/decryption. */
class DniCipherServiceTest {

    private static final String VALID_32_BYTE_KEY =
            Base64.getEncoder()
                    .encodeToString("0123456789abcdef0123456789abcdef".getBytes()); // exactly 32
    // bytes

    private DniCipherService service;

    @BeforeEach
    void setUp() {
        AppProperties props =
                new AppProperties(
                        null, // jwt
                        new AppProperties.Encryption(VALID_32_BYTE_KEY),
                        null, // cors
                        null, // admin
                        null // rateLimit
                        );
        service = new DniCipherService(props);
    }

    @Test
    @DisplayName("encrypt returns non-null for non-null input")
    void testEncryptNonNull() {
        String plaintext = "12345678X";
        String ciphertext = service.encrypt(plaintext);

        assertThat(ciphertext).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("encrypt returns null for null input")
    void testEncryptNull() {
        String ciphertext = service.encrypt(null);

        assertThat(ciphertext).isNull();
    }

    @Test
    @DisplayName("decrypt recovers original plaintext after encrypt")
    void testEncryptDecryptRoundTrip() {
        String original = "87654321Y";
        String encrypted = service.encrypt(original);
        String decrypted = service.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("decrypt returns null for null input")
    void testDecryptNull() {
        String plaintext = service.decrypt(null);

        assertThat(plaintext).isNull();
    }

    @Test
    @DisplayName("each encryption produces different ciphertext (different IV)")
    void testEncryptProducesDifferentCiphertexts() {
        String plaintext = "same text";
        String cipher1 = service.encrypt(plaintext);
        String cipher2 = service.encrypt(plaintext);

        // Same plaintext should produce different ciphertexts due to random IV
        assertThat(cipher1).isNotEqualTo(cipher2);

        // But both should decrypt to same plaintext
        assertThat(service.decrypt(cipher1)).isEqualTo(plaintext);
        assertThat(service.decrypt(cipher2)).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("ciphertext is longer than plaintext (GCM overhead)")
    void testCiphertextLongerThanPlaintext() {
        String plaintext = "12345678";
        String encrypted = service.encrypt(plaintext);

        // Base64 decodes to: 12-byte IV + ciphertext + 16-byte GCM tag
        byte[] decrypted = Base64.getDecoder().decode(encrypted);
        int plaintextBytes = plaintext.getBytes().length;
        int ciphertextBytes = decrypted.length;

        // IV (12) + plaintext (8) + tag (16) = 36 bytes, Base64 encoded
        assertThat(ciphertextBytes).isGreaterThan(plaintextBytes);
    }

    @Test
    @DisplayName("invalid ciphertext throws IllegalStateException on decrypt")
    void testDecryptInvalidCiphertext() {
        String invalidBase64 = Base64.getEncoder().encodeToString("tooshort".getBytes());

        assertThatThrownBy(() -> service.decrypt(invalidBase64))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to decrypt value");
    }

    @Test
    @DisplayName("constructor validates key length is exactly 32 bytes")
    void testConstructorValidatesKeyLength() {
        // Key that decodes to 16 bytes (too short for AES-256)
        String short16ByteKey = Base64.getEncoder().encodeToString("0123456789abcdef".getBytes());
        AppProperties propsShort =
                new AppProperties(
                        null, // jwt
                        new AppProperties.Encryption(short16ByteKey),
                        null, // cors
                        null, // admin
                        null // rateLimit
                        );

        assertThatThrownBy(() -> new DniCipherService(propsShort))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must decode to exactly 32 bytes");
    }
}
