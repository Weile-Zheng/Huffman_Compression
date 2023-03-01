// Import any package as required

import java.io.*;
import java.util.*;

public class HuffmanSubmit implements Huffman {
    Queue<HuffmanNode> queue;
    Map<Integer, String> huffCodeMap; //Stores unicode char as key and huffman encoding as string value.
    Map<Integer, Integer> frequency;

    HuffmanSubmit() {
        queue = new PriorityQueue<>();
        huffCodeMap = new HashMap<>();
        frequency = new HashMap<>();
    }

    //Scan frequency of character in a file. Store as key value pair in hashmap.
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

    //Given an inputfile name and outputfile name(freqfile), write frequency to output
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

    //Read the frequency file and put characters and frequencies into frequency map
    private void parseFrequencyFile(String frequencyFile) {
        File file = new File(frequencyFile);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] arr = line.split(":");
                int decimal = Integer.parseInt(arr[0], 2); //convert binary to unicode decimal.
                frequency.put(decimal, Integer.parseInt(arr[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Enqueue element from a map into the instance variable queue of this huffmansubmit class
    private void enQueue() {
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
    private HuffmanNode huffmanTree() {
        HuffmanNode root = null;
        while (queue.size() != 1) {
            HuffmanNode minNode = queue.poll();
            HuffmanNode secondMinNode = queue.poll();
            //Dummy node only hold total frequency from the two child nodes
            HuffmanNode totalFrequency = new HuffmanNode(-1, minNode.freq + secondMinNode.freq);
            totalFrequency.left = minNode;
            totalFrequency.right = secondMinNode;
            root = totalFrequency;
            queue.offer(root);
        }
        return root;
    }

    private void huffmanCodeToMap(HuffmanNode node, String s) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            huffCodeMap.put(node.c, s);
        }

        huffmanCodeToMap(node.left, s + "0");
        huffmanCodeToMap(node.right, s + "1");


    }


    public void encode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();
        BinaryOut encodeStream = new BinaryOut(outpath + "/" + outputFile);
        System.out.println("Output now writing");
        scanFrequency(inputFile);
        enQueue(); //enqueue element of frequency hashmap to pqueue
        writeToFrequency(freqFile);
        HuffmanNode root = huffmanTree(); //build huffmantree with queue. Return root.
        huffmanCodeToMap(root, "");
        File file = new File(inputFile);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            int s = reader.read();
            while (s != -1) {
                encodeStream.write(huffCodeMap.get(s));//Get the corresponding huffman code with respect to character s
                encodeStream.flush();
                s = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodeStream.flush();
        queue.clear(); //Since we never poll() all element from queue, we must clear it after use
    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();
        parseFrequencyFile(freqFile); //Parse frequency file into frequency hashmap
        enQueue(); //Put elements of frequency hashmap into pqueue
        HuffmanNode root = huffmanTree(); //build huffmantree with queue. Return root.
        BinaryIn stream = new BinaryIn(inputFile);

        try {
            FileWriter writer = new FileWriter(outpath + "/" + outputFile);
            HuffmanNode current = root;
            //System.out.println(current.left.left.c);
            while (!stream.isEmpty()) {
                int x = Character.getNumericValue(stream.readChar());
                System.out.println(x);
                if (x == 0) {
                    current = current.left;
                    if (current.left == null && current.right ==null){
                        System.out.println(current.c);
                        writer.write((char)current.c);
                        current = root;
                    }
                } else {
                    current =current.right;
                    if (current.left == null && current.right == null) {
                        System.out.println(current.c);
                        writer.write((char)current.c);
                        current =root;
                    }
                }
            }
           writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        queue.clear();
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
        huffman.encode("test.txt", "output.enc","frequency.txt" );
        huffman.decode("output.enc", "decode.txt", "frequency.txt");
        //huffman.writeToFrequency("./src/alice30.txt", "frequency.txt");
        // huffman.huffmanCode(huffman.huffmanTree(),"" );
        //huffman.parseFrequencyFile("frequency.txt");

        System.out.println();

        for (Integer key : huffman.frequency.keySet()) {
            System.out.println(key + " " + huffman.frequency.get(key));
        }

        for (Integer key : huffman.huffCodeMap.keySet()) {
            System.out.println(key + " " + huffman.huffCodeMap.get(key));
        }


        //huffman.encode("ur.jpg", "ur.enc", "freq.txt");
        //huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
        // After decoding, both ur.jpg and ur_dec.jpg should be the same.
        // On linux and mac, you can use `diff' command to check if they are the same.


    }

}
