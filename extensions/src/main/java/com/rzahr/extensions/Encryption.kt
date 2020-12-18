package com.rzahr.extensions

import android.util.Base64
import android.util.Log
import java.security.SecureRandom
import java.util.HashMap
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object Security {

   /* val chars = charArrayOf('A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A'
                            'A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A'
                            'A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A','A', 'A', 'A', 'A', 'A', 'A', 'A'
                            'A', 'A', 'A', 'A', 'A', 'A', 'A')*/

    data class EncryptedData(val text: String?, val salt: String?, val iv: String?)

    fun String.encrypt(password: CharArray): EncryptedData {
        val map = encrypt(this.toByteArray(Charsets.UTF_8), password)
        return EncryptedData(Base64.encodeToString(map["encrypted"], Base64.NO_WRAP), Base64.encodeToString(map["salt"], Base64.NO_WRAP), Base64.encodeToString(map["iv"], Base64.NO_WRAP))
    }

    fun String.decrypt(password: CharArray, salt: String, iv: String): String? {
        return decrypt(Base64.decode(salt, Base64.NO_WRAP), Base64.decode(iv, Base64.NO_WRAP),
            Base64.decode(this, Base64.NO_WRAP), password)?.let { String(it, Charsets.UTF_8) }
    }

    private fun encrypt(dataToEncrypt: ByteArray, password: CharArray): HashMap<String, ByteArray> {
        val map = HashMap<String, ByteArray>()

        try {
            // 1
            //Random salt for next step
            val random = SecureRandom()
            val salt = ByteArray(256)
            random.nextBytes(salt)

            // 2
            //PBKDF2 - derive the key from the password, don't use passwords directly
            val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
            val keySpec = SecretKeySpec(keyBytes, "AES")

            // 3
            //Create initialization vector for AES
            val ivRandom = SecureRandom() //not caching previous seeded instance of SecureRandom
            val iv = ByteArray(16)
            ivRandom.nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            // 4
            //Encrypt
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encrypted = cipher.doFinal(dataToEncrypt)

            // 5
            map["salt"] = salt
            map["iv"] = iv
            map["encrypted"] = encrypted
        } catch (e: Exception) {
            Log.e("MYAPP", "encryption exception", e)
        }

        return map

    }

    private fun decrypt(salt: ByteArray, iv: ByteArray, encrypted: ByteArray, password: CharArray): ByteArray? {
        var decrypted: ByteArray? = null
        try {

            // 2
            //regenerate key from password
            val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
            val keySpec = SecretKeySpec(keyBytes, "AES")

            // 3
            //Decrypt
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            decrypted = cipher.doFinal(encrypted)
        } catch (e: Exception) {
            Log.e("MYAPP", "decryption exception", e)
        }

        return decrypted
    }
}
