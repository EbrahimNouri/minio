package com.sajayanegar.storage.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class CommandRunner {

    public String runCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        StringBuilder output = new StringBuilder();

        try (InputStream inputStream = process.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        if (exitCode == 0) {
            return output.toString();
        } else {
            throw new RuntimeException("Command execution failed with exit code: " + exitCode);
        }
    }

//    public static void main(String[] args) {
//        try {
//            String command = "docker ";
//            String output = runCommand(command);
//            System.out.println(output);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    public String runCommand2(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return ("Command execution failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
           return e.getMessage();
        }

    }
}


