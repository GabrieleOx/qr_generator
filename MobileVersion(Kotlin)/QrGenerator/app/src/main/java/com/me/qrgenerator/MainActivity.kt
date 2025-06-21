package com.me.qrgenerator

import android.icu.text.CaseMap.Title
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.io.IOException
import java.nio.file.Paths
import com.me.qrgenerator.ui.theme.QrGeneratorTheme
import java.nio.file.Path

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrGeneratorTheme {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(245, 188, 95)),
                ) {

                    Text(
                        text = "Generatore di QR Codes:",
                        modifier = Modifier
                            .offset(y = 65.dp),
                        fontSize = 25.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
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
        MatrixToImageWriter.writeToPath(mat, ext, Paths.get(percorso) as Path)
        println("Qr code creato correttamente")
    } catch (e1: WriterException) {
        println("Errore nella creazione wr")
    } catch (e1: IOException) {
        println("Errore nella creazione")
    }
}