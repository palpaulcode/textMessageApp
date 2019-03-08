
package messagingapp;

import java.sql.*;
import java.sql.DriverManager;

public class DBConnect 
{
    String url = "jdbc:mysql://localhost:3306/phonebook";
    String username = "root";
    String password = "";
   
    private Connection con ;
    private Statement st;
    private ResultSet rs ; 
    private PreparedStatement pstmt; 
     /*st.close();   con.close();   */    
  
    //constructor
    public DBConnect()
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
    } //end constructor
   
    //method to view profiles 
     public void viewProfiles() 
      {
        try
        {
         System.out.println("FIRST NAME\t\tLAST NAME\t\tPHONE NUMBER");
         rs = st.executeQuery("select * from profiles");         
         while (rs.next() ) {
                             System.out.println(rs.getString(1)+"\t\t\t"+rs.getString(2)+"\t\t\t"+rs.getString(3) );
                            }
        }
        catch (Exception ex){
                             System.out.println("ERROR: " + ex); 
                            }
      } //end method viewProfiles
    
    //this query selects phone_number from profiles table and inserts it
    //to the outbox table to send a message to the selected contact
     public String selectPhone_number() throws SQLException
     {
       try 
        { 
          rs = st.executeQuery("SELECT phoneNumber FROM profiles WHERE fname = 'PAUL' ");
          rs.next();
          rs.getString(1);
          System.out.println("receiver_phone_number: " + rs.getString(1));
        }
        catch (Exception ex){
                             System.out.println("ERR : INVALID RECEIVER NUMBER " + ex );     
                            } 
        return rs.getString(1);
     }//end method selectPhone_number()
     
     //this method sends your message ( -->> single recipient <<-- )
     public void setNewMessage(String newSMSMessage)
     {
      try {
        pstmt = con.prepareStatement("INSERT into outbox(sender,receiver,message,MessageType) VALUES (?,?,?,?)");  
        pstmt.setString(1, "+254704395540" );
        pstmt.setString(2, selectPhone_number());
        pstmt.setString(3, newSMSMessage );
        pstmt.setString(4, "sms.txt" ); 
        pstmt.executeUpdate();
       }        
       catch (SQLException ex){
            System.out.println(ex.getMessage() ); 
            }
         finally{
            System.out.println("Message sent!");
            }
     }
     // method to sent group messages
     public void groupMessaging(String text)
     { 
      try {
       rs = st.executeQuery("SELECT phoneNumber FROM profiles");
       while(rs.next()) {
         groupText(rs.getString("phoneNumber"),text );
        }
       }
       catch (SQLException ex) {
         System.out.println(ex.getMessage() ); 
        }
     }
     
     //method groupText to message multiple recipients at once
     private void groupText(String receiver, String sms)
     {
      try {
        pstmt = con.prepareStatement("INSERT into outbox(sender,receiver,message,MessageType) VALUES (?,?,?,?)");
        pstmt.setString(1, "+254707439931" );
        pstmt.setString(2, receiver);
        pstmt.setString(3, sms);
        pstmt.setString(4, "sms.txt" ); 
        pstmt.executeUpdate();
     }
      catch (SQLException ex) {
        System.out.println(ex.getMessage() ); 
          }
     }
     
     //method to view messages from a selected user 
     public void viewMessage() throws SQLException
     {
      try { 
        System.out.println("RECEIVER_CONTACT\t\tMESSAGE");
        rs = st.executeQuery("SELECT receiver,message FROM inbox WHERE sender = '+254716427175' ");//to be directed to automatically get the phone number when a name is selected on the screen
        while(rs.next()) {      
            System.out.println(rs.getString(1)+"\t\t\t"+rs.getString(2) );
           }
        }
        catch (Exception ex) {
            ex.printStackTrace();
          }  
      }//end method viewMessage
     
     //method to view all send messages
     public void viewSentMessages() throws SQLException
     {
       try  { 
        System.out.println("RECEIVER_CONTACT\t\tMESSAGE");
        rs = st.executeQuery("SELECT MessageTo,MessageText FROM messagelog");
        while(rs.next()) {      
            System.out.println( rs.getString(1)+"\t\t\t"+ rs.getString(2) );
           }
        }
        catch (Exception ex) {
            ex.printStackTrace();
          }     
     }// end method viewSentMessages()
     
     //method to view inbox messages
     public void viewInbox()
     {
      try { 
        System.out.println("SENDER\t\tMESSAGE");
        rs = st.executeQuery("SELECT sender,message FROM inbox");
        while(rs.next()) {      
            System.out.println( rs.getString(1)+"\t"+ rs.getString(2) );
           }
        }
        catch (Exception ex) {
            ex.printStackTrace();
          }     
     }
    
     //method to populate table profiles in the database 
     public void setProfiles (String firstName, String lastName, String phoneNumber)
     {
       try {
        pstmt =  con.prepareStatement("INSERT into profiles(fname,lname,phoneNumber) VALUES (?,?,?)");
        pstmt.setString(1,firstName );
        pstmt.setString(2,lastName );
        pstmt.setString(3, phoneNumber );        
        pstmt.executeUpdate();
       }
        catch (SQLException ex){
            System.out.println(ex.getMessage() ); 
           }
     }//end method setProfile()

     public void delProfile(String name)
     {
      try { 
       pstmt = con.prepareStatement("delete from profiles WHERE fname = ? "); 
       pstmt.setString(1,name );
       int result_set = pstmt.executeUpdate();
       if (result_set > 0) {
            System.out.println("PROFILE DELETED");
          }
       else {
         System.out.println("NO PROFILE");
        }
      }
      catch (Exception ex) {
        System.out.println("ERR : " + ex.getMessage());
       }
     }//end method delProfile
    
    //method to delete all inbox messages
     public void clearInbox()
     {
      try { 
      pstmt = con.prepareStatement("delete from inbox "); 
      int result_set = pstmt.executeUpdate();
      if (result_set > 0) {
        System.out.println("INBOX MESSAGES DELETED");
       }
       else{
        System.out.println("INBOX ALREADY EMPTY");
       }
     }
      catch (Exception ex) {
        System.out.println("ERR : " + ex.getMessage());
       }
    }// end method clearInbox
    
     //this method clears all the sent messages from the outbox log
     public void clearmessagelog()
     {
      try { 
       pstmt = con.prepareStatement("delete from messagelog"); 
       int result_set = pstmt.executeUpdate();
       if (result_set > 0) {
        System.out.println("OUTBOX LOG CLEARED");
        }
       else {
         System.out.println("OUTBOX LOG ALREADY EMPTY");
        }
       }
      catch (Exception ex) {
        System.out.println("ERR : " + ex.getMessage());
       }
    }
      public void clearOutbox()
     {
      try { 
       pstmt = con.prepareStatement("delete from outbox"); 
       int result_set = pstmt.executeUpdate();
       if (result_set > 0) {
        System.out.println("OUTBOX CLEARED");
        }
       else {
         System.out.println("OUTBOX ALREADY EMPTY");
        }
       }
      catch (Exception ex) {
        System.out.println("ERR : " + ex.getMessage());
       }
     }
}