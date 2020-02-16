package edu.escuelaing.arep.Retos;

import java.io.*;
import java.net.*;
import java.util.Date;



public class Servidor {

    //Atributos
    
    int puerto;

//region Sockets
    ServerSocket socketServidor;
    Socket socketCliente;
//endregion

    PrintWriter printWriter;
    BufferedReader bufferedReader;
    BufferedOutputStream bufferedOutputStream;
    OutputStream outputStream;

    String inputLine, archivo = "/";

    String tipoContenido;

    public Servidor() throws IOException{

        
        while(true){
            puerto = getPuerto();
            System.out.println("Encontre el puerto: "+ puerto);
            IniciadorAtributosConexion(puerto);
            System.out.println("Hice conexion en el puerto: "+ puerto);
            try {
                System.out.println("Listo para recibir, puerto: " + socketServidor.getLocalPort());
                socketCliente = socketServidor.accept();
                System.out.println("Nueva Coneccion");
            } catch (IOException e) {
                System.err.println("Fallo al aceptar el puerto del cliente.");
                System.exit(1);
            }

            RealizadorConexionStream();

            while ((inputLine = bufferedReader.readLine()) != null) {
                if(inputLine.startsWith("GET")){

                    archivo = inputLine.substring(inputLine.indexOf("/") + 1, inputLine.indexOf("HTTP"));

                }

                if (!bufferedReader.ready()){

                    break;

                } 
                
            }

            if(archivo.equals(" ") || archivo.equals("/")) {
                archivo = "index.html";

            }

            if(!archivo.equals("/")){

                CreacionArchivo();

            }

            printWriter.flush();
            CerrarTodo();
        }
    }



    public int getPuerto(){
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
         }
        return 4567;
    }

    public void IniciadorAtributosConexion(int puerto){
        try {
            socketServidor = new ServerSocket(puerto);    
        } catch (IOException e) {
            System.err.println("No se realiza ninguna conexion por el puerto:" + puerto);
            System.exit(1);
        }
        printWriter = null;
        bufferedReader = null;
        bufferedOutputStream = null;
        outputStream = null;
    }

    public void RealizadorConexionStream() throws IOException {
        outputStream = socketCliente.getOutputStream();
        printWriter = new PrintWriter(socketCliente.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
        bufferedOutputStream = new BufferedOutputStream(socketCliente.getOutputStream());
    }

    public void CreacionArchivo() {

        String path = System.getProperty("user.dir")
        + System.getProperty("file.separator") 
        + "src"
        + System.getProperty("file.separator")
        + "main"
        + System.getProperty("file.separator")
        + "java"
        + System.getProperty("file.separator")
        + "resources"
        + System.getProperty("file.separator")
        + archivo.substring(0, archivo.length()/** - 1*/ );

        System.out.println("Request: " + archivo);
        System.out.println("Path: " + path);

        tipoContenido ="";
        ConfirmarTipoContenido();
        
        try {
            System.out.println("Entre al try");
            System.out.println("Path: " + path);
            File pagina = new File(path);
            //Aqui esta el problema
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(pagina));
            System.out.println("tipocontenido: " + tipoContenido);
            if(tipoContenido.contains("image/")){
                System.out.println("Estoy intentando mostrar una imagen");
                MostrarImagen(pagina,tipoContenido.substring(tipoContenido.indexOf("/")+1));
            } 
            else{
                System.out.println("Estoy intentando mostrar una pagina con bf2");
                MostrarPagina(bufferedReader2);
            }

        } catch (IOException e) {
            MostrarPaginaError();
        }
    }

    public void MostrarPagina(BufferedReader br) throws IOException {
        String outString = 
        "HTTP/1.1 200 Ok\r\n" + 
        "Content-type: "+ tipoContenido +"\r\n" +
        "Server: Java HTTP Server\r\n" +
        "Date: " + new Date() + "\r\n" +
        "\r\n";
        String lineasAgregar;
        while ((lineasAgregar = br.readLine()) != null){
            outString += lineasAgregar;
        }
        printWriter.println(outString);
        br.close();
        
    }

    public void MostrarPaginaError(){
        String outputLine =
        "HTTP/1.1 404 Not Found\r\n"
        + "Content-type: "+ tipoContenido +"\r\n"
        + "Server: Java HTTP Server\r\n" 
        + "Date: " + new Date() + "\r\n" 
        + "\r\n" 
        + "<!DOCTYPE html>" 
        + "<html>" 
        + "<head>" 
        + "<meta charset=\"UTF-8\">" 
        + "<title>Este tipo de Archivo no fue encontrado</title>\n" 
        + "</head>" 
        + "<body>" 
        + "<center><h1>Este tipo de Archivo no fue encontrado</h1></center>" 
        + "</body>" 
        + "</html>";
        printWriter.println(outputLine);
    }

    public void ConfirmarTipoContenido(){
        System.out.println("archivo: " + archivo);

        if(archivo.endsWith(".html ") || archivo.endsWith(".htm ") || archivo.endsWith(".html") || archivo.endsWith(".htm")){
            tipoContenido = "text/html";
        }
        else if(archivo.endsWith(".css ")){
            tipoContenido = "text/css";
        }

        else if(archivo.endsWith(".ico ")){
            tipoContenido = "image/x-icon";
        }

        else if(archivo.endsWith(".png ")){
            tipoContenido = "image/png";
        }

        else if(archivo.endsWith(".jpeg ")){
            tipoContenido = "image/jpeg";
        }

        else if(archivo.endsWith(".js ")){
            tipoContenido = "application/javascript";
        }

        else if(archivo.endsWith(".json ")){
            tipoContenido = "application/json";
        }

        else{
            tipoContenido = "text/plain";
        }

        System.out.println("tipocontenido: " + tipoContenido);
    }

    public void CerrarTodo() throws IOException {
        printWriter.close();
        outputStream.close();
        bufferedOutputStream.close();
        bufferedReader.close();
        socketCliente.close();
        socketServidor.close();
        System.out.println("Si cerre");
    }
        
    public void MostrarImagen(File pagina, String formato) throws IOException {
        
        FileInputStream fis = new FileInputStream(pagina);
         byte[] data = new byte[(int) pagina.length()];
         fis.read(data);
         fis.close();

         // Cabeceras con la info de la imágen
         DataOutputStream dataOutputStream2 = new DataOutputStream(outputStream);
         String outString = "HTTP/1.1 200 Ok\r\n" + 
         "Content-type: image/"+ formato +"\r\n" +
         "Server: Java HTTP Server\r\n" +
         "Date: " + new Date() + "\r\n" +
         "Content-Length: " + data.length + "\r\n" +
         "\r\n";
         dataOutputStream2.writeBytes(outString);
         dataOutputStream2.write(data);
         dataOutputStream2.close();

        
    }

}