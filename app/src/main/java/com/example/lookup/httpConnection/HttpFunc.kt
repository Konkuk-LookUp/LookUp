package com.example.lookup.httpConnection

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class HttpFunc(private val context: Context) {
    // data를 주고받기 위한 scope 생성
    private val scope = CoroutineScope(Dispatchers.Main)
    companion object{
        var baseurl = "http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/"
    }

    fun GET(obj:String) {
        scope.launch {
            var response:ByteArray? = null
            var url = baseurl+ obj
            // 백그라운드 작업을 시작하기 전에 실행되는 부분 (예: Dialog 표시)
            val getResult = withContext(Dispatchers.IO) {
                // 네트워크 통신을 위해 IO 스레드에서 실행
                try {
                    response = HttpURLConn().GET(url)
//                    Log.d("check12",url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            // onPostExecute에 해당하는 작업 수행 (예: 결과를 UI에 업데이트)
            getResult?.let {
                // UI 업데이트 로직
                if(response !=null){
                    val file = File(context.filesDir, "client.obj")
                    FileOutputStream(file).use { outputStream ->
                        outputStream.write(response) // data는 바이트 배열입니다.
                    }
                }

            }
        }
    }

    fun cancel() {
        scope.cancel()
        // 작업이 취소되었을 때 수행되는 부분 (예: 취소 관련 UI 처리)
    }
}