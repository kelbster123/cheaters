package assignment7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by varun on 4/26/2018.
 */
public class cheaters {


    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Wrong number of input variables");
            System.exit(0);
        }

        final File directory = new File(args[0]);
        int n = Integer.parseInt(args[1]);

        LinkedHashMap<String, LinkedList<Integer>> similarities = new LinkedHashMap<>(1500000, .75f, false);

        HashMap<Integer, String> filesToName = new HashMap<>(1500, .75f);

        int numberOfFiles = 0;


        for(File f : directory.listFiles()) {
            String name = f.getName();

            filesToName.put(numberOfFiles, name);
            Scanner s;

            try{
                s = new Scanner(f);

                Queue<String> buffer = new LinkedList<String>();

                while(s.hasNext() && buffer.size() < n) {
                    String inputWord = s.next();
                    String formattedWord = makeCaps(inputWord);
                    buffer.add(formattedWord);
                }

                while(buffer.size() == n) {
                    String phrase = "";
                    for(String word : buffer) {
                        phrase += word;
                    }


                    if(similarities.containsKey(phrase)) {
                        similarities.get(phrase).add(numberOfFiles);
                    }else{
                        LinkedList<Integer> newPhrase = new LinkedList<Integer>();
                        newPhrase.add(numberOfFiles);
                        similarities.put(phrase, newPhrase);
                    }

                    buffer.remove();

                    if(s.hasNext()) {
                        String nextWord = s.next();
                        nextWord = makeCaps(nextWord);
                        buffer.add(nextWord);
                    }

                }
                //delete this
                numberOfFiles++;
            }
            catch (FileNotFoundException e) {
                System.out.println("File not found");
            }



        }

    }


    public static String makeCaps(String input) {
        String result = "";
        char[] str = input.toCharArray();
        for(int idx = 0; idx < str.length; idx++) {
            char c = str[idx];

            if((c >= 'A') && (c <= 'Z')) {
                result += c;
            } else if((c >= 'a') && (c <= 'z')) {
                result += 'A' + c - 'a';
            }
        }
        return result;
    }

}
