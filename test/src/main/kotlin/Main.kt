package org.example

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.io.IOException
import java.nio.file.Paths


fun main() {
    println("Inserisci percorso di creazione, estensione, link e nome che vuoi dare al QrCode:")
    createQr(readln(), readln(), readln(), readln())
}

fun createQr(path: String, ext: String, link: String, name: String){
    val mat: BitMatrix?
    var path = path
    if (path[path.length-1] != '\\')
        path += '\\'
    path += name + '.' + ext
    try {
        mat = MultiFormatWriter().encode(link, BarcodeFormat.QR_CODE, 400, 400)
        MatrixToImageWriter.writeToPath(mat, ext, Paths.get(path) as java.nio.file.Path)
        println("Qr code creato correttamente")
    } catch (e1: WriterException) {
        println("Errore nella creazione wr")
    } catch (e1: IOException) {
        println("Errore nella creazione")
    }
}