// Import any package as required

import java.io.*;
import java.util.*;

public class HuffmanSubmit implements Huffman {
    Queue<HuffmanNode> queue;

    private void scanFrequency(String inputFile){
        Map<Character, Integer> frequency = new HashMap<>();
        File file = new File(inputFile);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int s = reader.read();
            while(s != -1){
                char c = (char) s;
                if(frequency.containsKey(c))
                    frequency.put(c,frequency.get(c)+1);
                else{
                    frequency.put(c,1);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        for (Character key: frequency.keySet()) {
            System.out.println(key + " " + frequency.get(key));
        }

    }
    private class HuffmanNode implements Comparable<HuffmanNode>{
        char character;
        int freq;
        HuffmanNode(char c){
            character=c;
            freq ++;
        }

        void addFreq(){
            freq++;
        }
        @Override
        public int compareTo(HuffmanNode o) {
            return freq-o.freq;
        }
    }


    //private buildFreqFile(){

    //}
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
        BinaryOut encodeStream = new BinaryOut(outpath+"/"+outputFile);
        System.out.println("Output now writing");
        //encodeStream.write();
        //encodeStream.write(11010110);
        encodeStream.flush();


    }


    public void decode(String inputFile, String outputFile, String freqFile) {
        String outpath = outputPath();

    }

    public void test(){
        HuffmanNode c= new HuffmanNode('c');
        HuffmanNode a= new HuffmanNode('a');
        HuffmanNode b= new HuffmanNode('b');
        c.addFreq();
        c.addFreq();
        a.addFreq();
        queue = new PriorityQueue<>();
        queue.offer(c);
        queue.offer(a);
        queue.offer(b);
        while(queue.isEmpty()==false){
            System.out.println(queue.poll().character);
        }


    }


    public static void main(String[] args) {
        HuffmanSubmit huffman = new HuffmanSubmit();
        //huffman.encode("", "test.txt", "");
        huffman.scanFrequency("test.txt");



        //huffman.encode("ur.jpg", "ur.enc", "freq.txt");
        //huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
        // After decoding, both ur.jpg and ur_dec.jpg should be the same.
        // On linux and mac, you can use `diff' command to check if they are the same.


    }

}
