/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.rel;

import main.Gerador;
import main.email.MailJava;
import main.email.MailJavaSender;
import connection.ConnectionFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author Edenilson
 */
public class Gera_Internacao {

    public static void Internacao() {

        try {
            Connection conn = ConnectionFactory.getConnection();

            PreparedStatement stmt = null;
            ResultSet rs = null;

            stmt = conn.prepareStatement("Select"
                    + " DATE(arqatend.datatend) AS DATA_INTERNACAO,"
                    + " arqint.codlei As LEITO,"
                    + " arqatend.numatend As ATENDIMENTO,"
                    + " cadpac.codpac As PRONTUARIO,"
                    + " cadpac.nomepac As PACIENTE,"
                    + " cadpac.sexo As SEXO,"
                    + " tabppadr.codsppadr As CODIGO_PROCEDIMENTO,"
                    + " tabppadr.descrsp As PROCEDIMENTO,"
                    + " tabppadr.qtdmax As DIAS_SUS,"
                    + " DATE (NOW()) - DATE(arqatend.datatend) AS DIAS_INTERNADOS,"
                    + " cadprest.nomeprest As MEDICO_RESPONSAVEL,"
                    + " cadcc.codcc,"
                    + " cadcc.nomecc"
                    + " From"
                    + " arqint Inner Join"
                    + " arqatend On arqint.numatend = arqatend.numatend Inner Join"
                    + " cadpac On arqatend.codpac = cadpac.codpac Inner Join"
                    + " tabppadr On tabppadr.codsppadr = arqatend.procprin Inner Join"
                    + " cadprest On arqatend.codprest = cadprest.codprest Inner Join"
                    + " cadlei On arqint.codlei = cadlei.codlei Inner Join"
                    + " cadaco On cadlei.codaco = cadaco.codaco Inner Join"
                    + " cadcc On cadaco.codcc = cadcc.codcc"
                    + " Where"
                    + " arqint.posicao = 'I' And"
                    + " tabppadr.codtbppadr = '03'"
                    + " Order By"
                    + " cadcc.nomecc,"
                    + " 10 Desc");

            rs = stmt.executeQuery();

            /* if(rs.next()){
                String nome = rs.getString("nome");
                System.out.println("Nomme" +nome);
            }*/
            //String src = "Teste.jasper";
            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(rs);
            JasperPrint jasperPrint = null;
            InputStream logo = null;

            try {
                logo = new FileInputStream(new File("relatorios//imgs//logo.jpeg"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Gerador.class.getName()).log(Level.SEVERE, null, ex);
            }

            InputStream src = null;
            try {
                src = new FileInputStream(new File("relatorios//Rel_Atendimento_wareline.jasper"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Gerador.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                HashMap parametros = new HashMap();
                parametros.put("logo", logo);

                jasperPrint = JasperFillManager.fillReport(src, parametros, resultSetDataSource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, "relatorios/internacao_.pdf");
                /*try {
                    Runtime.getRuntime().exec("cmd /c start relatorios/internacao_.pdf");
                } catch (IOException ex) {
                    Logger.getLogger(Gerador.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            } catch (JRException ex) {
                System.out.println("Error: " + ex);
            }

            /*JasperViewer view = new JasperViewer(jasperPrint, false);
        view.setVisible(true);
             */
            enviarEmail();

            ConnectionFactory.closeConnection(conn, stmt, rs);

        } catch (SQLException ex) {
            Logger.getLogger(Gerador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void enviarEmail() {

        MailJava mj = new MailJava();

        //configurando email
        mj.setSmtpHostMail("smtp.gmail.com");
        mj.setSmtpPortMail("587");
        mj.setSmtpAuth("true");
        mj.setSmtpStarttls("true");
        mj.setUserMail("edenilsonjak@gmail.com");
        mj.setFromNameMail("Edenilson");
        mj.setPassMail("blablabla");
        mj.setCharsetMail("ISO-8859-1");
        mj.setSubjectMail("Relatório Internação Hospital Santarém");
        mj.setBodyMail(htmlMessage());
        mj.setTypeTextMail(MailJava.TYPE_TEXT_HTML);

        //sete quantos destinatarios desejar
        Map<String, String> map;
        map = new HashMap();
       // map.put("marcoaurelio@gmail.com", "Gmail");
        //map.put("edenilsonjak@gmail.com", "Gmail");
        
        String pathEmail = "emails/emails.txt";
        
        try {
            Scanner scanner = new Scanner(new FileReader(pathEmail)).useDelimiter(";");
            while (scanner.hasNext()) {                
                map.put(scanner.next().trim(), scanner.next().trim());
            }
            scanner.close();
            
        } catch (IOException e) {
            Logger.getLogger(Gerador.class.getName()).log(Level.SEVERE, null, e); 
        }
        
        mj.setToMailsUsers(map);

        //seta quatos anexos desejar
        List<String> files = new ArrayList<>();
        files.add("relatorios/internacao_.pdf");

        mj.setFileMails(files);

        try {
            new MailJavaSender().senderMail(mj);
        } catch (UnsupportedEncodingException e) {
        }

    }
    
    private static String htmlMessage() {
        return "<html> "
                + "<head>"
                + "<title>Contato NTI IPG Santarém - PA!</title> "
                + "</head> "
                + "<body> "
                + "<div style='background-color:orange; width:28%; height:100px;'>"
                + "<p> Contato NTI IPG Santarém - PA! "
                + "</p>"
                + "</div>"
                + "</body> "
                + "</html>";
    }

}
