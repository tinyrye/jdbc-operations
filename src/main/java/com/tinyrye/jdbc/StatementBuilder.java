package com.tinyrye.jdbc;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StatementBuilder implements Supplier<String>
{
    public static Supplier<String> line(Supplier<String> sqlSegment) {
        return () -> String.format("%s\n", sqlSegment.get());
    }

    public static Supplier<String> line(String sqlSegment) {
        return () -> String.format("%s\n", sqlSegment);
    }

    public static Supplier<String> separated(Supplier<String> sqlSegment) {
        return () -> String.format(" %s ", sqlSegment.get());
    }

    public static Supplier<String> separated(String sqlSegment) {
        return () -> String.format(" %s ", sqlSegment);
    }

    public static Optional<String> eqCompIfNotNull(String lhs, Optional<Object> paramAsRhs) {
        return binaryClauseIfNotNull(lhs, "=", paramAsRhs);
    }
    
    public static Optional<String> binaryClauseIfNotNull(String lhs, String op, Optional<Object> paramAsRhs) {
        return paramAsRhs.map(param -> String.format("%s %s ?", lhs, op));
    }

    public static String where() {
        return "WHERE";
    }

    public static String orJoin() {
        return "OR";
    }

    public static String andJoin() {
        return "AND";
    }

    public static Predicate<String> hasWhereCheck() {
        return (sql) -> sql.toUpperCase().contains("WHERE");
    }

    private StringBuilder sql = new StringBuilder();
    
    public StatementBuilder() {

    }

    public StatementBuilder(String sql) {
        this.sql.append(sql);
    }
    
    public StatementBuilder(Supplier<String> sql) {
        this.sql.append(sql.get());
    }
    
    public StatementBuilder(StringBuilder sql) {
        this.sql = sql;
    }
    
    public StatementBuilder appendLine(String sqlSegment) {
        sql.append("\n").append(sqlSegment);
        return this;
    }

    protected boolean ensureAndReturnIfAppended(Supplier<String> sqlSegment, Predicate<String> sqlCheck) {
        boolean exists = sqlCheck.test(sql.toString());
        if (! exists) sql.append(sqlSegment.get());
        return exists;
    }
    
    public StatementBuilder ensure(Supplier<String> sqlSegment, Predicate<String> sqlCheck) {
        ensureAndReturnIfAppended(sqlSegment, sqlCheck);
        return this;
    }
    
    public StatementBuilder appendLine(Optional<String> sqlSegment, Supplier<String> orElseSegment) {
        sql.append("\n").append(sqlSegment.orElse(orElseSegment.get()));
        return this;
    }
    
    protected boolean ensureWhereAndReturnIfAppended() {
        return ensureAndReturnIfAppended(() -> where(), hasWhereCheck());
    }
    
    public StatementBuilder appendWhereClauseLine(Supplier<String> clause, String clauseJoin) {
        sql.append("\n");
        boolean needsToJoin = ensureWhereAndReturnIfAppended();
        if (needsToJoin) sql.append(clauseJoin);
        sql.append(' ').append(clause.get());
        return this;
    }
    
    public StatementBuilder appendWhereClauseLine(Optional<String> sqlSegment, String clauseJoin, Supplier<String> orElseSegment) {
        sql.append("\n");
        boolean needsToJoin = ensureWhereAndReturnIfAppended();
        if (needsToJoin) sql.append(clauseJoin);
        sql.append(' ').append(sqlSegment.orElse(orElseSegment.get()));
        return this;
    }
    
    public StatementBuilder andWhereClauseLine(Optional<String> sqlSegment, Supplier<String> orElseSegment) {
        return appendWhereClauseLine(sqlSegment, "AND", orElseSegment);
    }
    
    @Override
    public String get() {
        return sql.toString();
    }
    
    @Override
    public String toString() {
        return sql.toString();
    }
}