package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        CharStream cs = CharStreams.fromString("SUM(1, 2, 5, 100)");
        ExprLexer lexer = new ExprLexer(cs);
        lexer.addErrorListener(new ErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ExprParser parser = new ExprParser(tokens);
        //parser.addErrorListener(new ErrorListener());
        AutoCompletion completion = new AutoCompletion();
        Map<String, String> messages = completion.completion(parser);

        System.out.println(messages);

        if (messages.isEmpty()) {
            CharStream cs2 = CharStreams.fromString("SUM(1, 2, 5, 100)");
            ExprLexer lexer2 = new ExprLexer(cs2);
            lexer2.addErrorListener(new ErrorListener());
            CommonTokenStream tokens2 = new CommonTokenStream(lexer2);
            ExprParser parser2 = new ExprParser(tokens2);
            Compiler compiler = new Compiler();
            Listener listener = compiler.compile(parser2);
            System.out.println("println: " + listener.arguments.stream().mapToLong(Long::parseLong).sum());
        }
    }

    static class AutoCompletion {

        Map<String, String> completion(ExprParser parser) {
            Map<String, String> completion = new HashMap<>();

            ExprParser.FunctionContext context = parser.function();
            Listener listener = new Listener();
            ParseTreeWalker.DEFAULT.walk(listener, context);
            if (!listener.keyword) {
                completion.put("SUM", "");
            }
            if (listener.argumentsError) {
                completion.put("arguments", "引数が間違っています");
            }
            return completion;
        }
    }

    static class Listener extends ExprBaseListener {
        Boolean keyword;
        Boolean argumentsError = Boolean.FALSE;

        List<String> arguments = new ArrayList<>();

        @Override
        public void enterFunction(ExprParser.FunctionContext ctx) {
            System.out.println("enterR: " + ctx.getText());
        }

        @Override
        public void exitFunction(ExprParser.FunctionContext ctx) {
            System.out.println("exitR: " + ctx.getText());
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            System.out.println("enter: " + ctx.getText());
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            System.out.println("exit: " + ctx.getText());
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            System.out.println("Terminal: " + node.getText());
            if (keyword == null) {
                keyword = Boolean.TRUE;
                return;
            }
            if (",".equals(node.getText())) return;
            if (")".equals(node.getText())) return;
            arguments.add(node.getText());
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
            System.out.println("Error: " + node.getText());
            if (keyword == null) {
                keyword = Boolean.FALSE;
                return;
            }
            this.argumentsError = Boolean.TRUE;
        }
    }

    static class Compiler {

        Listener compile(ExprParser parser) throws ParseCancellationException {
            parser.addErrorListener(new ErrorListener());
            ExprParser.FunctionContext context = parser.function();
            Listener listener = new Listener();
            ParseTreeWalker.DEFAULT.walk(listener, context);
            return listener;
        }
    }

    static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
            throw new ParseCancellationException(msg);
        }
    }
}
