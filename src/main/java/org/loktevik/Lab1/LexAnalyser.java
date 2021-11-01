package org.loktevik.Lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LexAnalyser {
    public static int numOfStates;
    public static List<String> alphabet = new ArrayList<>();
    public static int [][] transFunc;
    public static int startState;
    public static int [] endStates;
    public static List<String> keywords = new ArrayList<>();
    public static List<String> identifiers = new ArrayList<>();
    public static List<String> constants = new ArrayList<>();
    public static List<String> specialSymbols = new ArrayList<>();
    public static Map<Integer, String> stateMap = new HashMap<>();
    public static List<Lexeme> lexemes = new ArrayList<>();

    private static void loadKeywords() throws IOException {
        Path path = Paths.get("src/main/resources/lab1_resources/keywords.txt");
        BufferedReader reader = Files.newBufferedReader(path);

        while (reader.ready()){
            keywords.add(reader.readLine());
        }
    }

    private static void loadAlphabet(){
        for (int i = 65; i <= 90; i++){
            alphabet.add(String.valueOf((char)i));
            alphabet.add(String.valueOf((char)(i + 32)));
        }

        for (int i=0; i <= 9; i++){
            alphabet.add(String.valueOf(i));
        }

        alphabet.addAll(Arrays.asList("> < = + -".split(" ")));
    }

    private static void loadStateMap(){
        stateMap.put(0, "Start");
        stateMap.put(1, "Identifier");
        stateMap.put(2, "Constant");
        stateMap.put(3, "Arithmetic");
        stateMap.put(4, "LessComparison");
        stateMap.put(5, "Assignment");
        stateMap.put(6, "MoreOrEqualComparison");
        stateMap.put(7, "Equality");
        stateMap.put(8, "Final");
    }

    public static void main(String[] args) {
        try {
            loadKeywords();
            loadAlphabet();
            loadStateMap();
            prepareData();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        System.out.println("\nВведите строку на вход автомата: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        startAutomate(input + " ");

        List list = new ArrayList();
    }

    public static void startAutomate(String input){
        int currentState = startState;
        String [] inputArray = input.split("");
        StringBuilder lexeme = new StringBuilder();
        int lexemeIndex = 0;
        int movingIndex = 0;

        for (String symbol : inputArray){
            movingIndex++;
            int index = getTableIndex(symbol);
            if (index != 7)
                lexeme.append(symbol);

            int previousState = currentState;
            currentState = transFunc[currentState][index];

            if (currentState == 8){
                checkLexemeType(lexeme.toString());
                getLexemeInfo(previousState, lexeme.toString(), lexemeIndex);
                lexemeIndex = movingIndex;
                lexeme.setLength(0);
            }
        }

        boolean result = false;
        for (int i = 0; i < endStates.length; i++){
            if (currentState == endStates[i]){
                result = true;
                break;
            }
        }

        if (result){
            System.out.println("Выражение принято.");

            System.out.println();
            printLexemes();
        }
        else
            System.out.println("Строка отвергнута.");
    }

    public static int getTableIndex(String symbol){
        if (Character.isDigit(symbol.charAt(0)))
            return 1;

        if (symbol.charAt(0) >= 65 && symbol.charAt(0) <= 122)
            return 0;

        return switch (symbol) {
            case "+" -> 2;
            case "-" -> 3;
            case "<" -> 4;
            case ">" -> 5;
            case "=" -> 6;
            default -> 7;
        };
    }

    public static void checkLexemeType(String lexeme){
        for (String s : "+-<>=".split(""))
            if (lexeme.contains(s)){
                specialSymbols.add(lexeme);
                return;
            }
        if (Character.isDigit(lexeme.charAt(0)))
            constants.add(lexeme);
        else if (!keywords.contains(lexeme))
            identifiers.add(lexeme.toString());
    }

    public static void getLexemeInfo(int previousState, String lexeme, int position){
        String state = stateMap.get(previousState);
        LexemeType lexemeType = null;
        LexemeClass lexemeClass = null;
        switch (state) {
            case "Identifier":
                if (keywords.contains(lexeme)) {
                    lexemeClass = LexemeClass.KEYWORD;
                    for (LexemeType lex : LexemeType.values()) {
                        if (lex.toString().toLowerCase(Locale.ROOT).equals(lexeme)) {
                            lexemeType = lex;
                        }
                    }
                } else {
                    lexemeClass = LexemeClass.IDENTIFIER;
                    lexemeType = LexemeType.UNDEFINED;
                }
                break;
            case "Constant":
                lexemeClass = LexemeClass.CONSTANT;
                lexemeType = LexemeType.UNDEFINED;
                break;
            case "Arithmetic":
                lexemeClass = LexemeClass.SPECIAL_SYMBOL;
                lexemeType = LexemeType.ARITHMETIC;
                break;
            case "LessComparison":
                lexemeClass = LexemeClass.SPECIAL_SYMBOL;
                lexemeType = LexemeType.COMPARISON;
                break;
            case "Assignment":
                lexemeClass = LexemeClass.SPECIAL_SYMBOL;
                lexemeType = LexemeType.ASSIGNMENT;
                break;
            case "MoreOrEqualComparison":
                lexemeClass = LexemeClass.SPECIAL_SYMBOL;
                lexemeType = LexemeType.COMPARISON;
                break;
            case "Equality":
                lexemeClass = LexemeClass.SPECIAL_SYMBOL;
                lexemeType = LexemeType.COMPARISON;
                break;
            default:
                return;
        }

        Lexeme lex = new Lexeme(lexemeClass, lexemeType, lexeme, position);
        lexemes.add(lex);
    }

    private static void printLexemes(){
        System.out.println("\nКлючевые слова: ");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getLexemeClass() == LexemeClass.KEYWORD)
                .collect(Collectors.toList())){
            System.out.println(lex);
        }

        System.out.println("\nИдентификаторы: ");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getLexemeClass() == LexemeClass.IDENTIFIER)
                .collect(Collectors.toList())){

            System.out.println(lex);
        }

        System.out.println("\nКонстанты: ");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getLexemeClass() == LexemeClass.CONSTANT)
                .collect(Collectors.toList())){

            System.out.println(lex);
        }

        System.out.println("\nСпециальные символы: ");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getLexemeClass() == LexemeClass.SPECIAL_SYMBOL)
                .collect(Collectors.toList())){

            System.out.println(lex);
        }
    }

    public static void prepareData() throws IOException {
        numOfStates = 9;
        int alphabetLength = 8;
        //начальное состояние
        startState = 0;
        //конечное состояние
        endStates = new int [] {8};

        Path path = Paths.get("src/main/resources/lab1_resources/trans_table.txt");
        BufferedReader reader = Files.newBufferedReader(path);

        //матрица переходов
        transFunc = new int[numOfStates][8]; //размерность массива функции переходов
        for (int i=0; i < numOfStates; i++){
            String[] states = reader.readLine().split(" ");
            for (int j=0; j<alphabetLength; j++){
                transFunc[i][j] = Integer.parseInt(states[j]);
            }
        }
    }
}
