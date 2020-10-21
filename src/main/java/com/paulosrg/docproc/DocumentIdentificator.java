/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paulosrg.docproc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author User
 */
public class DocumentIdentificator {
    
    private HashMap<String, Set> dictionaries;
    
    public DocumentIdentificator(){
        dictionaries = new HashMap<>();
    }
    
    public DocumentIdentificator(File dictionary) {
        try {
            this.loadDictionary(dictionary);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentIdentificator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object[] checkDocumentType(File file) throws IOException{
        PDDocument doc = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();
        String[] extractedText = textStripper.getText(doc).split("[^\\p{L}\\p{Nd}]+");
        Set<String> docSet = new HashSet<>(Arrays.asList(extractedText));
        //HashMap<String, Float> results = new HashMap<>();
        String tipoDeDocumento = "";
        float porcentagem = 0;
        for(Iterator<Map.Entry<String, Set>> it = dictionaries.entrySet().iterator(); it.hasNext(); ){
            Map.Entry<String, Set> current = it.next();
            Set<String> currentType = (Set<String>)((HashSet)current.getValue()).clone();
            currentType.retainAll(docSet);
            float pc = (currentType.size()/(float)current.getValue().size())*100;
            if(pc > porcentagem){
                porcentagem = pc;
                tipoDeDocumento = current.getKey();
            }
        }

        doc.close();
        //return results.toString();
        return new Object[]{tipoDeDocumento, porcentagem};
    }
    
    public void loadDictionary(File jsonfile) throws FileNotFoundException{
        JsonObject jsobj = new Gson().fromJson(new InputStreamReader(new FileInputStream(jsonfile), Charset.forName("UTF-8")), JsonObject.class);
        dictionaries = new HashMap<>();
        for (Entry<String, JsonElement> nextElement : jsobj.entrySet()) {
            String docName = nextElement.getKey();
            ArrayList<String> values = new ArrayList<>();
            for (JsonElement nextValue : nextElement.getValue().getAsJsonArray()) {
                values.add(nextValue.getAsString());
            }
            dictionaries.put(docName, new HashSet<>(values));
        }
    }
    
}
