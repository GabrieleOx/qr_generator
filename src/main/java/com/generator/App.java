package com.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public final class App {
    private App() {
    }

    public static void main(String[] args) throws WriterException, IOException {
        JFrame windowFrame = new JFrame("QR Generator");
        JLabel pathLabel = new JLabel("Dove?"), linkLabel = new JLabel("Link:"), fileNameLabel = new JLabel("Nome del file:"), extensionLabel = new JLabel("Estensione del file:");
        JTextField linkTextField = new JTextField("www.esempio.com"), fileNameTextField = new JTextField("ExampleQrCode"), pathTextField = new JTextField("_Path_");
        JFileChooser pathChooser = new JFileChooser("/", FileSystemView.getFileSystemView());
        JCheckBox jpgCheckBox = new JCheckBox(".JPG"), pngCheckBox = new JCheckBox(".PNG");
        JButton sendButton = new JButton("Crea"), pathButton = new JButton("Seleziona");
        JPanel linkPanel = new JPanel(), pathPanel = new JPanel(), fileNamePanel = new JPanel(), extensionPanel = new JPanel(), checkBoxPanel = new JPanel(), selectionPanel = new JPanel();

        //Graphical part

        windowFrame.setSize(585, 520);
        windowFrame.setResizable(false);
        windowFrame.getContentPane().setBackground(new Color(214, 144, 30));
        windowFrame.setLayout(null);
        windowFrame.add(linkPanel); windowFrame.add(pathPanel); windowFrame.add(fileNamePanel); windowFrame.add(extensionPanel); windowFrame.add(sendButton);

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

        extensionPanel.setBounds(330, 200, 250, 100);
        extensionPanel.setLayout(new GridLayout(0, 1));
        extensionPanel.setBackground(null);
        checkBoxPanel.setLayout(new GridLayout(1, 0));
        checkBoxPanel.setBackground(null);
        jpgCheckBox.setFont(new Font("Verdana", Font.PLAIN, 16));
        jpgCheckBox.setBackground(null);
        pngCheckBox.setFont(new Font("Verdana", Font.PLAIN, 16));
        pngCheckBox.setBackground(null);
        pngCheckBox.setSelected(true);
        checkBoxPanel.add(jpgCheckBox); checkBoxPanel.add(pngCheckBox);
        extensionLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        extensionPanel.add(extensionLabel); extensionPanel.add(checkBoxPanel);

        sendButton.setBounds(200, 360, 150, 50);
        sendButton.setFont(new Font("Verdana", Font.BOLD, 20));
        sendButton.setBackground(new Color(237, 69, 88));

        //Action part

        pathButton.addActionListener(ActionListener -> {
            pathTextField.setText("");
            pathChooser.showSaveDialog(windowFrame);
            pathTextField.setText(pathChooser.getSelectedFile().getPath());
        });

        jpgCheckBox.addActionListener(ActionListener -> {
            pngCheckBox.setSelected(!pngCheckBox.isSelected());
        });

        pngCheckBox.addActionListener(ActionListener -> {
            jpgCheckBox.setSelected(!jpgCheckBox.isSelected());
        });

        sendButton.addActionListener(ActionListener -> {
            String path, link = linkTextField.getText(), name = fileNameTextField.getText(), ext;
            BitMatrix mat;

            if(jpgCheckBox.isSelected())
                ext = "jpg";
            else if(pngCheckBox.isSelected())
                ext = "png";
            else ext = "jpeg"; // as a default (you never know...)

            path = pathTextField.getText() + '\\' + name + '.' + ext;

            try {
                mat = new MultiFormatWriter().encode(link, BarcodeFormat.QR_CODE, 400, 400);
                MatrixToImageWriter.writeToPath(mat, ext, Paths.get(path));
                JOptionPane.showMessageDialog(windowFrame,"Qr code creato correttamente","Alert",JOptionPane.WARNING_MESSAGE);
            } catch (WriterException | IOException e1) {
                JOptionPane.showMessageDialog(windowFrame,"Errore nella creazione:\n" + e1.getMessage(),"Alert",JOptionPane.WARNING_MESSAGE);
            }
            windowFrame.dispose();
        });

        windowFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        windowFrame.setVisible(true);
    }
}
