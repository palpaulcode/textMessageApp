/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author TURNKEY
 */
public class ClassRegister 
{
    String url = "jdbc:mysql://localhost:3306/phonebook";
    String username = "root";
    String password = "";
   
    private Connection con ;
    private Statement st;
    private ResultSet rs ; 
    private PreparedStatement pstmt;     
  
    //constructor
    public ClassRegister()
    {
      try
        {
         Class.forName("com.mysql.jdbc.Driver");
         con =DriverManager.getConnection(url,username,password);
         st = con.createStatement(); 
        }
        catch (ClassNotFoundException | SQLException ex) //multi-catch 
        {
         System.out.println("ERROR: " + ex);
        }
    } 
    
    public void readSMS()
    {
        try {
        rs = st.executeQuery ( "SELECT message, sender FROM inbox ") ;
            
        if (rs.isBeforeFirst()) 
          {
            while (rs.next()) {
                String newText = rs.getString(1);
                processSMS(newText);
               }
          }
                
        }
        catch (SQLException ex){
            System.out.println("message_registration_err : ");
        }
        
    }
    
    //method to process incoming sms message
    private void processSMS(String text)
    {
       String sms = "";
       String phoneNumber = "";
       try {          
           sms = text.toUpperCase();
           phoneNumber = rs.getString("sender");
       }
       catch(SQLException ex){
           //output to be added
       }
         if (sms.startsWith("REG:")) {
               processRegister(sms, phoneNumber);
               deleteSMS(phoneNumber);
            }
          if (sms.startsWith("C:")) {
                checkStatus(sms, phoneNumber);
                deleteSMS(phoneNumber);
        }
    }
    
    //method to register new user profile through text message
    public void processRegister(String regsms, String phoneNumber)
    {
       /*
        *split the message
        *keyword firstname:lastname
        */
        
        regsms.trim();
        System.out.println(regsms.replaceFirst("REG:", ""));
        String data[] = regsms.split(":");  

        if (data.length==3)
        {          
            try {
            pstmt = con.prepareStatement("INSERT INTO profiles (fname,lname,phoneNumber) VALUES (?,?,?)");
            pstmt.setString(1, data[1]);
            pstmt.setString(2, data[2]);
            pstmt.setString(3, phoneNumber);
            }
            catch (SQLException ex){
                System.out.println("Unable to register user through text message ");
                ex.printStackTrace();

            }
            
                regsms = "Dear ".concat(data  [1]).concat(" ").concat(data [2]).concat(", Thankyou for registering with "
                        + "TURNKEY INC. You are now able to receive our product updates and tech news!");
              
                sendSMS(regsms, phoneNumber);  
        }
        else {
            System.out.println("code jammed!!!"); //to be removed
            regsms = "Wrong format. Use format REG:FirstName:LastName";
            sendSMS(regsms, phoneNumber);
        }
    }
    
    private void checkStatus(String statusSms, String number)
    {
        statusSms.trim();
        //statusSms.replaceFirst("C", "");
        try {
           pstmt = con.prepareStatement("SELECT fname, lname FROM profiles WHERE phoneNumber = ? ");
           pstmt.setString(1, number);
           
           if (rs.isBeforeFirst())
           {
               while(rs.next()){
                   statusSms = "Dear ".concat(rs.getString("fname")).concat(" ").concat(rs.getString("lname")).concat(" you are "
                           + "already registered to TURNKEY INC. Thankyou for choosing us."); 
                   sendSMS(statusSms, number);
               }
           }
            else {
                errorCheck("AccNotExist", number);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    private void errorCheck(String action, String phone)
    {
      String textBack = "";
      
      if (action.equalsIgnoreCase("AccNotExist")){
          textBack = "No record of you in the system. send a message in the format 'regme:firstName:lastName' without "
                  + "the quotes. Thankyou.";
          
          
         sendSMS(textBack, phone); 
      }  
    }
    
    //message sending method
    private void sendSMS(String message, String phoneNo)
    {
        try{
            pstmt = con.prepareStatement("INSERT into outbox(sender,receiver,message,MessageType) VALUES (?,?,?,?)");
            pstmt.setString(1, "+254704395540");
            pstmt.setString(2, phoneNo);
            pstmt.setString(3, message);
            pstmt.setString(4, "sms.txt");
            pstmt.executeUpdate();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    
    //method to delete sms to avoid repeat registration
    private void deleteSMS(String contact)
    {
        try {
            //copy processed sms to table porcessedsms for reference.
            pstmt = con.prepareStatement("INSERT INTO processedsms (SELECT sender, message, senttime FROM inbox WHERE sender = ? )");
            pstmt.setString(1, contact);
        }
        catch (SQLException ex){
            System.out.println(ex.getMessage() );
        }
        try{
            //delete sms from inbox
            pstmt = con.prepareStatement("DELETE FROM inbox WHERE sender = ?");
            pstmt.setString(1, contact);
            pstmt.executeUpdate();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

}
