package com.example.examen3servidor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnOpenTetherSettings: Button
    private lateinit var statusTextView: TextView

    // Lanzador para solicitar permisos. Este es el método moderno y recomendado.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value }
            if (allPermissionsGranted) {
                // Si se conceden los permisos, se activa el botón.
                onPermissionsGranted()
            } else {
                // Si se deniegan, se informa al usuario.
                Toast.makeText(this, "Los permisos son necesarios para continuar.", Toast.LENGTH_LONG).show()
                statusTextView.text = "Permisos denegados. La app no puede funcionar."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenTetherSettings = findViewById(R.id.btnOpenTetherSettings)
        statusTextView = findViewById(R.id.statusTextView)

        // Al iniciar, se comprueban y solicitan los permisos.
        requestBluetoothPermissions()

        btnOpenTetherSettings.setOnClickListener {
            openTetherSettings()
        }
    }

    private fun requestBluetoothPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31) y superior
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            // Versiones anteriores
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Si faltan permisos, se lanzará la solicitud.
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Si ya están concedidos, se activa el botón directamente.
            onPermissionsGranted()
        }
    }

    private fun onPermissionsGranted() {
        statusTextView.text = "Permisos concedidos. Listo para activar."
        btnOpenTetherSettings.isEnabled = true
        Toast.makeText(this, "Permisos concedidos.", Toast.LENGTH_SHORT).show()
    }

    private fun openTetherSettings() {
        // Este Intent es la clave. Intenta abrir la pantalla de configuración de Anclaje de Red.
        // Es la forma más fiable de permitir que el usuario active el tethering.
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setClassName("com.android.settings", "com.android.settings.TetherSettings")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Si la ruta específica falla (puede pasar en algunos fabricantes),
            // se intenta abrir la configuración de red general.
            Toast.makeText(this, "No se pudo abrir la configuración específica. Abriendo ajustes de red.", Toast.LENGTH_LONG).show()
            try {
                startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la configuración de red.", Toast.LENGTH_SHORT).show()
                Log.e("SERVER_APP", "Error opening settings", e)
            }
        }
    }
}
