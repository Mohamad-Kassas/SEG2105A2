// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  @Override
  public void connectionClosed(){
    clientUI.display("Connection Terminated");
  }

  @Override
  public void connectionException(Exception exception){
    clientUI.display("Server disconnected");
    quit();
    connectionClosed();
  }

    /**
   * Executes the given command in the console
   * 
   * @param command The message from the UI
   */
  public void executeCommand(String rawCommand){
    String trimCommand = rawCommand.trim();
    String[] listCommand = trimCommand.split(" ");
    String command = listCommand[0];
    if (command.equals("#quit")){
      executeQuitCommand();
    }

    else if (command.equals("#logoff")){
      executeLogOffCommand();
    }

    else if (command.equals("#sethost")){
      executeSetHostCommand(listCommand);
    }

    else if (command.equals("#setport")){
      executeSetPortCommand(listCommand);
    }

    else if (command.equals("#login")){
      executeLogInCommand();
    }

    else if (command.equals("#gethost")){
      clientUI.display(executeGetHostCommand());
    }

    else if (command.equals("#getport")){
      clientUI.display(executeGetPortCommand());
    }

    else{
      clientUI.display("Invalid Command");
    }
  }



  //Class methods ***************************************************

  private void executeQuitCommand(){
    quit();
  }

  private void executeLogOffCommand(){
    if (isConnected()){
    try{
      closeConnection();
    }
    catch (IOException e){}
  }
  else{
    clientUI.display("Client already disconnected");
  }
  }

  private void executeSetHostCommand(String[] listCommand){
    String argument = "";
        try{
          argument = listCommand[1];
        }
        catch (IndexOutOfBoundsException e){
          clientUI.display("Invalid Command Argument");
          return;
        }

        if (isConnected()){
          clientUI.display("Client already connected to server, cannot change host");
          return;
        }

        setHost(argument);
        clientUI.display("Host has been set to: " + argument);
  }


  private void executeSetPortCommand(String[] listCommand){
    String argument = "";
    int port = 5555;
    try{
      argument = listCommand[1];
      port = Integer.parseInt(argument);
      if (port <=0 || port > 65535){
        throw new IllegalArgumentException();
      }
      
    }
    catch (IndexOutOfBoundsException e){
      clientUI.display("Invalid Command Argument");
      return;
    }

    catch (NumberFormatException e){
      clientUI.display("Port is not an integer, cannot change port");
      return;
    }

    catch (IllegalArgumentException e){
      clientUI.display("Port is not a valid port number, cannot change port");
      return;
    }

    if (isConnected()){
      clientUI.display("Client already connected to server, cannot change port");
      return;
    }

    setPort(port);
    clientUI.display("Port has been set to: " + port);
  }


  private void executeLogInCommand(){
    if (isConnected()){
      clientUI.display("Client already connected to server");
      return;
    }
    try{
      openConnection();
      clientUI.display("Connected to server");
    }
    catch (IOException e){}
  }


  private String executeGetHostCommand(){
    return "Current host: " + getHost();
  }

  private String executeGetPortCommand(){
    return "Current port: " + String.valueOf(getPort());
  }
}
//End of ChatClient class
