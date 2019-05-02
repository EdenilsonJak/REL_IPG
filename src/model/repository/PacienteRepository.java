/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.repository;

import connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.bean.cadPaciente;

/**
 *
 * @author Edenilson
 */
public class PacienteRepository {
    
    public void Salvar(cadPaciente paciente){
        
       Connection con = ConnectionFactory.getConnection();
       PreparedStatement stmt = null;
       
        try {
            stmt = con.prepareCall("INSERT INTO paciente (nome, dt_nascimento)VALUES(?,?)");
            stmt.setString(1, paciente.getNome());
            stmt.setDate(2, paciente.getDt_nasciment());
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Salvo com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error ao salvar!"+ ex);
        }finally{
            ConnectionFactory.closeConnection(con, stmt);
        }
        
    }
    
}
