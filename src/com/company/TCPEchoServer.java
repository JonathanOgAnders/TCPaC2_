package com.company;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPEchoServer
{
    private static ServerSocket servSock;
    private static final int PORT = 1234;

    private static Socket link;

    private static ArrayList<InetAddress> blockedClients;

    private static PrintWriter output;

    public static void main(String[] args)
    {
        output = null;
        blockedClients = new ArrayList<>();

        System.out.println("Opening port...\n");
        try
        {
            servSock = new ServerSocket(PORT);      //Step 1.
        }
        catch(IOException ioEx)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        establishConnection();

        while(true)
        {
            if(!blockedClients.contains(link.getInetAddress()))
            {
                do
                {
                    handleClient();
                } while (true);
            }
        }
    }

    private static void handleClient()
    {
        int numMessages = 0;

        try
        {

            Scanner input = new Scanner(link.getInputStream());

            String message = input.nextLine();      //Step 4.
            while (!message.equals("***CLOSE***") && !(blockedClients.contains(link.getInetAddress())))
            {
                System.out.println("Message received.");
                numMessages++;
                output.println("Message " + numMessages + ": " + message);   //Step 4.
                message = input.nextLine();
            }
            output.println(numMessages + " messages received.");//Step 4.

        }
        catch(IOException ioEx)
        {
            ioEx.printStackTrace();
        }

        finally
        {
            try
            {
                System.out.println(
                        "\n* Closing connection... *");
                link.close();				    //Step 5.
            }
            catch(IOException ioEx)
            {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    public static void establishConnection()
    {
        link = null;
        try
        {
            link = servSock.accept();

            output = new PrintWriter(link.getOutputStream(),true);
            if(link.getInetAddress().equals(InetAddress.getLocalHost()))
            {
                output.println("You've been blocked from using this service.");
                blockedClients.add(link.getInetAddress());
                System.out.println("Blocked " + link.getInetAddress());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
