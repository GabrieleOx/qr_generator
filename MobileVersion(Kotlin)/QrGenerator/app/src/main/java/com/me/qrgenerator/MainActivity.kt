package com.me.qrgenerator

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.io.IOException
import java.nio.file.Paths
import com.me.qrgenerator.ui.theme.QrGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrGeneratorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QrGeneratorTheme {
        Greeting("Android")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createQr(path: String, ext: String, link: String, name: String){
    val mat: BitMatrix?
    var percorso = path
    if (percorso[percorso.length-1] != '\\')
        percorso += '\\'
    percorso += "$name.$ext"
    try {
        mat = MultiFormatWriter().encode(link, BarcodeFormat.QR_CODE, 400, 400)
        MatrixToImageWriter.writeToPath(mat, ext, Paths.get(percorso) as java.nio.file.Path)
        println("Qr code creato correttamente")
    } catch (e1: WriterException) {
        println("Errore nella creazione wr")
    } catch (e1: IOException) {
        println("Errore nella creazione")
    }
}