package com.example.examen3cliente

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        // --- Configuración del WebView ---
        // Habilitamos JavaScript y DOM Storage, crucial para webs modernas.
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Usamos un WebViewClient personalizado para manejar eventos y errores.
        webView.webViewClient = MyWebViewClient()

        // Cargamos una URL directamente al iniciar la app.
        webView.loadUrl("https://www.google.com")
    }

    /**
     * WebViewClient personalizado para obtener más información sobre la carga de la página
     * y poder depurar errores fácilmente.
     */
    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("WebView", "Página empezó a cargar: $url")
            Toast.makeText(this@MainActivity, "Cargando: $url", Toast.LENGTH_SHORT).show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("WebView", "Página terminó de cargar: $url")
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            // Este callback es crucial. Se activa si hay un error de red o de carga.
            val errorMessage = "Error al cargar web: ${error?.description}"
            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            Log.e("WebView", "Error: ${error?.errorCode} - ${error?.description}")
        }
    }

    /**
     * Maneja el botón "Atrás" del sistema para navegar hacia atrás en el historial
     * del WebView si es posible.
     */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
