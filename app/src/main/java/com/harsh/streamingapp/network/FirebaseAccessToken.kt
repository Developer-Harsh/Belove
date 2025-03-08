package com.harsh.streamingapp.network

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.harsh.streamingapp.utils.Constants
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object FirebaseAccessToken {
    fun getAccessToken(): String? {
        try {
            val json = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"videortc-d83ba\",\n" +
                    "  \"private_key_id\": \"30d7fe9db6bed6d970574e74cf92de90b96687ca\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDINaXP47wqZMp7\\n3/hSSu+Tnd0rwbbjHBixX0Sy6BzzmbPmKT36AhF7srLmel+/vTdCWq54oCEicPAO\\n9DFDlCT7gswHUclwCJ9+Rqx2RGQbIwWuLMXDilOwn2xylMywxcs16g6x4ng2G/a8\\nJnID+uNC9ULcqhZzBJzCPj8VGM40E3bnUhzF5sxSS3UdrVe+XncL8cAhPG1lWDke\\n/XPX+L3/v28HW3m4C33QXY0uidlLn0WQNJzrwUPSxSrLy7y+aCVPZiHWxV5yxuzT\\nM8S7wzy6bZ05gO+bSje3OIBev6n9RuSWxg3Tg5RVI01ET2uuxoiPdjEqjyUPVF2z\\n9URaKfwfAgMBAAECggEAYC0ZVrkdSrY5aPGKDiouVZjXxu3bR9VW7VoJG/KR+dmj\\nyJSWT7PPpz8MXth5h5fDZG5AcvTlhfB/+YcH5WFUtLe8AGYjIN0blSMpNPUcq8aS\\ns32RkSIxmt6yz4Y4oFCcEm26Gmzi94dOa0sCqXPL2X4NXQieGSmljlAtqJpOcEfx\\nGU+nK6ZyIiB0720oYRjxA+90+MHSHtyRSKMtIChzZ/z9EZqps/0WFtEYAy9c+n1+\\nmXnKzYcWJ7aC1XnnY4b0qvo29Y9j8eepmNe4FBy6A4RU9/yCtrwnKXvWJulBo0S4\\n72VKKWQQGIhgjjSPHUseyS+MTkiVjTLMk5qdUcg7mQKBgQDPkVFqyOKg9q/2Qc+e\\nW8gXbHQcXo4Kc69Qol8mQZem/WLDlT/jNtuSd0zId6X67IUTgboMfBMS1yPycVVC\\nbRUav8ymrml0TrFRM7c4SXqokVsFhA+CBr6qWmMtUrE1xP4FCZU7iDab+PvyAmbF\\ntPVLEWw/3klP0tM5QIuv8G/kaQKBgQD27M64HfXKYpE2dw0OHgpQzN93autqFxAs\\nSBGhSUvK/xcE1JxjbfAEbR7QGri4uEKpRdKY/wHWrynxXISk27IB6eN8hHc9kE5B\\niLc68RJLv1gShTL0xtPNKlQcE31JNDpp7atNPrtKVCxCUUW3ABbBefAWqd8PbE6E\\nef0/mSQrRwKBgAakPres/9toqU624l7NzwQ42ypBOEhLVVO+Lk86EBEu0Gczslyh\\n17rDCHjtoyj5QnTSVNKrcazLp4HNcI30kZVOh8zZjdRD5FaV0goCB+O26vOtOxXN\\nA3LN+5hqqfLvf7VHjjfebujkuaMPwpwW4zZos08D3NKqC0osr4uMpPfpAoGBAMce\\nxcChoGC5RaKnNGbrAKSR4a7aEMc3m/opj9YBVlZ3iKeDkC16LTBUkqF8uvNy24bH\\nYIGyOlhWXrtdQTX0WELEyobSB5oksYaVajTkzD07KxmcQjAz2f67oKIXcVPEA0sC\\ntr4O1VemMx6zjUV6k98npt2P1OoKC/M2sGtF4m3nAoGAHBTb9Y03HXFiOY9Pbh3N\\ny6GPWBxMEFO51qiHsftfjJekNxXq6wyInFFa379p0974IHJ6ihKHoAYCRJzAoQHI\\n6yC5Pu7fOZ8zNv3bPSMQyxxpjC0GcSSR2OIsPlGdhPWatDgxUTv+Z6wO8PzsFB0F\\n7A7GehsXLEwiZXUu9Z1ODwM=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-h2q9p@videortc-d83ba.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"117961141808673962074\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-h2q9p%40videortc-d83ba.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            val stream = ByteArrayInputStream(json.toByteArray(StandardCharsets.UTF_8))
            val googleCreds = GoogleCredentials.fromStream(stream).createScoped(arrayListOf(
                Constants.FCM_SCOPE))
            googleCreds.refresh()

            return googleCreds.accessToken.tokenValue
        } catch (e: IOException) {
            Log.e("TAG", e.message.toString())
            return null
        }
    }
}