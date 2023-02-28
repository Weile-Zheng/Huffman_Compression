// Import any package as required

import java.io.*;
import java.util.*;

public class HuffmanSubmit implements Huffman {
    Queue<HuffmanNode> queue;

    HuffmanSubmit(){
        queue = new PriorityQueue<>();
    }

    //Scan frequency of character in a file. Store as key value pair in hashmap.
    private static Map scanFrequency(String inputFile) {
        Map<Integer, Integer> frequency = new HashMap<>();
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


        return frequency;
    }

    //Given an inputfile name and outputfile name(freqfile), scanFrequency() inputfile and write to output
    private void writeToFrequency(String inputFileName, String freqFileName) {
        Map<Integer, Integer> frequency = scanFrequency(inputFileName);
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

    //Enqueue element from a map into the instance variable queue of this huffmansubmit class
    private void enQueue(Map<Integer, Integer> frequency){
        for (Integer key : frequency.keySet()) {
            queue.offer(new HuffmanNode(key, frequency.get(key)));
        }
    }

    //Nested HuffmanNode Class
    private class HuffmanNode implements Comparable<HuffmanNode> {
        int c; //unicode representation of the character
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

    //Build huffman tree with the priority queue instance variable.
    //Return a HuffmanNode to the root of the tree.
    private HuffmanNode huffmanTree(){
        HuffmanNode root = null;
        while(queue.size()!=1){
            HuffmanNode minNode = queue.poll();
            HuffmanNode secondMinNode = queue.poll();
            //Dummy node only hold total frequency from the two child nodes
            HuffmanNode totalFrequency = new HuffmanNode(-1, minNode.freq+ secondMinNode.freq);
            totalFrequency.left = minNode;
            totalFrequency.right = secondMinNode;
            root = totalFrequency;
            queue.offer(root);
        }
        return root;
    }

    void printCode(HuffmanNode node, String s){
        if(node == null){
            return;
        }
        if(node.left == null && node.right == null){
            System.out.println(node.c +" " + s);
        }

        printCode(node.left, s+"0");
        printCode(node.right , s+"1");
    }

    public void encode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();
        BinaryOut encodeStream = new BinaryOut(outpath + "/" + outputFile);
        System.out.println("Output now writing");
        enQueue(scanFrequency(inputFile));

        encodeStream.flush();

    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();

    }

    private String outputPath() { //We are not dealing with the exception in this method
        try {
            Process currDir = Runtime.getRuntime().exec("pwd");
            BufferedReader reader = new BufferedReader(new InputStreamReader(currDir.getInputStream()));
            String line = reader.readLine();
            return line;
        } catch (IOException e) {
            System.out.println("Failed to find path to current directory. Output file directed to home");
        }
        String home = System.getProperty("user.home");
        return home;
    }

    public static void main(String[] args) {
        HuffmanSubmit huffman = new HuffmanSubmit();
        //huffman.encode("", "test.txt", "");
        //huffman.writeToFrequency("./src/alice30.txt", "frequency.txt");
        huffman.queue = new PriorityQueue<>();
        huffman.enQueue(scanFrequency("test.txt"));
        huffman.printCode(huffman.huffmanTree(),"" );


        //huffman.encode("ur.jpg", "ur.enc", "freq.txt");
        //huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
        // After decoding, both ur.jpg and ur_dec.jpg should be the same.
        // On linux and mac, you can use `diff' command to check if they are the same.


    }

}
