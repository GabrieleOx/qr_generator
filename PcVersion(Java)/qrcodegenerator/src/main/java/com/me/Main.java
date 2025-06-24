package com.me;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws WriterException, IOException {
        String percorsoBase;
        if(System.getProperty("os.name").contains("Windows"))
            percorsoBase = System.getProperty("user.home") + "\\Desktop";
        else percorsoBase = System.getProperty("user.home") + "/Desktop";
        Color [] colors = new Color[2];
        JFrame windowFrame = new JFrame("QR Generator");
        JLabel pathLabel = new JLabel("Dove?"), linkLabel = new JLabel("Link:"), fileNameLabel = new JLabel("Nome del file:"), colorsLabel = new JLabel("Colori qr code:");
        JTextField linkTextField = new JTextField("www.esempio.com"), fileNameTextField = new JTextField("ExampleQrCode"), pathTextField = new JTextField(percorsoBase);
        JFileChooser pathChooser = new JFileChooser("/", FileSystemView.getFileSystemView());
        JColorChooser colorChooser = new JColorChooser();
        JButton sendButton = new JButton("Crea"), pathButton = new JButton("Seleziona"), qrcodeColorButton = new JButton("Colore del pattern"), backgroundColorButton = new JButton("Colore dello sfondo");
        JPanel linkPanel = new JPanel(), pathPanel = new JPanel(), fileNamePanel = new JPanel(), colorsPanel = new JPanel(), colorChooserPanel = new JPanel(), selectionPanel = new JPanel();

        colors[0] = Color.black;
        colors[1] = null;

        //Graphical part

        windowFrame.setSize(585, 520);
        windowFrame.setResizable(false);
        windowFrame.getContentPane().setBackground(new Color(214, 144, 30));
        windowFrame.setLayout(null);
        windowFrame.add(linkPanel); windowFrame.add(pathPanel); windowFrame.add(fileNamePanel); windowFrame.add(colorsPanel); windowFrame.add(sendButton);

        linkPanel.setBounds(20, 20, 200, 100);
        linkPanel.setLayout(new GridLayout(0, 1));
        linkPanel.setBackground(null);
        linkLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        linkTextField.setFont(new Font("Verdana", Font.PLAIN, 16));
        linkTextField.setBackground(new Color(241, 245, 203));
        linkPanel.add(linkLabel); linkPanel.add(linkTextField);

        pathPanel.setBounds(300, 20, 250, 100);
        pathPanel.setLayout(new GridLayout(0, 1));
        pathPanel.setBackground(null);
        selectionPanel.setLayout(new GridLayout(1, 0));
        selectionPanel.setBackground(null);
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pathChooser.setDialogTitle("Seleziona destinazione");
        pathTextField.setFont(new Font("Verdana", Font.PLAIN, 16));
        pathTextField.setBackground(new Color(241, 245, 203));
        pathButton.setBackground(new Color(218, 224, 159));
        selectionPanel.add(pathTextField); selectionPanel.add(pathButton);
        pathLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        pathPanel.add(pathLabel); pathPanel.add(selectionPanel);

        fileNamePanel.setBounds(20, 200, 200, 100);
        fileNamePanel.setLayout(new GridLayout(0, 1));
        fileNamePanel.setBackground(null);
        fileNameLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        fileNameTextField.setFont(new Font("Verdana", Font.PLAIN, 16));
        fileNameTextField.setBackground(new Color(241, 245, 203));
        fileNamePanel.add(fileNameLabel); fileNamePanel.add(fileNameTextField);

        colorsPanel.setBounds(300, 200, 250, 100);
        colorsPanel.setLayout(new GridLayout(0, 1));
        colorsPanel.setBackground(null);
        colorChooserPanel.setLayout(new GridLayout(0, 1));
        colorChooserPanel.setBackground(null);
        qrcodeColorButton.setFont(new Font("Verdana", Font.BOLD, 16));
        qrcodeColorButton.setBackground(new Color(241, 245, 203));
        backgroundColorButton.setFont(new Font("Verdana", Font.BOLD, 16));
        backgroundColorButton.setBackground(new Color(241, 245, 203));
        colorChooserPanel.add(qrcodeColorButton);colorChooserPanel.add(backgroundColorButton);
        colorsLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        colorsPanel.add(colorsLabel); colorsPanel.add(colorChooserPanel);

        sendButton.setBounds(200, 360, 150, 50);
        sendButton.setFont(new Font("Verdana", Font.BOLD, 20));
        sendButton.setBackground(new Color(237, 69, 88));

        //Action part

        pathButton.addActionListener(ActionListener -> {
            pathTextField.setText("");
            pathChooser.showSaveDialog(windowFrame);
            pathTextField.setText(pathChooser.getSelectedFile().getPath());
        });

        sendButton.addActionListener(ActionListener -> {
            String path, link = linkTextField.getText(), name = fileNameTextField.getText();

            if(System.getProperty("os.name").contains("Windows"))
                path = pathTextField.getText() + '\\' + name + ".png";
            else path = pathTextField.getText() + '/' + name + ".png";

            try {
                generateQr(path, link, windowFrame, colors);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(windowFrame,"Errore nella creazione:\n" + e.getMessage(),"Alert",JOptionPane.WARNING_MESSAGE);
            }
            
            windowFrame.dispose();
        });

        qrcodeColorButton.addActionListener(ActionListener -> {
            colors[0] = JColorChooser.showDialog(windowFrame, "Colore Pattern", colors[0]);
        });

        backgroundColorButton.addActionListener(ActionListener -> {
            colors[1] = JColorChooser.showDialog(windowFrame, "Colore Sfondo", Color.white);
        });

        windowFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        windowFrame.setVisible(true);
    }

    private static void generateQr(String path, String link, JFrame window, Color [] colori) throws IOException{
        BitMatrix mat;
        int on = rgbToInt(colori[0].getRed(), colori[0].getBlue(), colori[0].getGreen(), colori[0].getAlpha()), off;

        if(colori[1] != null)
            off = rgbToInt(colori[1].getRed(), colori[1].getBlue(), colori[1].getGreen(), colori[1].getAlpha());
        else off = 0;

        MatrixToImageConfig config = new MatrixToImageConfig(on, off);
        
        try {
            mat = new MultiFormatWriter().encode(link, BarcodeFormat.QR_CODE, 400, 400);
            MatrixToImageWriter.writeToPath(mat, "png", Paths.get(path), config);
            JOptionPane.showMessageDialog(window,"Qr code creato correttamente","Alert",JOptionPane.WARNING_MESSAGE);
        } catch (WriterException e1) {
            JOptionPane.showMessageDialog(window,"Errore nella creazione:\n" + e1.getMessage(),"Alert",JOptionPane.WARNING_MESSAGE);
        }
    }

    private static int clamp(int val, int min, int max){
        return Math.max(min, Math.min(max, val));
    }
    private static int rgbToInt(int red, int blue, int green, int alpha){
        alpha = clamp(alpha, 0, 255);
        red = clamp(red, 0, 255);
        blue = clamp(blue, 0, 255);
        green = clamp(green, 0, 255);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
