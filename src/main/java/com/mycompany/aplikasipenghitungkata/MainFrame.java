/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aplikasipenghitungkata;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author ASUS
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        addListeners();
    }
    
        private void initComponents() {
        btnHitung = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaInput = new javax.swing.JTextArea();
        tfCari = new javax.swing.JTextField();
        lblKata = new javax.swing.JLabel();
        lblKarakter = new javax.swing.JLabel();
        lblKalimat = new javax.swing.JLabel();
        lblParagraf = new javax.swing.JLabel();
        lblHasilCari = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Penghitung Kata");

        textAreaInput.setColumns(20);
        textAreaInput.setLineWrap(true);
        textAreaInput.setRows(10);
        textAreaInput.setWrapStyleWord(true);
        jScrollPane1.setViewportView(textAreaInput);

        btnHitung.setText("Hitung");
        btnSimpan.setText("Simpan Hasil");
        btnCari.setText("Cari");

        lblKata.setText("Kata: 0");
        lblKarakter.setText("Karakter (inkl. spasi): 0");
        lblKalimat.setText("Kalimat: 0");
        lblParagraf.setText("Paragraf: 0");
        lblHasilCari.setText("Hasil cari: -");
        lblStatus.setText("Status: Siap");

        // Layout sederhana (gunakan GroupLayout di NetBeans GUI builder)
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(12,12,12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblKata)
                    .addComponent(lblKarakter)
                    .addComponent(lblKalimat)
                    .addComponent(lblParagraf)
                    .addComponent(lblHasilCari)
                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari)
                    .addComponent(btnHitung)
                    .addComponent(btnSimpan)
                    .addComponent(lblStatus))
                .addContainerGap()
            )
        );
        
         pack();
        setLocationRelativeTo(null);
    }
        
            private void addListeners() {
        // DocumentListener untuk real-time update
        textAreaInput.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCounts(); }
            public void removeUpdate(DocumentEvent e) { updateCounts(); }
            public void changedUpdate(DocumentEvent e) { updateCounts(); }
        });

        btnHitung.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCounts();
                lblStatus.setText("Status: Dihitung (manual)");
            }
        });

        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String term = tfCari.getText().trim();
                if (term.isEmpty()) {
                    lblHasilCari.setText("Hasil cari: masukkan kata");
                    return;
                }
                int found = searchOccurrences(textAreaInput.getText(), term);
                lblHasilCari.setText("Hasil cari: " + found);
                lblStatus.setText("Status: Pencarian selesai");
            }
        });

        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveResultToFile();
            }
        });
    }
            
    private void updateCounts() {
        String s = textAreaInput.getText();
        int words = countWords(s);
        int charsWithSpaces = countCharactersWithSpaces(s);
        int sentences = countSentences(s);
        int paragraphs = countParagraphs(s);

        lblKata.setText("Kata: " + words);
        lblKarakter.setText("Karakter (inkl. spasi): " + charsWithSpaces);
        lblKalimat.setText("Kalimat: " + sentences);
        lblParagraf.setText("Paragraf: " + paragraphs);
    }

    private int countWords(String s) {
        if (s == null) return 0;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return 0;
        String[] parts = trimmed.split("\\s+");
        return parts.length;
    }

    private int countCharactersWithSpaces(String s) {
        if (s == null) return 0;
        return s.length();
    }

    private int countSentences(String s) {
        if (s == null) return 0;
        // split by . ! ? (sangat sederhana, tapi efektif untuk tugas)
        String[] parts = s.split("[.!?]+");
        int count = 0;
        for (String p : parts) {
            if (p.trim().length() > 0) count++;
        }
        return count;
    }

    private int countParagraphs(String s) {
        if (s == null) return 0;
        // hitung blok yang tidak kosong dipisah oleh baris kosong
        String[] parts = s.split("\\r?\\n\\s*\\r?\\n");
        int count = 0;
        for (String p : parts) {
            if (p.trim().length() > 0) count++;
        }
        return count;
    }

    
     private int searchOccurrences(String s, String term) {
        if (s == null || term == null || term.isEmpty()) return 0;
        // cari kata utuh, case-insensitive
        Pattern p = Pattern.compile("\\b" + Pattern.quote(term) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        int count = 0;
        while (m.find()) count++;
        return count;
    }

    private void saveResultToFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            lblStatus.setText("Status: Simpan dibatalkan");
            return;
        }
        File f = chooser.getSelectedFile();
        try (BufferedWriter bw = Files.newBufferedWriter(f.toPath())) {
            bw.write("Teks:\n");
            bw.write(textAreaInput.getText() + "\n\n");
            bw.write(lblKata.getText() + "\n");
            bw.write(lblKarakter.getText() + "\n");
            bw.write(lblKalimat.getText() + "\n");
            bw.write(lblParagraf.getText() + "\n");
            bw.write(lblHasilCari.getText() + "\n");
            lblStatus.setText("Status: Disimpan ke " + f.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            lblStatus.setText("Status: Error simpan");
        }
    }

            

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaInput = new javax.swing.JTextArea();
        btnHitung = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        tfCari = new javax.swing.JTextField();
        lblKata = new javax.swing.JLabel();
        lblKarakter = new javax.swing.JLabel();
        lblKalimat = new javax.swing.JLabel();
        lblParagraf = new javax.swing.JLabel();
        lblHasilCari = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        textAreaInput.setColumns(20);
        textAreaInput.setRows(5);
        jScrollPane2.setViewportView(textAreaInput);

        jScrollPane1.setViewportView(jScrollPane2);

        btnHitung.setText("Hitung");

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnCari.setText("Cari");

        lblKata.setText("Kata : 0");

        lblKarakter.setText("Karakter (inkl. spasi): 0");

        lblKalimat.setText("Kalimat: 0");

        lblParagraf.setText("Paragraf: 0");

        lblHasilCari.setText("Hasil cari: -");

        lblStatus.setText("Status: Siap");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblKalimat)
                            .addComponent(lblParagraf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblHasilCari, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnSimpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHitung, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCari, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(33, 33, 33))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblKarakter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblKata, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblKata)
                        .addGap(18, 18, 18)
                        .addComponent(lblKarakter)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHitung)
                    .addComponent(lblKalimat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(lblParagraf))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCari)
                    .addComponent(lblHasilCari))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHitung;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHasilCari;
    private javax.swing.JLabel lblKalimat;
    private javax.swing.JLabel lblKarakter;
    private javax.swing.JLabel lblKata;
    private javax.swing.JLabel lblParagraf;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextArea textAreaInput;
    private javax.swing.JTextField tfCari;
    // End of variables declaration//GEN-END:variables
}
