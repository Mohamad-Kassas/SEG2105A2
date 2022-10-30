import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;


public class ServerConsole implements ChatIF{

  //Class variables *************************************************

   /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
    
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  EchoServer server;
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 


  //Constructors ****************************************************
   /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   * @throws IOException
   */
  public ServerConsole(int port)
  {
    server= new EchoServer(port, this);
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
    
    try 
    {
      server.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }


  //Instance methods ************************************************
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        if (message.charAt(0) == '#'){
          server.executeCommand(message);
        }
        else{
          message = "SERVER MSG> " + message;
          server.handleMessageFromServerUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  public void display(String message) 
  {
    System.out.println("> " + message);
  }
}

