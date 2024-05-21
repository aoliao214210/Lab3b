package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySwingApp extends JFrame {
    private JPanel inputPanel;          // 输入panel
    private JPanel outputPanel;         // 输出panel
    private JPanel buttonPanel;         // 按钮panel
    private JTextField inputField;      // 输入框
    private JTextArea outputArea;       // 文字输出框
    private GraphDrawer graphDrawer;  // 图可视化框
    private JScrollPane textPane;       // 文字panel
    private JButton generateGraphButton;// 生成有向图按钮
    private JButton showGraphButton;    // 展示有向图按钮
    private JButton saveGraphButton;    // 保存有向图按钮
    private JButton queryBridgeButton;  // 查询桥接词
    private JButton generateNewTextButton;  // 产生新文本
    private JButton shortestPathButton;     // 获取最短路
    private JButton randomWalkButton;       // 随机游走
    private JButton stopWalkButton;             // 停止
    private TextGraph textGraph;


    // 构造方法
    public MySwingApp() {
        setTitle("Graph");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textGraph = new TextGraph();

        // 输入输出框
        inputPanel = new JPanel();
        inputField = new JTextField(30);
        generateGraphButton = new JButton("Generate Graph");
        inputPanel.add(inputField);
        inputPanel.add(generateGraphButton);

        // 输出框
        outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        // 文字输出区
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        textPane = new JScrollPane(outputArea);
        // 图片展示区
        // 组合输出框
        outputPanel.add(textPane, BorderLayout.NORTH);


        // 功能区
        buttonPanel = new JPanel();
        showGraphButton = new JButton("Show Graph");
        saveGraphButton = new JButton("Save Graph");
        queryBridgeButton = new JButton("Query Bridge");
        generateNewTextButton = new JButton("Generate New Text");
        shortestPathButton = new JButton("Shortest Path");
        randomWalkButton = new JButton("Random Walk");
        stopWalkButton = new JButton("Stop");

        // 设置事件
        addButtonActions();

        // 向面板中添加各个元素
        inputPanel.add(generateGraphButton);
        buttonPanel.add(showGraphButton);           // 添加按钮
        buttonPanel.add(saveGraphButton);
        buttonPanel.add(queryBridgeButton);
        buttonPanel.add(generateNewTextButton);
        buttonPanel.add(shortestPathButton);
        buttonPanel.add(randomWalkButton);
        buttonPanel.add(stopWalkButton);
        add(inputPanel, BorderLayout.NORTH);        // 添加面板
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 设置可见状态
        setVisible(true);
    }

    // 添加事件
    private void addButtonActions(){
        // 生成有向图
        generateGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateGraphAction();
            }
        });

        // 展示有向图
        showGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textGraph.showDirectedGraph();
                showDirectedGraph();
            }
        });

        // 保存有向图
        saveGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGraphAction();
            }
        });

        // 查询桥接词
        queryBridgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryBridgeAction();
            }
        });

        // 生成新文本
        generateNewTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewTextAction();
            }
        });

        // 获取最短路
        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shortestPathAction();
            }
        });

        // 随机游走
        randomWalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomWalkAction();
            }
        });

        // 停止
        stopWalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopWalkAction();
            }
        });
    }

    // 生成图事件
    private void generateGraphAction(){
        // 获取文件路径
        String filePath = inputField.getText();
        inputField.setText("");
        // 默认路径
        if(filePath.isEmpty()){
            filePath = "E:\\Java\\SE-lab1-mvn\\src\\main\\java\\org\\example\\test.txt";
        }

        boolean flag = textGraph.processFile(filePath);
        if(!flag){
            outputArea.setText("请输入正确路径");
        }else{
            outputArea.setText("生成有向图成功！");
        }
        return;
    }

    // 展示图事件
    private void showDirectedGraph(){
//        Map<String, Map<String, Integer>> graph = GraphDrawer.createExampleGraph();
        graphDrawer = new GraphDrawer(textGraph.graph, textGraph.root);
        outputPanel.add(graphDrawer, BorderLayout.CENTER);
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    // 保存图事件
    private void saveGraphAction(){
        File imageFile = new File("graph.png");
        graphDrawer.saveGraphAsImage(imageFile);
    }

    // 桥接词事件
    private void queryBridgeAction(){
        // 获取用户输入
        String input = inputField.getText();
        inputField.setText("");
        String[] words = input.split(" ");

        // 验证输入
        if(words.length == 2){
            if (!textGraph.graph.containsKey(words[0]) || !textGraph.graph.containsKey(words[1])) {
                outputArea.setText("No " + words[0] + " or " + words[1] + " in the graph!");
                return;
            }
        }else{
            outputArea.setText("请输入两个单词");
            return;
        }

        // 获取输出
        Set<String> bridgeWords = textGraph.queryBridgeWords(words[0], words[1]);

        // 检验输出
        if (bridgeWords.isEmpty()) {
            outputArea.setText("No bridge words from " + words[0] + " to " + words[1] + "!");
        } else {
            outputArea.setText("The bridge words from " + words[0] + " to " + words[1] + " are: \n " + String.join(", ", bridgeWords) + ".");
        }
    }

    // 产生文本事件
    private void generateNewTextAction(){
        String input = inputField.getText().toLowerCase();

        if(input.isEmpty()){
            outputArea.setText("请输入文本");
            return;
        }

        String newText = textGraph.generateNewText(input);
        outputArea.setText(newText);
        return;
    }

    // 获取最短路
    private void shortestPathAction(){
        // 获取输入
        String input = inputField.getText();
        if(input.isEmpty()){
            outputArea.setText("输入两个单词");
            return;
        }
        String[] words = input.split(" ");
        String word1 = words[0].toLowerCase();
        String word2 = words[1].toLowerCase();

        // 检查输入
        textGraph.graphValues = textGraph.graph.values();
        int word_check1=0,word_check2=0,word_check=0;
        for (Map<String, Integer> map : textGraph.graphValues) {
            // 检查当前Map中是否包含目标字符串作为键
            if (map.containsKey(word1) ){
                //System.out.println("集合中包含目标字符串作为键。");
                word_check1=1;
            }
            if(map.containsKey(word2) ){
                word_check2=1;
            }
            if( word_check1==1 && word_check2==1){
                word_check=1;
                break;
            }
            // 检查当前Map中是否包含目标字符串作为值
        }
        if((!textGraph.graph.containsKey(word1)||!textGraph.graph.containsKey(word2) )&& word_check==0){
            outputArea.setText("No " + word1 + " or " + word2 + " in the graph!");
        }

        List<List<String>> allPaths = textGraph.calcShortestPath(word1, word2);

        if (allPaths.isEmpty()) {
            outputArea.setText("No path from " + word1 + " to " + word2 + "!");
        } else {
            outputArea.setText("All shortest paths from " + word1 + " to " + word2 + ":");
            for (List<String> path : allPaths) {
                outputArea.append(String.join(" -> ", path) + " (length=" + textGraph.calculatePathLength(path) + ")");
            }
            graphDrawer.setSpecialPath(allPaths.get(0));
        }
    }

    // 随机游走事件
    private void randomWalkAction(){
        outputArea.setText("开始随机游走");
        textGraph.randomWalk();
        outputArea.setText("结束！");
    }

    // 停止游走事件
    private void stopWalkAction(){
        textGraph.toStop = true;
    }

}
