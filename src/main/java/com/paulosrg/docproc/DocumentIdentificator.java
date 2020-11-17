/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paulosrg.docproc;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author User
 */
public class DocumentIdentificator {
        
    public DocumentIdentificator(){
        
    }
    
    public String checkDocumentType(File f, Connection c) throws IOException, SQLException{
        PDDocument doc = PDDocument.load(f);
        PDFTextStripper textStripper = new PDFTextStripper();
        String[] extractedText = textStripper.getText(doc).split("[^\\p{L}\\p{Nd}]+");
        Set<String> foundTextOnDocument = new HashSet<>(Arrays.asList(extractedText));
        Set<String> dictionary = new HashSet<>();
        PreparedStatement ps = c.prepareStatement("SELECT palavra, valor_propriedade_id FROM dicionario_propriedade");
        ResultSet rs = ps.executeQuery();
        float highestPercentage = 0;
        int id = 0;
        
        while(rs.next()){
            Array words = rs.getArray("palavra");
            dictionary.addAll(Arrays.asList((String[])words.getArray()));
            int dictCount = dictionary.size();
            dictionary.retainAll(foundTextOnDocument);
            float currentPercentage = dictionary.size() / (float) dictCount;
            if(currentPercentage >= highestPercentage){
                highestPercentage = currentPercentage;
                id = rs.getInt("valor_propriedade_id");
            }
        }
        doc.close();
        ps = c.prepareStatement("SELECT valor FROM valor_lista_propriedade WHERE id = ?");
        ps.setInt(1, id);
        rs = ps.executeQuery();
        rs.next();
        String result = f.getName() + ": " + rs.getString("valor") + ": " + highestPercentage;
        return result;
    }
}
