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
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.io.IOException
import java.nio.file.Paths
import com.me.qrgenerator.ui.theme.QrGeneratorTheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.nio.file.Path

class MainActivity : ComponentActivity() {
    private lateinit var createfileLauncher: ActivityResultLauncher<Uri?>
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrGeneratorTheme {
                var link by remember { mutableStateOf("www.example.org") }
                var name by remember { mutableStateOf("NuovoQrCode") }
                var errore by remember { mutableStateOf(false) }
                var patternColor by remember { mutableStateOf(false) }
                var backgroundColor by remember { mutableStateOf(false) }
                var pColor by remember { mutableStateOf(Color.Black) }
                var bColor by remember { mutableStateOf(Color.Transparent) }
                val patternController = rememberColorPickerController()
                val backgroundController = rememberColorPickerController()
                if (patternColor){
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                    ) {
                        AlphaTile(
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .align(Alignment.CenterHorizontally)
                                .width(300.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(3.dp, Color.LightGray),
                            controller = patternController
                        )
                        HsvColorPicker(
                            initialColor = pColor,
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .fillMaxWidth()
                                .height(450.dp)
                                .padding(10.dp),
                            controller = patternController,
                            onColorChanged = { colorEnvelope: ColorEnvelope ->
                                pColor = colorEnvelope.color
                            }
                        )
                        AlphaSlider(
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(35.dp)
                                .border(3.dp, Color.LightGray),
                            controller = patternController
                        )
                        BrightnessSlider(
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(35.dp)
                                .border(3.dp, Color.LightGray),
                            controller = patternController
                        )
                        Button(
                            modifier = Modifier
                                .offset(y = 150.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                patternColor = false
                            }
                        ) {
                            Text( text = "Conferma colore" )
                        }
                    }
                }
                else if (backgroundColor){
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                    ) {
                        AlphaTile(
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .align(Alignment.CenterHorizontally)
                                .width(300.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(3.dp, Color.LightGray),
                            controller = backgroundController
                        )
                        HsvColorPicker(
                            initialColor = bColor,
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .fillMaxWidth()
                                .height(450.dp)
                                .padding(10.dp),
                            controller = backgroundController,
                            onColorChanged = { colorEnvelope: ColorEnvelope ->
                                bColor = colorEnvelope.color
                            }
                        )
                        AlphaSlider(
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(35.dp)
                                .border(3.dp, Color.LightGray),
                            controller = backgroundController
                        )
                        BrightnessSlider(
                            modifier = Modifier
                                .offset(y = 100.dp)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(35.dp)
                                .border(3.dp, Color.LightGray),
                            controller = backgroundController
                        )
                        Button(
                            modifier = Modifier
                                .offset(y = 150.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                backgroundColor = false
                            }
                        ) {
                            Text( text = "Conferma colore" )
                        }
                    }
                }
                else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(213, 232, 231)),
                    ) {
                        Text(
                            text = "Generatore di QR Codes:",
                            modifier = Modifier
                                .offset(y = 65.dp),
                            fontSize = 25.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Column(
                            modifier = Modifier
                                .offset(y = 125.dp)
                        ) {
                            Text(
                                text = "Link del qr code:",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            OutlinedTextField(
                                value = link,
                                singleLine = true,
                                modifier = Modifier
                                    .width(300.dp),
                                onValueChange = { text ->
                                    link = text
                                    if (text.isBlank())
                                        errore = true
                                    else if (name.isNotBlank())
                                        errore = false
                                }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .offset(y = 225.dp)
                        ) {
                            Text(
                                text = "Nome del qr code:",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            OutlinedTextField(
                                value = name,
                                singleLine = true,
                                modifier = Modifier
                                    .width(300.dp),
                                onValueChange = { text ->
                                    name = text
                                    if (text.isBlank())
                                        errore = true
                                    else if (link.isNotBlank())
                                        errore = false
                                }
                            )
                        }
                        Button(
                            modifier = Modifier
                                .offset(y = 325.dp),
                            onClick = {
                                patternColor = true
                            }
                        ) {
                            Text(text = "Colore del Pattern")
                        }
                        Button(
                            modifier = Modifier
                                .offset(y = 385.dp),
                            onClick = {
                                backgroundColor = true
                            }
                        ) {
                            Text(text = "Colore dello Sfondo")
                        }
                        Button(
                            modifier = Modifier
                                .offset(y = 560.dp),
                            onClick = {
                                if (!errore)
                                    salvaQrCode(this@MainActivity, "$name.png", createQr(link, pColor, bColor))
                            }
                        ) {
                            Text(
                                text = "Crea codice QR",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (errore)
                            Text(
                                text = "Compila tutti i campi per creare il qr code!!",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .offset(y = 165.dp)
                            )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createQr(link: String, onColor: Color, offColor: Color): Bitmap?{
    val mat: BitMatrix?
    try {
        mat = MultiFormatWriter().encode(link, BarcodeFormat.QR_CODE, 400, 400)

        val width = mat.width
        val height = mat.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (mat[x, y]) onColor.toArgb() else offColor.toArgb()
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