// Import any package as required

import java.io.*;
import java.util.*;

public class HuffmanSubmit implements Huffman {
    Queue<HuffmanNode> queue;

    HuffmanSubmit(){
        queue = new PriorityQueue<>();
    }

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

    private void enQueue(Map<Integer, Integer> frequency){
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

    public void encode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();
        BinaryOut encodeStream = new BinaryOut(outpath + "/" + outputFile);
        System.out.println("Output now writing");
        //encodeStream.write();
        //encodeStream.write(11010110);
        encodeStream.flush();


    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();

    }

    public void test() {
        /*HuffmanNode c= new HuffmanNode('c');
        HuffmanNode a= new HuffmanNode('a');
        HuffmanNode b= new HuffmanNode('b');
        c.addFreq();
        c.addFreq();
        a.addFreq();
        queue = new PriorityQueue<>();
        queue.offer(c);
        queue.offer(a);
        queue.offer(b);
        a.addFreq();
        a.addFreq(); //Modifying comparing values for an element already in queue is ok.
        //Even after adding in, they will be sorted automatically when polled
        while(queue.isEmpty()==false){
            System.out.println(queue.poll().character);}*/

    }

    public static void main(String[] args) {
        HuffmanSubmit huffman = new HuffmanSubmit();
        //huffman.encode("", "test.txt", "");
        //huffman.writeToFrequency("./src/alice30.txt", "frequency.txt");
        huffman.queue = new PriorityQueue<>();
        huffman.enQueue(scanFrequency("./src/alice30.txt"));


        //huffman.encode("ur.jpg", "ur.enc", "freq.txt");
        //huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
        // After decoding, both ur.jpg and ur_dec.jpg should be the same.
        // On linux and mac, you can use `diff' command to check if they are the same.


    }

}
