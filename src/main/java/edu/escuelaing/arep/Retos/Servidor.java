package edu.escuelaing.arep.Retos;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class Servidor {

    //Atributos

    private ServerSocket serverSocket;
    private Socket socketCliente;
    private OutputStream out;
    private BufferedReader in;
    String inputLine, outputLine;
    private static ArrayList<String> listaFormatos = new ArrayList<>(Arrays.asList("jpg","png","img"));
    
    public static void main(String[] args) throws IOException {
        new Servidor();
    }

    public Servidor() throws IOException{
        Iniciador();
        while(true){
            try {
                socketCliente = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Fallo al aceptar el puerto del cliente.");
                System.exit(1);
            }
            out = socketCliente.getOutputStream();
            in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibi: " + inputLine);
                if (!in.ready()) break;
                outputLine = MetodosDeLlamado();
            }
            if(outputLine != null) {
                String formato = null;
                if (outputLine.length() > 3){
                    formato = outputLine.substring(outputLine.length() - 3);
                }
                if(listaFormatos.contains(formato)){
                    try {
                        MostrarImagen(formato);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    MostrarHtml(out);
                }
            }
            socketCliente.close();
        }
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

    

    public String MetodosDeLlamado(){
        if (inputLine.contains("GET")) {
            String [] splitedLine = inputLine.split(" ");
            outputLine = splitedLine[1] ;
        }
        return outputLine;
    }

    public void MostrarHtml(OutputStream out) {
        Scanner scanner = null;
        try {
            scanner = new Scanner( new File("src/main/resources/" + outputLine));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            byte htmlBytes[] = htmlString.getBytes("UTF-8");
            PrintStream ps = new PrintStream(out);
            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
            ps.println("HTTP/1.1 200 OK");
            ps.println("Content-Type: text/html; charset=UTF-8");
            ps.println("Date: " + df.format(new Date()));
            ps.println("Connection: close");
            ps.println();
            ps.println(htmlString);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            try {
                scanner = new Scanner( new File("src/main/resources/notfound.html"));
                String htmlString = scanner.useDelimiter("\\Z").next();
                scanner.close();
                byte htmlBytes[] = htmlString.getBytes("UTF-8");
                PrintStream ps = new PrintStream(out);
                DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
                ps.println("HTTP/1.1 200 OK");
                ps.println("Content-Type: text/html; charset=UTF-8");
                ps.println("Date: " + df.format(new Date()));
                ps.println("Connection: close");
                ps.println();
                ps.println(htmlString);
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

        }
    }
        
    public void MostrarImagen(String formato) throws IOException {
        try{
            PrintWriter pw = new PrintWriter(out, true);
            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type: image/png\r\n");
            System.out.println(outputLine);
            BufferedImage image= ImageIO.read(new File("src/main/resources/images/" + outputLine));
            ImageIO.write(image, formato, out);
        } catch (IOException e) {
                BufferedImage image= ImageIO.read(new File("src/main/resources/images/error.png"));
                ImageIO.write(image, formato, out);
            }
        }

}