package edu.escuelaing.arep;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static ArrayList<String> imgext = new ArrayList<>(Arrays.asList("jpg","png","img"));
    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(8080);
            while(true) {
                System.out.println("Ready to receive on port 8080");
                clientSocket = serverSocket.accept();
                ResolveRequest(clientSocket.getOutputStream());
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ResolveRequest(OutputStream out) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = null;
        String path = null;
        while((line=bf.readLine())!=null) {
            System.out.println(line);
            if (!bf.ready()) break;
            if (line.contains("GET")) {
                String [] splitedLine = line.split(" ");
                path =splitedLine[1];
            }
        }
        if(path!=null) {
            serve(path, out);
        }
    }

    private static void serve(String path, OutputStream out) {
        String ext = null;
        if (path.length() > 3)
        {
            ext = path.substring(path.length() - 3);
        }
        if(imgext.contains(ext)){
            try {
                serveImage(path,out,ext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            serveHtml(path, out);

        }
    }

    private static void serveHtml(String path, OutputStream out) {
        Scanner scanner = null;
        try {
            scanner = new Scanner( new File("src/main/java/resources/"+path));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            byte htmlBytes[] = htmlString.getBytes("UTF-8");
            PrintStream response = new PrintStream(out);
            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
            response.println("HTTP/1.1 200 OK");
            response.println("Content-Type: text/html; charset=UTF-8");
            response.println("Date: " + df.format(new Date()));
            response.println("Connection: close");
            response.println();
            response.println(htmlString);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            try {
                scanner = new Scanner( new File("src/main/java/resources/NOTFOUND.html"));
                String htmlString = scanner.useDelimiter("\\Z").next();
                scanner.close();
                byte htmlBytes[] = htmlString.getBytes("UTF-8");
                PrintStream response = new PrintStream(out);
                DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
                response.println("HTTP/1.1 200 OK");
                response.println("Content-Type: text/html; charset=UTF-8");
                response.println("Date: " + df.format(new Date()));
                response.println("Connection: close");
                response.println();
                response.println(htmlString);
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

        }
    }

    private static void serveImage(String path,OutputStream outputStream,String ext) throws IOException {
        try{
        PrintWriter response = new PrintWriter(outputStream, true);
        response.println("HTTP/1.1 200 OK");
        response.println("Content-Type: image/png\r\n");
        BufferedImage image= ImageIO.read(new File("src/main/java/resources/"+path));
        ImageIO.write(image, ext, outputStream);
        } catch (IOException e) {
            BufferedImage image= ImageIO.read(new File("src/main/java/resources/imagenes/error.png"));
            ImageIO.write(image, ext, outputStream);
        }
    }


}