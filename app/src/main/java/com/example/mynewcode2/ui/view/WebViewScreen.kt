package com.example.mynewcode2.ui.view

import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen() {
    // 로딩 상태를 추적하는 변수
    val isLoading = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 로딩 중일 때 CircularProgressIndicator를 화면에 표시
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Text(
                text = "페이지 로딩 완료",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // WebView를 통해 웹 페이지를 로드
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    // JavaScript 활성화
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true // 로컬 스토리지 사용 허용

                    webViewClient = object : WebViewClient() {
                        // 페이지가 로딩을 완료했을 때
                        override fun onPageFinished(view: WebView, url: String?) {
                            super.onPageFinished(view, url)
                            // 페이지 로딩 완료 후 로딩 상태 업데이트
                            isLoading.value = false
                            Log.d("WebView", "Page finished loading: $url")
                        }

                        // 페이지 로딩이 시작되었을 때
                        override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            // 페이지 로딩 시작 시 로딩 상태 업데이트
                            isLoading.value = true
                            Log.d("WebView", "Page started loading: $url")
                        }

                        // 페이지 로딩 중 오류가 발생했을 때
                        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: android.webkit.WebResourceError) {
                            super.onReceivedError(view, request, error)
                            // 오류 발생 시 로딩 상태 업데이트
                            isLoading.value = false
                            Log.e("WebView", "Page loading error: ${error.description}")
                        }
                    }

                    webChromeClient = WebChromeClient() // 웹 페이지에서의 클라이언트 동작 관리
                    loadUrl("https://map.naver.com/") // 네이버 지도 URL 로드
                    Log.d("WebView", "Loading URL: https://map.naver.com/") // 로드 URL 로깅
                }
            }
        )
    }
}
