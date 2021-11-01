package org.loktevik.Lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lexeme {
    private LexemeClass lexemeClass;
    private LexemeType lexemeType;
    private String value;
    private int position;

    public Lexeme(LexemeClass lexemeClass, LexemeType lexemeType, String value, int position){
        this.lexemeClass = lexemeClass;
        this.lexemeType = lexemeType;
        this.value = value;
        this.position = position;
    }

    public LexemeClass getLexemeClass() {
        return lexemeClass;
    }

    public void setLexemeClass(LexemeClass lexemeClass) {
        this.lexemeClass = lexemeClass;
    }

    public LexemeType getLexemeType() {
        return lexemeType;
    }

    public void setLexemeType(LexemeType lexemeType) {
        this.lexemeType = lexemeType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Class: " + lexemeClass + ", Type: " + lexemeType + ", Value: " + value + ", Pos: " + position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lexeme lexeme = (Lexeme) o;
        return Objects.equals(value, lexeme.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexemeClass, lexemeType, value);
    }
}
