package org.example;

import javax.swing.*;

// 启动类
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MySwingApp();
            }
        });
    }
}