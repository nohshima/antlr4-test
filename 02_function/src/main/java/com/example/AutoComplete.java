package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

public class AutoComplete {

    private static List<String> FUNCTIONS = new ArrayList<>() {
        {
            add("ADD");
            add("SUB");
            add("MUL");
            add("DIV");
            add("SUM");
        }
    };

    public static void main(String[] args) {
        String inputSource = "S";
        CharStream cs = CharStreams.fromString(inputSource);
        ErrorListener errorListener = new ErrorListener();

        ExprLexer lexer = new ExprLexer(cs);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ExprParser parser = new ExprParser(tokens);
        parser.addErrorListener(errorListener);
        //parser.addErrorListener(new ErrorListener());
        AutoCompletion completion = new AutoCompletion();
        List<String> messages = completion.completion(parser);

        System.out.println(messages);
    }

    static class AutoCompletion {

        List<String> completion(ExprParser parser) {
            ExprParser.FunctionContext context = parser.function();
            Listener listener = new Listener();
            ParseTreeWalker.DEFAULT.walk(listener, context);
            List<String> completion = FUNCTIONS.stream()
                    .filter(function -> function.startsWith(listener.functionName))
                    .collect(Collectors.toList());
            return completion;
        }
    }

    static class Listener extends ExprBaseListener {
        String functionName;
        List<Integer> arguments = new ArrayList<>();

        @Override
        public void enterFunction(ExprParser.FunctionContext ctx) {
            System.out.println("enterR: " + ctx.getPayload());
        }

        @Override
        public void exitFunction(ExprParser.FunctionContext ctx) {
            System.out.println("exitR: " + ctx.getPayload());
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            System.out.println("enter: " + ctx.getPayload());
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            System.out.println("exit: " + ctx.getText());
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            System.out.println("Terminal: " + node.getText());
            if (node.getSymbol().getType() == ExprLexer.ID) {
                this.functionName = node.getText();
            }
            if (node.getSymbol().getType() == ExprLexer.NUMBER) {
                arguments.add(Integer.parseInt(node.getText()));
            }
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
            System.out.println("Error: " + node.getPayload());
        }
    }

    static class ErrorListener extends BaseErrorListener {
        List<String> errors = new ArrayList<>();
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            errors.add(msg);
        }
    }
}
