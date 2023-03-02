import java.io.*;
import java.util.*;

public class HuffmanMain implements Huffman {
    private Queue<HuffmanNode> queue;
    private Map<Integer, String> huffCodeMap;
    private Map<Integer, Integer> frequency;

    HuffmanMain() {
        queue = new PriorityQueue<>();
        huffCodeMap = new HashMap<>();
        frequency = new HashMap<>();
    }

    private void scanFrequency(String inputFile) {
        File file = new File(inputFile);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int s = reader.read();
            while (s != -1) {
                if (frequency.containsKey(s))
                    frequency.put(s, frequency.get(s) + 1);
                else {
                    frequency.put(s, 1);
                }
                s = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeToFrequency(String freqFileName) {
        String outpath = outputPath();
        try {
            System.out.println("Generating Frequency File");
            FileWriter writer = new FileWriter(outpath + "/" + freqFileName);
            for (Integer key : frequency.keySet()) {
                writer.write(Integer.toBinaryString(key) + ":" + frequency.get(key) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseFrequencyFile(String frequencyFile) {
        File file = new File(frequencyFile);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] arr = line.split(":");
                int decimal = Integer.parseInt(arr[0], 2);
                frequency.put(decimal, Integer.parseInt(arr[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void enQueue() {
        for (Integer key : frequency.keySet()) {
            queue.offer(new HuffmanNode(key, frequency.get(key)));
        }
    }

    //Nested HuffmanNode Class
    private class HuffmanNode implements Comparable<HuffmanNode> {
        int c;
        int freq;
        HuffmanNode left;
        HuffmanNode right;

        HuffmanNode(Integer c, int freq) {
            this.c = c;
            this.freq = freq;
        }

        @Override
        public int compareTo(HuffmanNode o) {
            return freq - o.freq;
        }
    }

    private HuffmanNode huffmanTree() {
        HuffmanNode root = null;
        while (queue.size() != 1) {
            HuffmanNode minNode = queue.poll();
            HuffmanNode secondMinNode = queue.poll();
            HuffmanNode totalFrequency = new HuffmanNode(-1, minNode.freq + secondMinNode.freq);
            totalFrequency.left = minNode;
            totalFrequency.right = secondMinNode;
            root = totalFrequency;
            queue.offer(root);
        }
        return root;
    }

    private void huffmanCodeToMap(HuffmanNode node, String s) {
        if (node == null)
            return;

        if (node.left == null && node.right == null)
            huffCodeMap.put(node.c, s);

        huffmanCodeToMap(node.left, s + "0");
        huffmanCodeToMap(node.right, s + "1");
    }


    public void encode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();
        BinaryOut encodeStream = new BinaryOut(outpath + "/" + outputFile);
        System.out.println("Output now writing");
        scanFrequency(inputFile);
        enQueue();
        writeToFrequency(freqFile);
        HuffmanNode root = huffmanTree();
        huffmanCodeToMap(root, "");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            int s = reader.read();
            while (s != -1) {
                String [] arr = huffCodeMap.get(s).split("");
                for(String i:arr){
                    encodeStream.write(i.equals("1"));
                }
                s = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodeStream.flush();
        queue.clear();
    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        String outputpath = outputPath();
        parseFrequencyFile(freqFile);
        enQueue();
        HuffmanNode root = huffmanTree();
        BinaryIn stream = new BinaryIn(inputFile);

        try {
            FileWriter writer = new FileWriter(outputpath + "/" + outputFile);
            HuffmanNode current = root;
            while (!stream.isEmpty()) {
                boolean b = stream.readBoolean();
                if (!b) {
                    current = current.left;
                } else {
                    current =current.right;
                }
                if (current.left == null && current.right ==null){
                    writer.write((char)current.c);
                    current = root;
                }
            }
           writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        queue.clear();
        }

    private String outputPath() {
        try {
            Process currDir = Runtime.getRuntime().exec("pwd");
            BufferedReader reader = new BufferedReader(new InputStreamReader(currDir.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Failed to find path to current directory. Output file directed to home");
        }
        return System.getProperty("user.home");
    }

    public static void main(String[] args) {
        HuffmanMain huffman = new HuffmanMain();
        huffman.encode("src/alice30.txt", "encode.enc","frequency.txt" );
        huffman.decode("encode.enc", "decode.txt", "frequency.txt");
    }

}
