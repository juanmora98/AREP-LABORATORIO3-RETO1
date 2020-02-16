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


    /**
     * Metodo constructor del servidor en el cual se realizan las distintas conexiones necesarias para generar las paginas y las imagenes
     * @throws IOException
     */
    public Servidor() throws IOException{

        
        while(true){
            puerto = getPuerto();          
            IniciadorAtributosConexion(puerto);

            try {
                
                socketCliente = socketServidor.accept();

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


    /**
     * Metodo encargado de encontrar el puerto por el que se ara la conexion
     * @return int que es el puerto para realizar la conexion
     */
    public int getPuerto(){
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
         }
        return 4567;
    }

    /**
     * Metodo iniciador del socket del servidor y de los distintos atributos necesarios para mostrar las imagenes y las paginas
     * @param puerto
     */
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


    /**
     * Metodo en el cual se establecen los valores de distintos atributos que necesitan del socket del cliente
     * @throws IOException
     */
    public void RealizadorConexionStream() throws IOException {
        outputStream = socketCliente.getOutputStream();
        printWriter = new PrintWriter(socketCliente.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
        bufferedOutputStream = new BufferedOutputStream(socketCliente.getOutputStream());
    }

    /**
     * Metodo utilizado para la generacion del path de los recursos y realizar la muestra de la pagina dependiendo de lo solicitado
     */
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

        tipoContenido ="";
        ConfirmarTipoContenido();
        
        try {

            File pagina = new File(path);
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(pagina));

            if(tipoContenido.contains("image/")){
                MostrarImagen(pagina,tipoContenido.substring(tipoContenido.indexOf("/") + 1));
            } 
            else{
                MostrarPagina(bufferedReader2);
            }

        } catch (IOException e) {
            MostrarPaginaError();
        }
    }

    /**
     * Metodo utilizado para mostrar una pagina web con el contenido solicitado que no es tipo imagen
     * @param br buffered creado anteriormente
     * @throws IOException
     */
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

    /**
     * Metodo en el cual se genera una pagina en la cual se muestraque el tipo de archivo buscado no se encuentra en los recursos
     */
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

    /**
     * Metodo en el cual a partir del formato con el que se busco un archivo se clasifica en que tipo esta
     */
    public void ConfirmarTipoContenido(){

        if(archivo.endsWith(".html ") || archivo.endsWith(".htm ") || archivo.endsWith(".html") || archivo.endsWith(".htm")){
            tipoContenido = "text/html";
        }
        else if(archivo.endsWith(".css ") || archivo.endsWith(".css")){
            tipoContenido = "text/css";
        }

        else if(archivo.endsWith(".ico ") || archivo.endsWith(".ico")){
            tipoContenido = "image/x-icon";
        }

        else if(archivo.endsWith(".png ") || archivo.endsWith(".png")){
            tipoContenido = "image/png";
        }

        else if(archivo.endsWith(".jpeg ") || archivo.endsWith(".jpeg")){
            tipoContenido = "image/jpeg";
        }

        else if(archivo.endsWith(".js ") || archivo.endsWith(".js")){
            tipoContenido = "application/javascript";
        }

        else if(archivo.endsWith(".json ") || archivo.endsWith(".json")){
            tipoContenido = "application/json";
        }

        else{
            tipoContenido = "text/plain";
        }

    }

    /**
     * Metodo con el cual se cierran todas las conexiones realizadas anteriormente
     * @throws IOException
     */
    public void CerrarTodo() throws IOException {

        printWriter.close();
        outputStream.close();
        bufferedOutputStream.close();
        bufferedReader.close();
        socketCliente.close();
        socketServidor.close();

    }
        
    /**
     * metodo con el cual se genera la pagina para poder mostrar imagenes
     * @param pagina
     * @param formato
     * @throws IOException
     */
    public void MostrarImagen(File pagina, String formato) throws IOException {
        
        FileInputStream fis = new FileInputStream(pagina);
         byte[] data = new byte[(int) pagina.length()];
         fis.read(data);
         fis.close();

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


    /**
     * metodo para arrancar el servidor
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        new Servidor();
    }

}