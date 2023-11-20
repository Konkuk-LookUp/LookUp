package com.example.lookup.httpConnection

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class HttpURLConn {
    // Post
    fun POST(mUrl: String, params: JSONObject): JSONObject? {
        lateinit var conn : HttpURLConnection
        try {
            // String으로 받아온 URL을 URL 객체로 생성
            val url = URL(mUrl)

            // HttpURLConnection을 열어줌
            conn = url.openConnection() as HttpURLConnection

            // Timeout 설정
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            // 요청 방식 설정 (POST)
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            // Accept-Charset 설정 (UTF-8)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")

            // POST로 넘겨줄 파라미터를 OutputStream으로 전송
            conn.outputStream.use { os ->
                val writer = OutputStreamWriter(os,StandardCharsets.UTF_8)
                writer.use {
                    it.write(params.toString())
                }
            }
            // 결과값 받아오기
            val response = StringBuilder()
            val resCode = conn.responseCode
            Log.d("resCode",resCode.toString())
            if(resCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { br ->
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            response.append(line)
                            response.append('\n')
                        }
                    }
                }
                Log.d("HTTPResponse",response.toString().trim { it <= ' '})
                try {
                    val jsonResponse = JSONObject(response.toString())
                    // JSON 객체로부터 데이터를 추출하거나 조작하는 로직을 여기에 구현하세요

                    return jsonResponse
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("JSONError", "Error parsing JSON")
                }
                // 결과값 return json?
                return null
            }
            // 결과 처리

        } catch (e: Exception) {
            e.printStackTrace()
            // 오류 발생 시 로그 출력 후 null 반환
            Log.d("ERROR", "Exception Error")
        }finally {

        }
        return null
    }

    // GET
    fun GET(mUrl: String):JSONObject? {
        try {
            val url = URL(mUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 10000
                doInput = true
                doOutput = true
            }
            // 결과값 받아오기 수정 필요함
            val resCode = conn.responseCode
            val response = StringBuilder()

            if(resCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { br ->
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            response.append(line)
                            response.append('\n')
                        }
                    }
                }
                Log.d("HTTPResponse",response.toString().trim { it <= ' '})
                try {
                    val jsonResponse = JSONObject(response.toString())
                    // JSON 객체로부터 데이터를 추출하거나 조작하는 로직을 여기에 구현하세요.
                    return jsonResponse
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("JSONError", "Error parsing JSON")
                }
                return null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("ERROR", "Check URL")
        }
        return null
    }

}