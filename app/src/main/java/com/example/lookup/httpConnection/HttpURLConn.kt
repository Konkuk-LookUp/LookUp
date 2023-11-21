package com.example.lookup.httpConnection

import android.content.res.Resources.NotFoundException
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.concurrent.TimeoutException

class HttpURLConn {
    // GET
    fun GET(mUrl: String):ByteArray? {
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
            conn.setRequestProperty("Content-Type", "application/octet-stream; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/octet-stream")
            // 결과값 받아오기 수정 필요함
            val resCode = conn.responseCode
            if (resCode == HttpURLConnection.HTTP_OK) {
                //TODO bytestream 읽을 수 있도록 수정해야함
                conn.inputStream.use { inputstream ->
                    var line = inputstream.readBytes()
                    return line
                }
            }
            Log.d("check12","check")

        }catch (e:MalformedURLException){
            Log.d("check12", "malformedurl")
        }catch (e:NotFoundException){
            Log.d("check12", "notFoundEx")
        }catch (e:TimeoutException){
            Log.d("check12", "timeout")
        }catch (e: ProtocolException){
            Log.d("check12", "protocol exception")
        }catch (e: IOException){
            Log.d("check12", "io exception")
        }
        catch (e: Exception) {
            e.printStackTrace()
            Log.d("check12", "Check URL")
        }
        return null
    }

}