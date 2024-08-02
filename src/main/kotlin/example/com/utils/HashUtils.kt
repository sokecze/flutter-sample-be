package example.com.utils

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val HASH_SIZE_BYTES = 32
private const val SALT_SIZE_BYTES = 15

@OptIn(ExperimentalStdlibApi::class)
fun String.generatePasswordHash(salt: String? = null): String {
    val iterations = 120_000
    val keyLength = HASH_SIZE_BYTES * 8

    val generatedSalt: ByteArray
    if (salt == null) {
        generatedSalt = ByteArray(SALT_SIZE_BYTES)
        SecureRandom().nextBytes(generatedSalt)
    } else {
        generatedSalt = salt.hexToByteArray()
    }

    val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    val spec: KeySpec = PBEKeySpec(this.toCharArray(), generatedSalt, iterations, keyLength)

    return "${factory.generateSecret(spec).encoded.toHexString()}${generatedSalt.toHexString()}"
}

fun String.getSalt(): String = substring(HASH_SIZE_BYTES * 2)
