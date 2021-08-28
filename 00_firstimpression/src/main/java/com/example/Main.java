package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        CharStream cs = CharStreams.fromString("hello world");
        HelloLexer lexer = new HelloLexer(cs);
        lexer.addErrorListener(new ErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        HelloParser parser = new HelloParser(tokens);
        //parser.addErrorListener(new ErrorListener());
        AutoCompletion completion = new AutoCompletion();
        Map<String, String> messages = completion.completion(parser);

        System.out.println(messages);

        if (messages.isEmpty()) {
            CharStream cs2 = CharStreams.fromString("hello world");
            HelloLexer lexer2 = new HelloLexer(cs2);
            lexer2.addErrorListener(new ErrorListener());
            CommonTokenStream tokens2 = new CommonTokenStream(lexer2);
            HelloParser parser2 = new HelloParser(tokens2);
            Compiler compiler = new Compiler();
            Listener listener = compiler.compile(parser2);
            System.out.println("println: " + listener.text);
        }
    }

    static class AutoCompletion {

        Map<String, String> completion(HelloParser parser) {
            Map<String, String> completion = new HashMap<>();

            HelloParser.RContext context = parser.r();
            Listener listener = new Listener();
            ParseTreeWalker.DEFAULT.walk(listener, context);
            if (!listener.keyword) {
                completion.put("hello", "");
            }
            if (!(listener.identity == null || listener.identity)) {
                completion.put("identity", "識別子");
            }
            if (!listener.identity) {
                completion.put("identity", "不正な識別子です");
            }
            return completion;
        }
    }

    static class Listener extends HelloBaseListener {
        Boolean keyword;
        Boolean identity;

        String text;

        @Override
        public void enterR(HelloParser.RContext ctx) {
            System.out.println("enterR: " + ctx.getText());
        }

        @Override
        public void exitR(HelloParser.RContext ctx) {
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
            identity = Boolean.TRUE;
            text = node.getText();
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
            System.out.println("Error: " + node.getText());
            if (keyword == null) {
                keyword = Boolean.FALSE;
                return;
            }
            identity = Boolean.FALSE;
        }
    }

    static class Compiler {

        Listener compile(HelloParser parser) throws ParseCancellationException {
            parser.addErrorListener(new ErrorListener());
            HelloParser.RContext context = parser.r();
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
