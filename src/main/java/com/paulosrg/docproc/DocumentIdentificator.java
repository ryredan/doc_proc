/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paulosrg.docproc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author User
 */
public class DocumentIdentificator {
    
    private HashMap<String, Set> dictionaries = new HashMap<>();

    public DocumentIdentificator() {

        dictionaries.put("Cartão de Ponto", new HashSet<>(Arrays.asList(new String[]{
            "Cartão", "Ponto",
            "Empregador", "Atividade", "Endereço.", "Empregado",
            "Cargo", "Setor", "Periodo", "DATA",
            "SEM", "ENTRADA", "SAIDA", "OBSERVAÇÕES",
            "SEG", "TER", "QUA", "QUI",
            "SEX", "SAB", "DOM"
        })));
        
        dictionaries.put("Termo de Responsibilidade", new HashSet<>(Arrays.asList(new String[]{
            "TERMO", "RESPONSABILIDADE", "RECEBIMENTO", "CRACHÁ",
            "portador", "recebi", "identificação", "conhecimento",
            "seguintes", "normas", "utilização", "funcional"
        })));
        
        dictionaries.put("Folha de Pagamento", new HashSet<>(Arrays.asList(new String[]{
            "Folha", "Mensal", "Código", "Nome",
            "Funcionário", "Descrição", "Referência", "Vencimentos",
            "Descontos", "Salário", "Base", "F.G.T.S",
            "Faixa", "IRRF"
        })));
    }
    
    public String checkDocumentType(File file) throws IOException{
        PDDocument doc = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();
        String[] extractedText = textStripper.getText(doc).split("[^\\p{L}\\p{Nd}]+");
        Set<String> docSet = new HashSet<>(Arrays.asList(extractedText));
        HashMap<String, Float> results = new HashMap<>();
        for(Iterator<Map.Entry<String, Set>> it = dictionaries.entrySet().iterator(); it.hasNext(); ){
            Map.Entry<String, Set> current = it.next();
            Set<String> currentType = (Set<String>)((HashSet)current.getValue()).clone();
            currentType.retainAll(docSet);
            results.put(current.getKey(), (currentType.size()/(float)current.getValue().size())*100);
        }
        doc.close();
        return results.toString();
        
    }
    
}
