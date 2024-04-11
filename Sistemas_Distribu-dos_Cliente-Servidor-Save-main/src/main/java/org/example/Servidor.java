package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("servidor on");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + socket);
                Thread clientHandler = new Thread(new ClientHandler(socket));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String command = in.readLine();
                switch (command) {
                    case "upload":
                        String filePath = in.readLine();
                        File file = new File(filePath);
                        FileOutputStream fileOutput = new FileOutputStream(file.getName());
                        byte[] bytes = new byte[1024];
                        int count;
                        while ((count = clientSocket.getInputStream().read(bytes)) > 0) {
                            fileOutput.write(bytes, 0, count);
                        }
                        fileOutput.close();
                        out.println("arquivo enviado");
                        break;
                    case "download":
                        String fileName = in.readLine();
                        File fileToDownload = new File(fileName);
                        if (!fileToDownload.exists()) {
                            out.println("o arquivo não foi encontradi");
                            break;
                        }
                        FileInputStream fileInput = new FileInputStream(fileToDownload);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileInput.read(buffer)) != -1) {
                            clientSocket.getOutputStream().write(buffer, 0, bytesRead);
                        }
                        fileInput.close();
                        break;
                    case "delete":
                        String fileToDelete = in.readLine();
                        File fileToDeleteObj = new File(fileToDelete);
                        if (fileToDeleteObj.delete()) {
                            out.println("Arquivo excluido");
                        }

                        break;
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}