package org.loktevik.Lab3;

import org.loktevik.Lab1.LexAnalyser;
import org.loktevik.Lab1.Lexeme;
import org.loktevik.Lab1.LexemeClass;
import org.loktevik.Lab1.LexemeType;

import java.util.*;
import java.util.stream.Collectors;

public class SyntaxSemanticAnalyser {
    public static List<Lexeme> lexemes = new ArrayList<>();
    public static List<PostfixEntry> entryList = new ArrayList<>();
    public static int index;

    public static void main(String[] args) {
        LexAnalyser.main(new String[]{});

        lexemes = LexAnalyser.lexemes
                .stream()
                .sorted(Comparator.comparingInt(Lexeme::getPosition))
                .collect(Collectors.toList());

        index = 0;
        analyse();
    }

    public static void analyse(){
        if (lexemes.get(index).getLexemeType() != LexemeType.BEGIN){
            printError("Ключевое слово begin ожидалось в позиции " +lexemes.get(index).getPosition());
            return;
        }

        while(lexemes.get(index).getLexemeType() != LexemeType.END){
            index++;
            int start = index;
            if (!isWhileStatement()){
                return;
            }
            //else index = start;
        }

        printEntryList();
    }

    public static boolean isWhileStatement(){

        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.DO){
            printError("Ключевое слово do ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }
        index++;
        if (!isStatement()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.LOOP){
            printError("Ключевое слово loop ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        int indFirst = entryList.size();
        index++;
        if (lexemes.get(index).getLexemeType() != LexemeType.WHILE){
            printError("Ключевое слово while ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;
        if (!isCondition()) return false;

        int indJmp = writeCmdPtr(-1);
        writeCmd(ECmd.JZ);

        if (index >= lexemes.size()){
            int position = lexemes.get(index-1).getPosition() + lexemes.get(index-1).getValue().length() + 1;
            printError("Ключевое слово end ожидалось в позиции " + position);
            return false;
        }
        if (lexemes.get(index).getLexemeType() != LexemeType.END){
            printError("Ключевое слово end ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }
        writeCmdPtr(indFirst);
        int indLast = writeCmd(ECmd.JMP);
        setCmdPtr(indJmp, indLast+1);

        //index++;
        return true;
    }

    public static boolean isCondition(){
        if (!isLogExpression()) return false;

        if (index >= lexemes.size())
            return true;

        while (lexemes.get(index) != null && lexemes.get(index).getLexemeType() == LexemeType.OR){
            index++;
            if (!isLogExpression()) return false;
            writeCmd(ECmd.OR);
        }
        return true;
    }

    public static boolean isLogExpression(){
        if (!isRelExpression()) return false;

        if (index >= lexemes.size())
            return true;

        while (lexemes.get(index) != null && lexemes.get(index).getLexemeType() == LexemeType.AND){
            index++;
            if (!isRelExpression())return false;
            writeCmd(ECmd.AND);
        }

        return true;
    }

    public static boolean isRelExpression(){
        if (!isOperand()) return false;

        if (index >= lexemes.size()){
            printError("Оператор сравнения ожидался в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        if (lexemes.get(index).getLexemeType() == LexemeType.COMPARISON){
            ECmd cmd = null;
            String val = lexemes.get(index).getValue();
            if (val.equals("<")) cmd = ECmd.CMPL;
            else if (val.equals("<=")) cmd = ECmd.CMPLE;
            else if (val.equals("<>")) cmd = ECmd.CMPNE;
            else if (val.equals("==")) cmd = ECmd.CMPE;
            index++;
            if (!isOperand()) return false;
            writeCmd(cmd);
        }
        return true;
    }

    public static boolean isOperand(){
        if (index >= lexemes.size()){
            return false;
        }
        if (lexemes.get(index).getLexemeClass() != LexemeClass.CONSTANT
        && lexemes.get(index).getLexemeClass() != LexemeClass.IDENTIFIER){
            printError("Переменная или константа ожидалась в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        if (lexemes.get(index).getLexemeClass() == LexemeClass.IDENTIFIER)
            writeVar(index);
        else
            writeConst(index);
        index++;
        return true;
    }

    public static boolean isLogicalOp(){
        if (lexemes.get(index).getLexemeType() != LexemeType.AND
        && lexemes.get(index).getLexemeType() != LexemeType.OR){
            printError("Логическая операция ожидалась в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        return true;
    }

    public static boolean isStatement(){
        if (lexemes.get(index).getLexemeClass() != LexemeClass.IDENTIFIER){
            printError("Идентификатор ожидался в позиции " +lexemes.get(index).getPosition());
            return false;
        }
        writeVar(index);

        index++;
        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.ASSIGNMENT){
            printError("Присваивание ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;
        if (!isArithmExpr()) return false;
        writeCmd(ECmd.SET);

        return true;
    }

    public static boolean isArithmExpr(){
        if (!isOperand()) return false;

        while (lexemes.get(index).getLexemeType() == LexemeType.ARITHMETIC){
            ECmd cmd = null;
            String val = lexemes.get(index).getValue();
            if (val.equals("+"))  cmd = ECmd.ADD;
            else if (val.equals("-")) cmd = ECmd.SUB;
            index++;
            if (!isOperand()) return false;
            writeCmd(cmd);
        }

        return true;
    }

    public static void printError(String message){
        System.out.println("Ошибка: " + message);
    }

    private static int writeCmd(ECmd cmd){
        PostfixEntry entry = new PostfixEntry();
        entry.setType(EEntryType.CMD);
        entry.setCmd(cmd);
        entryList.add(entry);
        return entryList.size()-1;
    }

    private static int writeVar(int index){
        PostfixEntry variable = new PostfixEntry();
        variable.setType(EEntryType.VAR);
        variable.setValue(lexemes.get(index).getValue());
        entryList.add(variable);
        return entryList.size()-1;
    }

    private static int writeConst(int index){
        PostfixEntry con = new PostfixEntry();
        con.setType(EEntryType.CONST);
        con.setValue(lexemes.get(index).getValue());
        entryList.add(con);
        return entryList.size()-1;
    }

    private static int writeCmdPtr(int ptr){
        PostfixEntry cmdPtr = new PostfixEntry();
        cmdPtr.setType(EEntryType.CMDPTR);
        cmdPtr.setCmdPtr(ptr);
        entryList.add(cmdPtr);
        return entryList.size()-1;
    }

    private static void setCmdPtr(int index, int ptr){
        entryList.get(index).setCmdPtr(ptr);
    }

    private static void printEntryList(){
        for (PostfixEntry entry : entryList) {
            System.out.print(entry + " ");
        }
    }
}
