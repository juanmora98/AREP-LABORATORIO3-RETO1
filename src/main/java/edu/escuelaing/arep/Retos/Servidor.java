package edu.escuelaing.arep.Retos;


import java.io.*;
import java.net.*;
import java.lang.Math;


public class Servidor {

    //Atributos

    private ServerSocket serverSocket;
    private Socket socketCliente;
    private PrintWriter out;
    private BufferedReader in;
    String inputLine, outputLine;

    
    public static void main(String[] args) throws IOException {
        new Servidor();
    }

    public void Iniciador(){
        int puerto = getPuerto();
        try {
            serverSocket = new ServerSocket(puerto);
        } catch (IOException e) {
            System.err.println("No se esta escuchando nada del puerto: " + puerto);
            System.exit(1);
        }
        socketCliente = null;
        out = null;
        in = null;
    }

    public int getPuerto(){
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

    public Servidor() throws IOException{
        Iniciador();
        try {
            socketCliente = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Fallo al aceptar el puerto del cliente.");
            System.exit(1);
        }
        out = new PrintWriter(socketCliente.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Recibi: " + inputLine);
            if (!in.ready())
                break;
        }

        outputLine = 
          "<!DOCTYPE html>" + 
          "<html>" + 
          "<head>" + 
          "<meta charset=\"UTF-8\">" + 
          "<title>Title of the document</title>\n" + 
          "</head>" + 
          "<body>" + 
          "<h1>Mi propio mensaje</h1>" + 
          "</body>" + 
          "</html>" + inputLine; 
        out.println("HTTP/1.1 200 OK");
        out.println("Content-type: " + "text/html");
        out.println("\r\n");
        out.println(outputLine);
        out.flush();
        out.close();
        in.close();
        socketCliente.close();
        serverSocket.close();
    }


}