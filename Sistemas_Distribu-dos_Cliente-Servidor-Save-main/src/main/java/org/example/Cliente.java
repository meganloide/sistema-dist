package org.example;
import java.io.*;
import java.net.Socket;
public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("escolha opção de dar upload, download e excluir");
            String command = userInput.readLine();
            out.println(command);

            switch (command) {
                case "upload":
                    System.out.println("mande o caminho do arquivo e depois finalize o programa:");
                    String filePath = userInput.readLine();
                    out.println(filePath);

                    File file = new File(filePath);
                    if (!file.exists()) {
                        System.out.println("Arquivo n encontrado!!!");
                        socket.close();
                        return;
                    }

                    try (BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file))) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileInput.read(buffer)) != -1) {
                            socket.getOutputStream().write(buffer, 0, bytesRead);
                        }
                        System.out.println(in.readLine());
                    }
                    break;
                case "download":
                    System.out.println("Digite o nome do arquivo: ");
                    String fileName = userInput.readLine();
                    out.println(fileName);

                    FileOutputStream fileOutput = new FileOutputStream("downloaded_" + fileName);
                    byte[] bytes = new byte[1024];
                    int count;
                    while ((count = socket.getInputStream().read(bytes)) > 0) {
                        fileOutput.write(bytes, 0, count);
                    }
                    fileOutput.close();
                    break;
                case "delete":
                    System.out.println("O que eu devo excluir?");
                    String fileToDelete = userInput.readLine();
                    out.println(fileToDelete);
                    System.out.println(in.readLine());
                    break;
                default:
                    System.out.println("Comando inválido!");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}