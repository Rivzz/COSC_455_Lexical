package com.mtjanney.project;

import com.mtjanney.project.reader.ReadFile;

import java.io.IOException;
import java.util.Scanner;

/**
 * Lexical components created by Michael Janney
 */
public class Project {
    /**
     * A new instance of reading a text file can be generated here
     * @param args = :)
     * @throws IOException = Read file does not exist.
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String location;
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------");
        System.out.println("IF YOU ARE READING A FILE LOCATED IN \\src\\main\\resources\\ CHANGE 'boolean resourcesPath' to TRUE on line 22 in Project.java!");
        System.out.println("IF YOU ARE READING A FILE NOT LOCATED THERE, CHANGE 'boolean resoucesPath' to FALSE on line 22 in Project.java!");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        boolean resourcesPath = true; // <------------------- CHANGE THIS IF WANT DIRECT FILE PATH (DEFAULT in \\src\\main\\resources\\)

        if (resourcesPath) System.out.print("\n\nPlease enter your desired file name or location (fileName.txt): ");
        else System.out.print("\n\nPlease enter your desired file name or location (\\\\dir\\\\location\\\\etc\\\\...\\\\fileName.txt): ");

        location = scanner.nextLine();
        new ReadFile(location, resourcesPath);

        System.out.println("\nPOS                            KIND                           VALUE" +
                "\n--------------------------------------------------------------------");

        ReadFile.next();
        ReadFile.print(ReadFile.position(), ReadFile.kind(), ReadFile.value());
        while (!ReadFile.kind().equals("end-of-text") && !ReadFile.TERMINATE) {
            ReadFile.next();
            if (!ReadFile.TERMINATE) ReadFile.print(ReadFile.position(), ReadFile.kind(), ReadFile.value());
        }
    }
}
