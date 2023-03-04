import java.io.*;
import java.net.URL;
import java.util.*;

public class HuffmanMain implements Huffman {
    private Queue<HuffmanNode> queue;
    private Map<Integer, String> huffCodeMap; //byte to huffman code
    private Map<Integer, Integer> frequency; //byte to frequency
    private int totalOutputBits = 0;
    HuffmanMain() {
        queue = new PriorityQueue<>();
        huffCodeMap = new HashMap<>();
        frequency = new HashMap<>();
    }

    private void scanFrequency(String inputFile) {
        File file = new File(inputFile);
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
            int s = reader.read();
            while (s != -1) {
                if (frequency.containsKey(s))
                    frequency.put(s, frequency.get(s) + 1); //If already contains, add one to freq
                else {
                    frequency.put(s, 1); //Add key(0-255 decimal representation) to hashmap if not seen, freq =1
                }
                s = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFrequency(String freqFileName) {
        try {
            FileWriter writer = new FileWriter(freqFileName);
            for (Integer key : frequency.keySet()) { //Integer.toBinaryString() drops leading zeros
                writer.write(String.format("%8s", Integer.toBinaryString(key)).replace(' ', '0')+":" +frequency.get(key)+ "\n");
                //Converting 0-255 binary representation to binary, leading zero was dropped, we add them back
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void parseFrequencyFile(String frequencyFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(frequencyFile));
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


    private void enQueue() {
        for (Integer key : frequency.keySet()) {
            queue.offer(new HuffmanNode(key, frequency.get(key)));
        }
    }


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
            queue.offer(root); //Put root node back for next iteration comparsion
        }
        return root;
    }

    private void huffmanCodeToMap(HuffmanNode node, String s) {
        if (node == null)
            return;

        if (node.left == null && node.right == null)
            huffCodeMap.put(node.c, s); //If reached leaf node, we made a huffman code, add it to hashmap

        huffmanCodeToMap(node.left, s + "0");
        huffmanCodeToMap(node.right, s + "1");
    }


    public void encode(String inputFile, String outputFile, String freqFile) {
        BinaryOut encodeStream = new BinaryOut(outputFile);
        scanFrequency(inputFile);
        enQueue(); //enqueue element of frequency hashmap to pqueue
        writeToFrequency(freqFile);
        HuffmanNode root = huffmanTree(); //build huffmantree with queue. Return root.
        huffmanCodeToMap(root, "");

        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
            int s = reader.read();
            while (s != -1) {
                String [] arr = huffCodeMap.get(s).split("");
                for(String i:arr){
                    encodeStream.write(i.equals("1"));
                    totalOutputBits ++;
                }
                s = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodeStream.flush();
        queue.clear();
        System.out.println("File Encoded as " + outputFile);
    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        parseFrequencyFile(freqFile);
        enQueue();
        HuffmanNode root = huffmanTree();
        BinaryIn stream = new BinaryIn("alice30.txt");

        try {
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
            HuffmanNode current = root;
            while (!stream.isEmpty()) {
                boolean b = stream.readBoolean();
                if (!b) {
                    current = current.left;
                } else {
                    current = current.right;
                }
                if (current.left == null && current.right == null) {
                    System.out.println(current.c);
                    writer.write(current.c);
                    current = root;
                }
            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        queue.clear();
        System.out.println("File Decoded ");
    }

    public static void main(String[] args) {
        /*
        args[0] method of encode or decode
        args[1] encode input file name/ decode input file name
        args[2] encode output file name/ decode output file name
        args[3] pwd current directory
        */
        HuffmanMain huffman = new HuffmanMain();
        String pwd = args[args.length-1]+"/";

        /*
        Manipulating output file name
        If args.length is 2, only two parameters are provided, therefore no output file name
        is specified, we parse the args[1], the default input file

        If args.length is 3, a specific output file name is requested, we parse args[2]
        */

        String parsedFile = args[1];
        if(args[0].equals("encode")){
            if(args.length == 3 && args[1].contains(".")){ //If already have file definition
                parsedFile = args[1].substring(0, args[1].lastIndexOf('.'));
            }

            else if(args.length ==4){ //If length = 3 we will parse the second argument
                parsedFile = args[2];
                if (parsedFile.contains(".")){
                    parsedFile = parsedFile.substring(0, parsedFile.lastIndexOf('.'));
                }
            }
            String frequencyFileName = "."+ parsedFile + "_freq.txt"; //Add frequency to the parsed file to keep track of names
            String encodeFileName = parsedFile + ".enc"; //Add enc to all files
            System.out.println(pwd+args[1]);
            huffman.encode(pwd+args[1], pwd+encodeFileName, pwd+frequencyFileName);
        }

        else{ //decode
            parsedFile = args[1].substring(0, args[1].lastIndexOf('.'));
            String frequencyFileName = "."+parsedFile + "_freq.txt";
            if(args.length ==4){
                huffman.decode(pwd+args[1], pwd+args[2],pwd+frequencyFileName);
            }
            else{
                huffman.decode(pwd+args[1],pwd+parsedFile,pwd+frequencyFileName);
            }
        }

    }

}
