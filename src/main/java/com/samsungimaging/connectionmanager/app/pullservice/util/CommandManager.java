package com.samsungimaging.connectionmanager.app.pullservice.util;

import java.util.Stack;

/* JADX INFO: loaded from: classes.dex */
public class CommandManager extends Thread {
    private Stack commands = new Stack();
    private ListenerList commandRequestListenerList = new ListenerList();

    public void queueCommand(Object command) {
        synchronized (this.commands) {
            this.commands.push(command);
            this.commands.notifyAll();
        }
        if (getState() == Thread.State.NEW) {
            start();
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Object command;
        while (true) {
            try {
                if (this.commands.size() == 0) {
                    synchronized (this.commands) {
                        this.commands.wait();
                    }
                }
                if (this.commands.size() != 0) {
                    synchronized (this.commands) {
                        command = this.commands.remove(0);
                    }
                    performOnCommandListener(command);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void stopThread() {
        interrupt();
    }

    public void addCommandRequestListener(CommandRequestListener listener) {
        this.commandRequestListenerList.add(listener);
    }

    public void removeCommandRequestListener(CommandRequestListener listener) {
        this.commandRequestListenerList.remove(listener);
    }

    public void performOnCommandListener(Object command) {
        int listenerSize = this.commandRequestListenerList.size();
        for (int i = 0; i < listenerSize; i++) {
            CommandRequestListener listener = (CommandRequestListener) this.commandRequestListenerList.get(i);
            listener.onCommand(command);
        }
    }
}
