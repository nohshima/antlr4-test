package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executer {

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
//        String inputSource = "ADD(1, 2)";
//        String inputSource = "SUB(1, 2)";
//        String inputSource = "MUL(2, 3)";
//        String inputSource = "DIV(4, 2)";
        String inputSource = "SUM(1, 2, 3, 4, 5, 6)";
        CharStream cs = CharStreams.fromString(inputSource);
        ErrorListener errorListener = new ErrorListener();

        ExprLexer lexer = new ExprLexer(cs);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        if (!errorListener.errors.isEmpty()) {
            System.out.println("不正な識別子が存在します。");
            return;
        }

        ExprParser parser = new ExprParser(tokens);
        parser.addErrorListener(errorListener);
        //parser.addErrorListener(new ErrorListener());
        Compiler compile = new Compiler();
        compile.compile(parser);

        if (!errorListener.errors.isEmpty()) {
            System.out.println("文法規則が不正です。");
            return;
        }

        if (compile.listener.functionName.equals("ADD")) {
            if (compile.listener.arguments.size() != 2) {
                System.out.println("不正な引数です");
                return;
            }
            int result = compile.listener.arguments.get(0) + compile.listener.arguments.get(1);
            System.out.println("RESULT: " + result);
        }
        if (compile.listener.functionName.equals("SUB")) {
            if (compile.listener.arguments.size() != 2) {
                System.out.println("不正な引数です");
                return;
            }
            int result = compile.listener.arguments.get(0) - compile.listener.arguments.get(1);
            System.out.println("RESULT: " + result);
        }
        if (compile.listener.functionName.equals("MUL")) {
            if (compile.listener.arguments.size() != 2) {
                System.out.println("不正な引数です");
                return;
            }
            int result = compile.listener.arguments.get(0) * compile.listener.arguments.get(1);
            System.out.println("RESULT: " + result);
        }
        if (compile.listener.functionName.equals("DIV")) {
            if (compile.listener.arguments.size() != 2) {
                System.out.println("不正な引数です");
                return;
            }
            int result = compile.listener.arguments.get(0) / compile.listener.arguments.get(1);
            System.out.println("RESULT: " + result);
        }
        if (compile.listener.functionName.equals("SUM")) {
            if (compile.listener.arguments.size() < 2) {
                System.out.println("不正な引数です");
                return;
            }
            int result = compile.listener.arguments.stream().mapToInt(Integer::intValue).sum();
            System.out.println("RESULT: " + result);
        }
    }

    static class Compiler {
        Listener listener = new Listener();

        void compile(ExprParser parser) {
            ExprParser.FunctionContext context = parser.function();
            ParseTreeWalker.DEFAULT.walk(listener, context);
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
