package com.psoir.projekt;

public class AppListener {


    public static void main(String[] args) {
        final Thread listener = new Thread(new Runnable() {
            public void run() {
            	System.out.println("Edition started");
                SqsListener sqsListener= new SqsListener();
                try {
                    sqsListener.listen();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        listener.start();
    }
}
