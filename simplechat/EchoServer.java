// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import ocsf.server.*;
import common.*;
import java.io.*;

import javax.sound.midi.SysexMessage;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;



  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    serverUI.display("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display("Server has stopped listening for connections");
  }

  public void handleMessageFromServerUI(String message)
  {
      sendToAllClients(message);
      serverUI.display(message);
  }


  // Overridden Hook Methods ****************************************
  @Override
  protected void clientConnected(ConnectionToClient client){
    serverUI.display("New client connected to the server");
  }

  @Override
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
      serverUI.display("Connection with client " + client.getName() + " terminated");
        }

  @Override
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
      serverUI.display("Client " + client.getName() + " disconnected");
      clientDisconnected(client);
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

    else if (command.equals("#stop")){
      executeStopCommand();
    }

    else if (command.equals("#close")){
      executeCloseCommand();
    }

    else if (command.equals("#setport")){
      executeSetPortCommand(listCommand);
    }

    else if (command.equals("#start")){
      executeStartCommand();
    }

    else if (command.equals("#getport")){
      serverUI.display(executeGetPortCommand());
    }

    else{
      serverUI.display("Invalid Command");
    }
  }


  //Class methods ***************************************************

  private void executeQuitCommand(){
    try{
      close();
      serverUI.display("Server has shutdown");
    }
    catch (IOException e){}
    System.exit(0);
  }

  private void executeStopCommand(){
    if (!isListening()){
      serverUI.display("Server already not listening for connections");
      return;
    }
    stopListening();
  }

  private void executeCloseCommand(){
    try{
      close();
      serverUI.display("Server disconnected all clients");
    }
    catch (IOException e){}
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
      serverUI.display("Invalid Command Argument");
      return;
    }

    catch (NumberFormatException e){
      serverUI.display("Port is not an integer, cannot change port");
      return;
    }

    catch (IllegalArgumentException e){
      serverUI.display("Port is not a valid port number, cannot change port");
      return;
    }

    if (isListening()){
      serverUI.display("Server already running, cannot change port");
      return;
    }

    setPort(port);
    serverUI.display("Port has been set to: " + port);
  }


  private void executeStartCommand(){
    if (isListening()){
      serverUI.display("Server already running");
      return;
    }
    try{
      listen();
      serverUI.display("Server started");
    }
    catch (IOException e){
      serverUI.display("ERROR - Could not listen for clients!");
    }
  }


  private String executeGetPortCommand(){
    return "Current port: " + String.valueOf(getPort());
  }


  //Class methods ***************************************************

  public static void main(String[] args) {
    int port = 0;

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }

    ServerConsole serverConsole = new ServerConsole(port);
    serverConsole.accept();
  }
}
//End of EchoServer class
