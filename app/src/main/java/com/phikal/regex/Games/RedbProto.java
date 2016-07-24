package com.phikal.regex.Games;

import android.util.Log;

import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RedbProto {

    private static final Pattern
            linep = Pattern.compile("^(.)(?: (.+))?$");

    private static final char
            INFO = '@', ERROR = '!', INPUT = ':',
            MATCH = '+', DMATCH = '-', ANSWR = '>';
    private static RedbConn conn = null;

    public RedbProto(String host) {
        if (conn == null)
            (conn = new RedbConn(host)).start();
    }

    private static boolean shouldWait(char c) {
        switch (c) {
            case INPUT:
            case ANSWR:
                return true;
        }
        return false;
    }

    public String aboutServer() {
        return conn.about;
    }

    public Task requestTask(int level) {
        try {
            conn.notifyOnInput(this);
            synchronized (this) {
                this.wait();
            }
            conn.write(String.valueOf(level));
            List<ProtoLine> lines = conn.getLinesSinceLastRequest();
            List<Word> right = new ArrayList<>(),
                    wrong = new ArrayList<>();

            for (ProtoLine line : lines) {
                switch (line.mode) {
                    case MATCH:
                        right.add(new Word(line.msg));
                        break;
                    case DMATCH:
                        right.add(new Word(line.msg));
                        break;
                }
            }

            new Task(right, wrong, -1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    public void submitSolution(String sol) {
        conn.notifyOnInput(this);
        try {
            synchronized (this) {
                this.wait();
            }
            conn.write(sol);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public String hasFailed() {
        if (conn.getMode() == ERROR) return conn.getMsg();
        return null;
    }

    public static class RedbError extends Exception {
        private String m;

        public RedbError(String m) {
            this.m = m;
        }

        @Override
        public String getMessage() {
            return m;
        }
    }

    private class ProtoLine {
        public final char mode;
        public final String msg;

        public ProtoLine(char mode, String msg) {
            this.mode = mode;
            this.msg = msg;
        }
    }

    private class RedbConn extends Thread {

        Socket conn = null;
        String host, about = null;

        private volatile LinkedList<ProtoLine> lines = new LinkedList<>();
        private volatile int lastRequest = 0;
        private PrintWriter writer;
        private BufferedReader reader;
        private Object waiter = null;

        protected RedbConn(String host) {
            this.host = host;
        }

        @Override
        public void run() {
            try {
                if (conn == null) {
                    conn = new Socket(host, 25921);
                    writer = new PrintWriter(conn.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                }

                while (reader.ready()) {
                    String pline = reader.readLine();
                    Log.d("redb_raw", pline);
                    if (pline == null)
                        break; // communication ended

                    Matcher m = linep.matcher(pline);
                    if (!m.matches())
                        throw new RedbError("Invalid Protocol: " + pline);

                    ProtoLine line = new ProtoLine(m.group(1).charAt(0), m.group(2));
                    lines.addFirst(line);
                    lastRequest++;

                    switch (line.mode) {
                        case ERROR:
                            throw new RedbError(line.msg);
                        case INPUT:
                        case ANSWR:
                            synchronized (this) {
                                this.wait();
                            }
                            if (waiter != null)
                                waiter.notify();
                            waiter = null;
                            break;
                        case INFO:
                            if (about == null)
                                about = line.msg;
                            Log.d("redb_conn_info", line.msg);
                            break;
                    }
                }
                reader.close();
                writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (RedbError re) {
                re.printStackTrace();
            }
            Log.d("redb_conn", "connection to \"" + host + "\" quit.");
        }

        public void write(String msg) {
            if (shouldWait(getMode())) {
                writer.println(msg);
                this.notify();
            }
        }

        public void notifyOnInput(Object obj) {
            waiter = obj;
        }

        public List<ProtoLine> getLines() {
            return lines;
        }

        public List<ProtoLine> getLinesSinceLastRequest() {
            int last = lastRequest;
            lastRequest = 0;
            return lines.subList(0, last);
        }

        public char getMode(int i) {
            if (lines.size() == 0) return (char) -1;
            return lines.get(i).mode;
        }

        public char getMode() {
            return getMode(0);
        }

        public String getMsg(int i) {
            if (lines.size() == 0) return null;
            return lines.get(i).msg;
        }

        public String getMsg() {
            return getMsg(0);
        }
    }
}
