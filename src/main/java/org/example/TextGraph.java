package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TextGraph {
    public Map<String, Map<String, Integer>> graph = new HashMap<>();
    public Collection<Map<String, Integer>> graphValues;
    public String root = null;
    private Random random = new Random();

    public boolean toStop = false;

    public boolean processFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            String previousWord = null;
            boolean flag = true;        // 第一次获取previousWord的flag
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
                String[] words = line.split("\\s+");
                String word = null;
                int i = 0;
                if(flag){
                    previousWord = words[0];    // 从第一个开始
                    root = previousWord;        // 记录根节点
                    flag = false;
                    i = 1;
                }

                for (; i < words.length; i++) {
                    word = words[i];
                    System.out.println(word);
                    if (word.isEmpty()) {
                        continue;
                    }
                    if (previousWord != null) {
                        System.out.println(word);
                        addEdge(previousWord, word);
                    }
                    previousWord = word;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println(graph);
        return true;
    }

    private void addEdge(String from, String to) {
        System.out.println("add edge from " + from + " to " + to );
        graph.putIfAbsent(from, new HashMap<>());
        graph.putIfAbsent(to, new HashMap<>());
        Map<String, Integer> edges = graph.get(from);
        edges.put(to, edges.getOrDefault(to, 0) + 1);
    }

    public void showDirectedGraph() {
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String from = entry.getKey();
            Map<String, Integer> edges = entry.getValue();
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                String to = edge.getKey();
                int weight = edge.getValue();
                System.out.println(from + " -> " + to + " [weight=" + weight + "]");
            }
        }
    }

    public Set<String> queryBridgeWords(String word1, String word2) {


        Set<String> bridgeWords = new HashSet<>();
        Map<String, Integer> edgesFromWord1 = graph.get(word1);

        for (String potentialBridge : edgesFromWord1.keySet()) {
            Map<String, Integer> edgesFromBridge = graph.get(potentialBridge);
            if (edgesFromBridge != null && edgesFromBridge.containsKey(word2)) {
                bridgeWords.add(potentialBridge);
            }
        }

        return bridgeWords;
    }

    public String generateNewText(String inputText) {
        String newText = inputText.replaceAll("[^a-zA-Z ]", " ");
        String[] words = newText.split("\\s+");

        StringBuilder processedText = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            processedText.append(words[i]);
            String word1 = words[i];
            String word2 = words[i + 1];

            if (graph.containsKey(word1)) {
                Set<String> bridgeWords = new HashSet<>();
                Map<String, Integer> edgesFromWord1 = graph.get(word1);

                for (String potentialBridge : edgesFromWord1.keySet()) {
                    Map<String, Integer> edgesFromBridge = graph.get(potentialBridge);
                    if (edgesFromBridge != null && edgesFromBridge.containsKey(word2)) {
                        bridgeWords.add(potentialBridge);
                    }
                }

                if (!bridgeWords.isEmpty()) {
                    String[] bridges = bridgeWords.toArray(new String[0]);
                    String bridgeWord = bridges[random.nextInt(bridges.length)];
                    processedText.append(" ").append(bridgeWord);
                }
            }

            processedText.append(" ");
        }

        if (words.length > 0) {
            processedText.append(words[words.length - 1]);
        }

        return processedText.toString();
    }

    public List<List<String>> calcShortestPath(String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, List<List<String>>> paths = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.keySet()) {
            if (node.equals(start)) {
                distances.put(node, 0);
                paths.put(node, new ArrayList<>());
                paths.get(node).add(new ArrayList<>(Collections.singletonList(node)));
            } else {
                distances.put(node, Integer.MAX_VALUE);
            }
            nodes.add(node);
        }

        List<List<String>> allPaths = new ArrayList<>();

        while (!nodes.isEmpty()) {
            String closest = nodes.poll();
            if (closest.equals(end)) {
                allPaths.addAll(paths.get(closest));
                break;
            }

            if (distances.get(closest) == Integer.MAX_VALUE) {
                break;
            }

            Map<String, Integer> neighbors = graph.get(closest);
            if (neighbors != null) {
                for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                    int alt = distances.get(closest) + neighbor.getValue();
                    if (alt < distances.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                        distances.put(neighbor.getKey(), alt);
                        previousNodes.put(neighbor.getKey(), closest);
                        nodes.remove(neighbor.getKey()); // Important: Remove and re-add to update priority
                        nodes.add(neighbor.getKey());
                        paths.putIfAbsent(neighbor.getKey(), new ArrayList<>());
                        for (List<String> path : paths.get(closest)) {
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor.getKey());
                            paths.get(neighbor.getKey()).add(newPath);
                        }
                    } else if (alt == distances.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                        paths.putIfAbsent(neighbor.getKey(), new ArrayList<>());
                        for (List<String> path : paths.get(closest)) {
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor.getKey());
                            paths.get(neighbor.getKey()).add(newPath);
                        }
                    }
                }
            }
        }
        return allPaths;
    }



    public int calculatePathLength(List<String> path) {
        int length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Map<String, Integer> neighbors = graph.get(path.get(i));
            if (neighbors != null) {
                Integer weight = neighbors.get(path.get(i + 1));
                if (weight != null) {
                    length += weight;
                }
            }
        }
        return length;
    }
    private String getRandomNode() {
        Random random = new Random();
        List<String> nodes = new ArrayList<>(graph.keySet());
        return nodes.get(random.nextInt(nodes.size()));
    }
    public void randomWalk() {
        List<String> traversedNodes = new ArrayList<>();
        List<String> traversedEdges = new ArrayList<>();
        String currentNode = getRandomNode();
        traversedNodes.add(currentNode);

        toStop = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("traversal_output.txt"))) {
            writer.write(currentNode + " ");
            System.out.println("当前节点: " + currentNode);
            while (true) {
                Map<String, Integer> edges = graph.get(currentNode);
                if (edges == null || edges.isEmpty()) {
                    writer.write(currentNode + " ");
                    break;
                }

                List<String> possibleNextNodes = new ArrayList<>(edges.keySet());
                String nextNode = possibleNextNodes.get(random.nextInt(possibleNextNodes.size()));
                traversedEdges.add(currentNode + " -> " + nextNode);
                currentNode = nextNode;
                traversedNodes.add(currentNode);

                if (traversedNodes.size() != new HashSet<>(traversedNodes).size()) {
                    writer.write(currentNode + " ");
                    break;
                }

                writer.write(currentNode + " ");

                // 检查用户是否想要停止遍历
                System.out.println("当前节点: " + currentNode);
                System.out.println("路径: " + String.join(" -> ", traversedNodes));
                System.out.println("Enter 'stop' to stop traversal, or press Enter to continue:");
                if (toStop){
                    writer.write("\n"+"Traversal stopped by user.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Traversal completed. Nodes traversed: " + traversedNodes);
        System.out.println("Edges traversed: " + traversedEdges);
    }

    public void randomWalk_stop() {
        List<String> traversedNodes = new ArrayList<>();
        List<String> traversedEdges = new ArrayList<>();
        String currentNode = getRandomNode();
        traversedNodes.add(currentNode);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("traversal_output.txt"))) {
            writer.write(currentNode + " ");
            System.out.println("当前节点: " + currentNode);
            while (true) {
                Map<String, Integer> edges = graph.get(currentNode);
                if (edges == null || edges.isEmpty()) {
                    writer.write(currentNode + " ");
                    break;
                }

                List<String> possibleNextNodes = new ArrayList<>(edges.keySet());
                String nextNode = possibleNextNodes.get(random.nextInt(possibleNextNodes.size()));
                traversedEdges.add(currentNode + " -> " + nextNode);
                currentNode = nextNode;
                traversedNodes.add(currentNode);

                if (traversedNodes.size() != new HashSet<>(traversedNodes).size()) {
                    writer.write(currentNode + " ");
                    break;
                }

                writer.write(currentNode + " ");

                // ����û��Ƿ���Ҫֹͣ����
                System.out.println("当前节点: " + currentNode);
                System.out.println("路径: " + String.join(" -> ", traversedNodes));
                System.out.println("Enter 'stop' to stop traversal, or press Enter to continue:");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if ("stop".equalsIgnoreCase(input.trim())) {
                    writer.write("\n"+"Traversal stopped by user.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Traversal completed. Nodes traversed: " + traversedNodes);
        System.out.println("Edges traversed: " + traversedEdges);
    }
}