package com.mtjanney.project.reader;

import java.io.*;
import java.util.List;

/**
 * Lexical components created by Michael Janney
 */
public class ReadFile {
    private static File file = null;
    public static String NEXT_TOKEN;
    public static boolean TERMINATE = false;
    public static int CURRENT_LINE = 1;
    public static int CURRENT_POS = 1;
    private static boolean increment = false;

    public ReadFile(String fileName, boolean systemDIR) throws IOException {
        if (systemDIR) {
            System.out.print(System.getProperty("user.dir"));
            file = new File(System.getProperty("user.dir") + "/src/main/resources/" + fileName);
        } else {
            file = new File(fileName);
        }

        System.out.println("\nFILE: " + fileName);
    }

    /**
     * This is the main program which executes the loop to read and display the appropriate tokens from the given
     * input text files located in Project.java.
     *
     * Todo: Each element is labeled for its functionality, and refactoring to simplify can be done in the future.
     * The reading of the text files and the output of their corresponding tokens is done concurrently without storage
     * of any tokens in a data structure. Easy implementation for future compiler elements such as syntax checking or
     * execution of the compiled language can be added inside check cases for specific terminals.
     *
     * Each loop of a "line" of text is read concurrently character by character and once a new "word" is located, a
     * new "terminal" is reached, and checked for specific cases, or the end-of-text symbol is located, the loop will
     * define the current "lexeme" and execute the next(), kind(), value(), and print() functions to correctly display
     * the resulting token from the current lexeme.
     *
     * @throws IOException = when file cannot be located.
     */
    public static boolean next() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        boolean skipClause = false;
        boolean skipLine = false;
        int internalLine = 1; int internalPosition = 1;
        StringBuilder word = new StringBuilder(); // This holds the last read characters until lexeme is defined.

        // This is the next() function, which reads the next lexeme concurrently from the input file.
        // This loop will execute until the end of the file is reached, the line statement equals the end-of-text,
        // or a symbol is read outside the language.
        while ((line = reader.readLine()) != null && !line.equals("end") && !TERMINATE) {
            if (increment) { // Correctly align to the next line when a final symbol is read for an increment.
                increment = false;

                CURRENT_POS = 1;
                CURRENT_LINE++;
            }

            if (internalLine >= CURRENT_LINE) {

                /////////////////////////////// STARTS READING A LINE OF TEXT FROM DOCUMENT ///////////////////////////////

                for (int i = 0; i < line.length(); i++) {
                    if (i + 1 < CURRENT_POS) { // Returns to the "saved" position in the document.
                        internalPosition++;
                    } else {
                        if (!skipLine) { // Verifying that no "//" symbol has been read.
                            char c;

                            c = line.charAt(i);

                            if (skipClause) { // Verifying that the next char should not be read due to special case (Ex. !=).
                                skipClause = false;
                            } else {

                                //////////////// IDENTIFY LEXEME FROM CHAR IN LINE OF TEXT FROM DOCUMENT ////////////////

                                if (c == ' ' || c == '\t') { // Will "read" a lexeme given a space.
                                    if (word.length() != 0) { // Ignoring whitespace.
                                        NEXT_TOKEN = word.toString();
                                        CURRENT_POS = internalPosition;
                                        CURRENT_LINE = internalLine;

                                        if (CURRENT_POS >= line.length()) {
                                            increment = true;
                                        }

                                        return true;
                                    }
                                } else if (checkTerminal(c)) { // Will "read" a character given a terminal symbol.
                                    if (word.length() != 0) {
                                        NEXT_TOKEN = word.toString();
                                        CURRENT_POS = internalPosition;
                                        CURRENT_LINE = internalLine;

                                        if (CURRENT_POS > line.length()) {
                                            increment = true;
                                        }

                                        return true;
                                    }

                                    // Checks for a special terminal case where a followup symbol may exist.
                                    if (c == '=' || c == ':' || c == '!' || c == '>' || c == '/') {
                                        if (i+1 < line.length()) { // Verifies that there is indeed a next symbol in the line.
                                            if (c == '=') {
                                                if (line.charAt(i+1) == '<') { // Checks special case.
                                                    NEXT_TOKEN = "=<";
                                                    skipClause = true;
                                                } else { // Not special case.
                                                    NEXT_TOKEN = String.valueOf(c);
                                                }
                                            } else if (c == ':') {
                                                if (line.charAt(i+1) == '=') {
                                                    NEXT_TOKEN = ":=";
                                                    skipClause = true;
                                                } else {
                                                    NEXT_TOKEN = String.valueOf(c);
                                                }
                                            } else if (c == '!') {
                                                if (line.charAt(i+1) == '=') {
                                                    NEXT_TOKEN = "!=";
                                                    skipClause = true;
                                                } else { // Verifying that the next symbol is not a single '!'
                                                    TERMINATE = true;
                                                    System.out.println("ERROR: Character '" + c + "' is outside the language. (Line " + CURRENT_LINE
                                                            + ", Char " + CURRENT_POS + ")");
                                                }
                                            } else if (c == '/') {
                                                if (line.charAt(i+1) == '/') { // Comment case
                                                    skipLine = true;
                                                } else { // Division case
                                                    NEXT_TOKEN = String.valueOf(c);
                                                }
                                            } else {
                                                if (line.charAt(i+1) == '=') {
                                                    NEXT_TOKEN = ">=";
                                                    skipClause = true;
                                                } else {
                                                    NEXT_TOKEN = String.valueOf(c);
                                                }
                                            }
                                        } else { // Single terminal because next char is either null or not special case.
                                            if (c == '!') { // Double-checking symbol is not a single !
                                                TERMINATE = true;
                                                System.out.println("ERROR: Character '" + c + "' is outside the language. (Line " + CURRENT_LINE
                                                        + ", Char " + CURRENT_POS + ")");
                                            }

                                            NEXT_TOKEN = String.valueOf(c);
                                        }
                                    } else { // Terminal is not special case.
                                        NEXT_TOKEN = String.valueOf(c);
                                    }

                                    if (!skipLine) {
                                        if (skipClause) {
                                            CURRENT_POS = internalPosition + 2;
                                        } else {
                                            CURRENT_POS = internalPosition + 1;
                                        }
                                        CURRENT_LINE = internalLine;

                                        if (CURRENT_POS >= line.length()) {
                                            increment = true;
                                        }

                                        formatPosition = true;

                                        return true;
                                    }
                                } else if (checkTerminalNumber(c)) { // Will "read" a character given a terminal symbol number.
                                    word.append(c);

                                    if (i+1 < line.length()) { // Verifying next char is something.
                                        if (!checkTerminalNumber(line.charAt(i+1))) { // If the next read char is a number.
                                            NEXT_TOKEN = word.toString();
                                            CURRENT_POS = internalPosition + 1;
                                            CURRENT_LINE = internalLine;

                                            if (CURRENT_POS >= line.length()) {
                                                increment = true;
                                            }

                                            formatPosition = true;

                                            return true;
                                        }
                                    }
                                } else { // Not a terminal symbol, so lexeme is still forming.
                                    word.append(c);

                                    if (!checkLanguage(c) && !TERMINATE) { // Verifies the char read is inside the language.
                                        TERMINATE = true;
                                        System.out.println("ERROR: Character '" + c + "' is outside the language. (Line " + CURRENT_LINE
                                                + ", Char " + CURRENT_POS + ")");
                                    }
                                }
                                /////////////////////////////////////////////////////////////////////

                                internalPosition++;
                            }
                        }
                    }
                }

                /////////////////////////////// FINISH READING A LINE OF TEXT FROM DOCUMENT ///////////////////////////////

                /////////////////////////////// BEGIN NEXT LINE OF TEXT FROM DOCUMENT ///////////////////////////////

                if (word.length() != 0) { // Last lexeme in line was forming, so must be completed.
                    NEXT_TOKEN = word.toString();
                    word.setLength(0);
                    CURRENT_POS = internalPosition;
                    CURRENT_LINE = internalLine;

                    if (CURRENT_POS >= line.length()) {
                        increment = true;
                    }

                    return true;
                }
            }

            internalLine++;
            skipLine = false; // Resets a skip line case with "//" symbols.
        }

        // end-of-text output in the case that it was reached.
        if (line != null) {
            CURRENT_POS = line.length() + 1;
            NEXT_TOKEN = line;
        }

        return true;
    }

    /**
     * Checks whether there is a single terminal instance for the concurrent character.
     * @param ch = current character.
     * @return = whether ch is terminal.
     */
    private static boolean checkTerminal(char ch) {
        List<Character> terminals = List.of('<', '>', '=', ':', ';', '-', '*', '/', '+', '(', ')', '!');

        return terminals.contains(ch);
    }

    /**
     * Checks whether the read character is inside the language.
     * @param ch = current character.
     * @return = whether ch is inside language.
     */
    private static boolean checkLanguage(char ch) {
        List<Character> language = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        , 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q'
        , 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'
        , 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        , '<', '>', '=', ':', ';', '-', '*', '/', '+', '_', '(', ')', '!');

        return language.contains(ch);
    }

    /**
     * Checks whether the read character is a terminal number.
     * Used to verify if an identifier needs to be separated given the next character in the line.
     * @param ch = current character
     * @return = whether ch is a number
     */
    private static boolean checkTerminalNumber(char ch) {
        List<Character> numbers = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

        return numbers.contains(ch);
    }

    /**
     * Prints out the current lexeme as a token definition.
     * Todo: For future, checking syntax and execution of text files will have logic here.
     * @param position = current position in text file.
     * @param kind = kind of read lexeme.
     * @param value = value of read lexeme (if applicable).
     */
    public static void print(String position, String kind, String value) {
        String column1Format = "%-30.30s";
        String column2Format = "%-30.30s";
        String column3Format = "%-30.30s";
        String formatInfo = column1Format + " " + column2Format + " " + column3Format;

        System.out.format(formatInfo, position, kind, value);
        System.out.println();
    }

    private static boolean formatPosition = false;

    /**
     * Obtains current position as a String.
     * @return = string of current position for display.
     */
    public static String position() {
        if (formatPosition) {
            formatPosition = false;
            return "Line " + CURRENT_LINE + ", Char " + (NEXT_TOKEN.length() > 1 ? (CURRENT_POS - NEXT_TOKEN.length()): CURRENT_POS - 1);
        } else {
            return "Line " + CURRENT_LINE + ", Char " + (NEXT_TOKEN.length() > 1 ? (CURRENT_POS - NEXT_TOKEN.length()): CURRENT_POS);
        }
    }

    /**
     * Returns a string of the current "kind" of lexeme read.
     * If the "kind" has a valid value, then it will be displayed as either "ID" or "NUM".
     * @return = string value of kind of lexeme.
     */
    public static String kind() {
        switch (NEXT_TOKEN) {
            case "end": {
                return "end-of-text";
            }
            case "if":
            case "then":
            case "else":
            case "fi":
            case "<":
            case "=<":
            case "=":
            case "!=":
            case ">=":
            case ">":
            case "program":
            case ":":
            case "print":
            case "true":
            case "false":
            case "(":
            case ")":
            case ";":
            case "int":
            case "od":
            case "do":
            case "+":
            case "*":
            case "-":
            case "/":
            case ":=":
            case "while":
            case "not":
            case "and":
            case "bool":
            case "or": {
                return NEXT_TOKEN;
            }
            default: {
                try {
                    Integer.parseInt(NEXT_TOKEN);

                    return "NUM";
                } catch (NumberFormatException nfe) {
                    return "ID";
                }
            }
        }
    }

    /**
     * Returns string value of read "kind" of lexeme.
     * Only applicable for NUM and ID, otherwise it will be " ".
     * @return = string "value" of read lexeme.
     */
    public static String value() {
        switch (kind()) {
            case "ID":
            case "NUM": {
                return NEXT_TOKEN;
            }
            default: {
                return " ";
            }
        }
    }
}
