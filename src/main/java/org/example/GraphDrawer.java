package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GraphDrawer extends JScrollPane {

    private Map<String, Map<String, Integer>> graph;
    private String root;
    private List<String> specialPath;
    public GraphDrawer(Map<String, Map<String, Integer>> graph, String root) {
        this.graph = graph;
        this.root = root;
        GraphPanel graphPanel = new GraphPanel();
        setViewportView(graphPanel);
        setPreferredSize(new Dimension(800, 600)); // 设置首选大小
        graphPanel.setPreferredSize(graphPanel.calculatePreferredSize()); // 设置GraphPanel的首选大小
    }

    public void setSpecialPath(List<String> path) {
        this.specialPath = path;
        repaint();
    }

    private class GraphPanel extends JPanel {

        // 重写构造方法
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGraph(g);
        }

        // 根据节点位置计算画布大小
        private Dimension calculatePreferredSize() {
            // 起始位置
            int xOffset = 50;
            int yOffset = 50;
            int xStep = 150;
            int yStep = 150;

            int maxWidth = 0;
            int maxHeight = 0;

            // 按层次结构排列节点
            Map<String, Point> positions = calculateNodePositions(xOffset, yOffset, xStep, yStep);

            for (Point position : positions.values()) {
                maxWidth = Math.max(maxWidth, position.x);
                maxHeight = Math.max(maxHeight, position.y);
            }

            return new Dimension(maxWidth + xStep, maxHeight + yStep);
        }

        private void drawGraph(Graphics g) {
            int xOffset = 50;
            int yOffset = 100;
            int xStep = 150;
            int yStep = 150;

            // 按层次结构排列节点
            Map<String, Point> positions = calculateNodePositions(xOffset, yOffset, xStep, yStep);

            System.out.println("-----check------");
            System.out.println(graph.keySet());
            System.out.println(positions.keySet());
            // 遍历图中所有节点
            for (String vertex : graph.keySet()) {
                Point position = positions.get(vertex);
                int x = position.x;
                int y = position.y;

                // 计算节点的大小
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(vertex);
                int textHeight = fm.getHeight();
                int nodeWidth = textWidth + 10; // 添加一些额外空间
                int nodeHeight = textHeight + 10; // 添加一些额外空间

                // 绘制节点
                g.setColor(Color.BLACK);
                g.drawOval(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight);
                g.drawString(vertex, x - textWidth / 2, y + textHeight / 4);

                // 绘制边和权重
                Map<String, Integer> neighbors = graph.get(vertex);
                int edgeIndex = 0;
                for (String neighbor : neighbors.keySet()) {
                    Point neighborPosition = positions.get(neighbor);
                    int nx = neighborPosition.x;
                    int ny = neighborPosition.y;

                    // 检查是否是特殊路径的一部分
                    boolean isSpecialPath = false;
                    if (specialPath != null) {
                        for (int i = 0; i < specialPath.size() - 1; i++) {
                            if (specialPath.get(i).equals(vertex) && specialPath.get(i + 1).equals(neighbor)) {
                                isSpecialPath = true;
                                break;
                            }
                        }
                    }

                    // 绘制贝塞尔曲线箭头
                    drawBezierArrow(g, x, y, nx, ny, nodeWidth / 2, nodeHeight / 2, edgeIndex, isSpecialPath);

                    // 绘制权重
                    g.drawString(neighbors.get(neighbor).toString(), (x + nx) / 2, (y + ny) / 2 - 10 * edgeIndex);
                    edgeIndex++;
                }
            }
        }

        // 箭头绘制
        private void drawBezierArrow(Graphics g, int x1, int y1, int x2, int y2, int nodeWidth, int nodeHeight, int edgeIndex, boolean isSpecialPath) {
            int ctrlX1 = (x1 + x2) / 2;
            int ctrlY1 = y1 - 50 - edgeIndex * 20;
            int ctrlX2 = (x1 + x2) / 2;
            int ctrlY2 = y2 - 50 - edgeIndex * 20;

            // 计算箭头的起始和终止位置，使其不进入圆形节点内部
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int arrowStartX = (int) (x1 + nodeWidth * Math.cos(angle));
            int arrowStartY = (int) (y1 + nodeHeight * Math.sin(angle));
            int arrowEndX = (int) (x2 - nodeWidth * Math.cos(angle));
            int arrowEndY = (int) (y2 - nodeHeight * Math.sin(angle));

            Graphics2D g2d = (Graphics2D) g;
            if (isSpecialPath) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.BLACK);
            }

            g2d.draw(new CubicCurve2D.Double(arrowStartX, arrowStartY, ctrlX1, ctrlY1, ctrlX2, ctrlY2, arrowEndX, arrowEndY));

            // 计算箭头的两个分支
            angle = Math.atan2(arrowEndY - ctrlY2, arrowEndX - ctrlX2);
            int arrowLength = 10;
            int x3 = (int) (arrowEndX - arrowLength * Math.cos(angle - Math.PI / 6));
            int y3 = (int) (arrowEndY - arrowLength * Math.sin(angle - Math.PI / 6));
            int x4 = (int) (arrowEndX - arrowLength * Math.cos(angle + Math.PI / 6));
            int y4 = (int) (arrowEndY - arrowLength * Math.sin(angle + Math.PI / 6));

            g2d.drawLine(arrowEndX, arrowEndY, x3, y3);
            g2d.drawLine(arrowEndX, arrowEndY, x4, y4);
        }

        /**
         * 计算图中节点的位置
         *
         * @param xOffset 水平方向的起始偏移量
         * @param yOffset 垂直方向的起始偏移量
         * @param xStep 水平方向上相邻节点之间的间隔
         * @param yStep 垂直方向上同一层次相邻节点之间的间隔
         * @return 一个映射，包含节点名称和其对应的坐标位置
         */
        private Map<String, Point> calculateNodePositions(int xOffset, int yOffset, int xStep, int yStep) {
            Map<String, Point> positions = new HashMap<>(); // 存储节点名称和其对应的坐标位置
            Map<String, Integer> levels = new HashMap<>();  // 存储节点名称和其所在的层次
            Set<String> visited = new HashSet<>();          // 存储已访问过的节点

            // 广度优先搜索
            Queue<String> queue = new LinkedList<>();       // 广度优先队列
            System.out.println("root:" + root);
            queue.add(root);        // 将根节点加入队列
            levels.put(root, 0);    // 根节点层次为0

            System.out.println(graph);
            // 开始搜索
            while (!queue.isEmpty()) {
                String vertex = queue.poll();
                int level = levels.get(vertex);
                visited.add(vertex);

                // 遍历所有邻居
                if (graph.get(vertex) != null) {
                    for (String neighbor : graph.get(vertex).keySet()) {
                        if (neighbor != null && !visited.contains(neighbor)) {
                            queue.add(neighbor);
                            levels.put(neighbor, level + 1);
                            visited.add(neighbor);
                        }
                    }
                }
            }

            // 分配节点位置
            Map<Integer, Integer> levelWidths = new HashMap<>();    // 每层节点数量
            for (String vertex : levels.keySet()) {
                int level = levels.get(vertex);
                levelWidths.put(level, levelWidths.getOrDefault(level, 0) + 1);
            }

            Map<Integer, Integer> levelCounters = new HashMap<>();
            System.out.println("levels:");
            System.out.println(levels);
            // 分配
            for (String vertex : levels.keySet()) {
                int level = levels.get(vertex);
                int index = levelCounters.getOrDefault(level, 0);
                int x = xOffset + level * xStep;
                int y = yOffset + index * yStep;
                positions.put(vertex, new Point(x, y));
                levelCounters.put(level, index + 1);        // 更新节点位置
            }

            return positions;
        }
    }

    // 保存图片
    public void saveGraphAsImage(File file) {
        Dimension size = getViewport().getView().getSize();
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        getViewport().getView().paint(g2d);
        g2d.dispose();
        try {
            ImageIO.write(image, "PNG", file);
            System.out.println("Graph saved as " + file.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}