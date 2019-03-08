/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagingapp;

import java.sql.SQLException;
import java.util.Scanner;

public class MessagingApp
{
    public static void main(String[] args) throws SQLException        
    {
      DBConnect connector = new DBConnect();
      Scanner details = new Scanner (System.in);
//      
//      ClassRegister register = new ClassRegister ();
//      register.readSMS();
//      
      /*method call to view profiles*/    
      connector.viewProfiles(); 
//       
//      /*method call to type in the new SMS message to be sent */
//      System.out.println("TYPE YOUR MESSAGE BELOW : ");
//      String theMessage = details.nextLine();
//      connector.setNewMessage(theMessage);
//      
//      /*method call to text all contacts at once*/
//      System.out.println("TYPE GROUP MESSAGE BELOW : ");
//      String sms = details.nextLine();
//      connector.groupMessaging(sms);
//
//      /*method call to view messages from a selected profile*/
//      connector.viewMessage();
//      
//      /*metod call to create new user profile and prompt user to enter the new profile details*/
//      System.out.print("TYPE IN FIRST NAME \n: ");
//      String fname = details.nextLine();
//      System.out.print("TYPE IN LAST NAME \n: ");
//      String lname = details.nextLine();
//      System.out.print("TYPE IN PHONE NUMBER \n: ");
//      String phone = details.nextLine();
//      connector.setProfiles (fname,lname,phone);
//     
//      /*this method selects a phone number from profile*/     
//      connector.selectPhone_number();
//        
//      /*method call to view sent messages*/
//      connector.viewSentMessages();
//      
//      /*method to view inbox messages*/
//      connector.viewInbox();
//     
//      /*method call to delete all messages from inbox*/     
//      connector.clearInbox();
//      
//      /*method call to delete an existing contact from profiles*/
//      System.out.println("ENTER NAME OF THE CONTACT YOU WANT TO DELETE:");
//      String delName = details.nextLine();
//      connector.delProfile(delName);
//      
//      /*method call to clear all sent messages from messagelog*/
//      connector.clearmessagelog();
//      
//      /*method call to clear outbox table*/
//      connector.clearOutbox();
    }
    
}
