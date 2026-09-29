package com.example.a24012011023_mad_practical7

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpRequest {

    companion object {

        private const val TAG = "HttpRequest"
    }

    fun makeServiceCall(
        reqUrl: String?,
        token: String? = null
    ): String {

        var response: String = ""

        try {

            val url = URL(reqUrl)

            val conn =
                url.openConnection() as HttpURLConnection

            if (token != null) {

                conn.setRequestProperty(
                    "Authorization",
                    "Bearer $token"
                )

                conn.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )
            }

            conn.requestMethod = "GET"

            response =
                convertStreamToString(
                    BufferedInputStream(conn.inputStream)
                )

            conn.disconnect()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Exception: ${e.message}"
            )
        }

        return response
    }

    private fun convertStreamToString(
        inputStream: BufferedInputStream
    ): String {

        val reader =
            BufferedReader(
                InputStreamReader(inputStream)
            )

        val stringBuilder = StringBuilder()

        var line: String?

        while (
            reader.readLine().also {
                line = it
            } != null
        ) {

            stringBuilder.append(line)
        }

        reader.close()

        return stringBuilder.toString()
    }
}