package com.ishanknijhawan.notefy.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ishanknijhawan.notefy.Handler.FingerprintHandler
import com.ishanknijhawan.notefy.R
import kotlinx.android.synthetic.main.activity_finger_print.*
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey


class fingerPrintActivity : AppCompatActivity() {

    lateinit var cipher: Cipher
    lateinit var keyStore: KeyStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger_print)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            val keyguardService = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            if (!fingerprintManager.isHardwareDetected){
                tv_main_text.text = "Fingerprint scanner not detected"
            }
            else if (ContextCompat.checkSelfPermission(this,Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
                tv_main_text.text = "Permission not granted"
            }
            else if(!keyguardService.isKeyguardSecure){
                tv_main_text.text = "Please lock your phone"
            }
            else if (!fingerprintManager.hasEnrolledFingerprints()){
                tv_main_text.text = "No fingerprint registered"
            }
            else {
                tv_main_text.text = "Touch the fingerprint sensor"
                generateKey()

                if (cipherInit()) {
                    val cryptoObject: FingerprintManager.CryptoObject =
                        FingerprintManager.CryptoObject(cipher)
                    val fingerprintHandler =
                        FingerprintHandler(this)
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject)
                }
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun cipherInit(): Boolean {
        try {
            cipher =
                Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }
        return try {
            keyStore.load(null)
            val key: SecretKey = keyStore.getKey(
                "AndroidKey",
                null
            ) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            val keyGenerator: KeyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    "AndroidKey",
                    KeyProperties.PURPOSE_ENCRYPT or
                            KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
                    )
                    .build()
            )
            keyGenerator.generateKey()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
    }
}
