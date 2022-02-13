package org.loktevik.Lab2;

import org.loktevik.Lab1.LexAnalyser;
import org.loktevik.Lab1.Lexeme;
import org.loktevik.Lab1.LexemeClass;
import org.loktevik.Lab1.LexemeType;

import java.util.*;
import java.util.stream.Collectors;

public class SyntaxAnalyser {
    public static List<Lexeme> lexemes = new ArrayList<>();
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
            else index = start;
        }
    }

    public static boolean isWhileStatement(){
        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.DO){
            int position = index >= lexemes.size() ? getAfterIndex() : lexemes.get(index).getPosition();
            printError("Ключевое слово do ожидалось в позиции " + position);
            return false;
        }
        index++;
        if (!isStatement()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.LOOP){
            int position = index >= lexemes.size() ? getAfterIndex() : lexemes.get(index).getPosition();
            printError("Ключевое слово loop ожидалось в позиции " + position);
            return false;
        }

        index++;
        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.WHILE){
            int position = index >= lexemes.size() ? getAfterIndex() : lexemes.get(index).getPosition();
            printError("Ключевое слово while ожидалось в позиции " + position);
            return false;
        }

        index++;
        if (!isCondition()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.END){
            int position = index >= lexemes.size() ? getAfterIndex() : lexemes.get(index).getPosition();
            printError("Ключевое слово end ожидалось в позиции " + position);
            return false;
        }
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
        }
        return true;
    }

    public static boolean isLogExpression(){
        if (!isRelExpression()) return false;

        if (index >= lexemes.size())
            return true;

        while (lexemes.get(index) != null && lexemes.get(index).getLexemeType() != LexemeType.AND){
            index++;
            if (!isRelExpression())return false;
        }

        return true;
    }

    public static boolean isRelExpression(){
        if (!isOperand()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.COMPARISON){
            printError("Оператор сравнения ожидался в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        if (lexemes.get(index).getLexemeType() == LexemeType.COMPARISON){
            index++;
            if (!isOperand()) return false;
        }
        return true;
    }

    public static boolean isOperand(){
        if (index >= lexemes.size() || (lexemes.get(index).getLexemeClass() != LexemeClass.CONSTANT
        && lexemes.get(index).getLexemeClass() != LexemeClass.IDENTIFIER)){
            printError("Переменная или константа ожидалась в позиции " +lexemes.get(index).getPosition());
            return false;
        }
        index++;
        return true;
    }

    public static boolean isLogicalOp(){
        if (index >= lexemes.size() || (lexemes.get(index).getLexemeType() != LexemeType.AND
        && lexemes.get(index).getLexemeType() != LexemeType.OR)){
            printError("Логическая операция ожидалась в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        return true;
    }

    public static boolean isStatement(){
        if (index >= lexemes.size() || lexemes.get(index).getLexemeClass() != LexemeClass.IDENTIFIER){
            int position = index >= lexemes.size() ? getAfterIndex() : lexemes.get(index).getPosition();
            printError("Идентификатор ожидался в позиции " + position);
            return false;
        }

        index++;
        if (index >= lexemes.size() || lexemes.get(index).getLexemeType() != LexemeType.ASSIGNMENT){
            int position = index >= lexemes.size() ? getAfterIndex() : lexemes.get(index).getPosition();
            printError("Присваивание ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;
        if (!isArithmExpr()) return false;

        return true;
    }

    public static boolean isArithmExpr(){
        if (!isOperand()) return false;

        while (index < lexemes.size() && lexemes.get(index).getLexemeType() == LexemeType.ARITHMETIC){
            index++;
            if (!isOperand()) return false;
        }

        return true;
    }

    public static int getAfterIndex(){
        int position = -1;
        if (index >= lexemes.size())
            position = lexemes.get(index-1).getPosition() + lexemes.get(index-1).getValue().length() + 1;
        else
            position = lexemes.get(index).getPosition();

        return position;
    }

    public static void printError(String message){
        System.out.println("Ошибка: " + message);
    }
}
