package com.me.qrgenerator

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    private lateinit var createfileLauncher: ActivityResultLauncher<Uri?>
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrGeneratorTheme {
                var link by remember {
                    mutableStateOf("www.example.org")
                }
                var name by remember{
                    mutableStateOf("NuovoQrCode")
                }
                var errore by remember{
                    mutableStateOf(false)
                }
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
                    Column (
                        modifier = Modifier
                            .offset(y = 225.dp)
                    ) {
                        Text(
                            text = "Link del qr code:",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        OutlinedTextField(
                            value = link,
                            onValueChange = { text ->
                                link = text
                            }
                        )
                    }
                    Column (
                        modifier = Modifier
                            .offset(y = 325.dp)
                    ) {
                        Text(
                            text = "Nome del qr code:",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { text ->
                                name = text
                            }
                        )
                    }
                    Button(
                        modifier = Modifier
                            .offset(y = 400.dp),
                        onClick = {
                            if(name.isBlank() || link.isBlank())
                                errore = true
                            else{
                                errore = false
                                salvaQrCode(this@MainActivity, "$name.png", createQr(link))
                            }
                        }
                    ) {
                        Text(text = "Crea codice QR")
                    }
                    if(errore)
                        Text(
                            text = "Compila tutti i campi per creare il qr code!!",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .offset(y = 420.dp)
                        )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createQr(link: String): Bitmap?{
    val mat: BitMatrix?
    try {
        mat = MultiFormatWriter().encode(link, BarcodeFormat.QR_CODE, 400, 400)

        val width = mat.width
        val height = mat.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (mat[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                bitmap.setPixel(x, y, color)
            }
        }
        return bitmap
    } catch (e1: WriterException) {
    } catch (e1: IOException) {
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.Q)
fun salvaQrCode(context: Context, fileName: String, bitmap: Bitmap?){
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)  // nome del file
        put(MediaStore.Downloads.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/")  // salva in Download
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        if (bitmap != null) {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        Toast.makeText(context, "Immagine salvata in Download", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
    }
}