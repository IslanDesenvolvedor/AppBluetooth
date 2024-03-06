package com.icodelt.appbluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var onOffTextView: TextView
    private lateinit var pairedDevices: TextView
    private lateinit var visivelTextView: TextView

    companion object {
        private const val REQUEST_CODE_ENABLE_BT = 1
        private const val REQUEST_CODE_DISCOVERABLE_BT = 2
        private const val BLUETOOTH_PERMISSION_CODE = 3
    }

    @SuppressLint("MissingInflatedId", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onOffTextView = findViewById(R.id.onOffTextView)
        pairedDevices = findViewById(R.id.listDispositivosPareados)
        visivelTextView = findViewById(R.id.visibleTextView)
        val statusTextView: TextView = findViewById(R.id.statusTextView)
        val buttonOn: Button = findViewById(R.id.buttonOn)
        val buttonOff: Button = findViewById(R.id.buttonOff)
        val buttonVisivel: Button = findViewById(R.id.buttonVisivel)
        val buttonPaired: Button = findViewById(R.id.buttonPaired)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            statusTextView.text = "Bluetooth não Indisponível"
        } else {
            statusTextView.text = "Bluetooth disponível"
        }

        if (bluetoothAdapter.isEnabled) {
            onOffTextView.text = "Bluetooth ligado"
        } else {
            onOffTextView.text = "Bluetooth desligado"
        }

        buttonOn.setOnClickListener {
            if (bluetoothAdapter.isEnabled){
                Toast.makeText(this, "Bluetooth já está ligado", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }

        buttonOff.setOnClickListener {
            if (!bluetoothAdapter.isEnabled){
                Toast.makeText(this, "Bluetooth já está desligado", Toast.LENGTH_SHORT).show()
            } else {
                bluetoothAdapter.disable()
                onOffTextView.text = "Bluetooth desligado"
            }
        }

        buttonVisivel.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    BLUETOOTH_PERMISSION_CODE
                )
            } else {
                if (!bluetoothAdapter.isDiscovering) {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
                    visivelTextView.text = "Bluetooth visivel"
                } else {
                    visivelTextView.text = "Bluetooth não visivel"
                }
            }
        }

        buttonPaired.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                pairedDevices.text = "Dispositivos pareados"
                val devices = bluetoothAdapter.bondedDevices
                for (device in devices) {
                    val deviceName = device.name
                    val deviceAddress = device.address
                    pairedDevices.append("\nDispositivo: $deviceName, endereço: $deviceAddress")
                }
            } else {
                Toast.makeText(this, "Ligue o Bluetooth primeiro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    onOffTextView.text = "Bluetooth ligado"
                } else {
                    onOffTextView.text = "Bluetooth desligado"
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!bluetoothAdapter.isDiscovering) {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
                }
            }
        }
    }
}
