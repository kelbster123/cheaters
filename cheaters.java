package assignment7;
/*
 * EE422C Project 7 submission by
 * Varun Prabhu
 * vp6793
 * 15465
 * Kelby Erickson
 * kde528
 * 15495
 * Spring 2018
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class cheaters {
	
	public static class CollisionObject { // object containing two files' names and the number of identical phrases between them
		int numCollisions;
		String file1;
		String file2;
		
		public CollisionObject(int numCol, String f1, String f2) {
			numCollisions = numCol;
			file1 = f1;
			file2 = f2;
		}
	}
	
	public static class CollisionComparator implements Comparator<CollisionObject> { // used to sort CollisionObjects from highest number of collisions to least number of collisions
		public int compare(CollisionObject c1, CollisionObject c2) {
			if (c1.numCollisions > c2.numCollisions) {
				return -1;
			} else if (c1.numCollisions < c2.numCollisions) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
    public static void main(String[] args) {
    	long time = System.nanoTime();
    	
    	//this is when the user input is not correct and the program will not work, so it exits
        if (args.length != 2 && args.length != 3) {
            System.out.println("Wrong number of input variables");
            System.exit(0);
        }

        //this is the path to all of the text files to check
        final File directory = new File(args[0]);
        
        //this is the number of words in each phrase to check
        int n = Integer.parseInt(args[1]);
        int limit = 0;
        
        //optional third user input which specifies the lower limit of the similarities needed for files to be considered similar
        //and shown on screen. This is defaulted to 0 (at least 1 similarity) if there is no user input.
        if (args.length == 3) {
        	limit = Integer.parseInt(args[2]);
        }
        
        
        
        //This is where we will store the similarities. Each 6 word phrase is mapped to a linked list of integers representing the 
        //file in which this phrase was found. 
        LinkedHashMap<String, LinkedList<Integer>> similarities = new LinkedHashMap<>(1500000, .75f, false);
        
        //This hashmap maps the file number to the string representing its name, integers are used in the similarities map for 
        //efficiency purposes and we use this to get the actual file name of each file.
        HashMap<Integer, String> filesToName = new HashMap<>(1500, .75f);
        
        //this value is the number that we assign to each file and is incremented each time we finish processing a file
        int numberOfFiles = 0;

        for (final File f : directory.listFiles()) {
            String name = f.getName();

            //stores the file number to name in the names hashmap
            filesToName.put(numberOfFiles, name);
            Scanner s;

            try {
                s = new Scanner(f);
                
                
                //this queue is used to store the n length phrases that we are processing
                Queue<String> buffer = new LinkedList<String>();
              
                //this fills up the buffer for the first time before we begin processing the data
                while (s.hasNext() && buffer.size() < n) {
                    String inputWord = s.next();
                    
                    //gets rid of punctuation and makes the word all caps before entering into buffer
                    String formattedWord = makeCaps(inputWord);
                    buffer.add(formattedWord);
                }

                //this loop will execute once for every n word phrase in the current file
                while (buffer.size() == n) {
                    String phrase = "";
                    //this creates the n word phrase that we will be hashing
                    for(String word : buffer) {
                        phrase += word;
                    }
                    
                    if (similarities.containsKey(phrase)) {//if this phrase has been seen before, add the file number to the linked list
                        similarities.get(phrase).add(numberOfFiles);
                    } else {//if this is the first time that this phrase has been seen, create a new linked list and add it to the map
                        LinkedList<Integer> newPhrase = new LinkedList<Integer>();
                        newPhrase.add(numberOfFiles);
                        similarities.put(phrase, newPhrase);
                    }

                    //removes the first word of the phrase and adds the next word in file to buffer to create the next n word phrase if possible
                    buffer.remove();
                    if (s.hasNext()) {
                        String nextWord = s.next();
                        nextWord = makeCaps(nextWord);
                        buffer.add(nextWord);
                    }
                }
                
                numberOfFiles++;
            }
            catch (FileNotFoundException e) {
                System.out.println("File not found");
            }
        }
        
        
        
        
        // Determine number of collisions (phrases in common) between any two files
        
        int[][] collisions = new int[numberOfFiles][numberOfFiles]; // collisions[x][y] is the number of phrases that files x and y share
        
        for (String key : similarities.keySet()) { // loop through each list of files with identical phrases
        	LinkedList<Integer> list = similarities.get(key); // list of files with a particular phrase in common
        	while (list.size() > 1) {
        		int firstElem = list.remove(); // next file at front of list
        		Iterator<Integer> it = list.iterator(); // iterator through rest of list
        		HashSet<Integer> alreadyChecked = new HashSet<>(); // if alreadyChecked.contains(x), then x has already been noted as having the particular phrase in common with the file at the front of the list
        		while (it.hasNext()) {
        			int otherElem = it.next(); // some file that has the particular phrase
        			if (otherElem != firstElem && !alreadyChecked.contains(otherElem)) {
        				alreadyChecked.add(otherElem);
        				int minElem; // min of the file at the front of the list and the other file
        				int maxElem; // max of the file at the front of the list and the other file
        				if (firstElem < otherElem) {
        					minElem = firstElem;
        					maxElem = otherElem;
        				} else {
        					minElem = otherElem;
        					maxElem = firstElem;
        				}
        				collisions[minElem][maxElem]++; // if it is found that two files have a phrase in common, increment the count for that pair
        			}
        		}
        	}
        }
        
        
        
        
        // Heapsort pairs of files according to the number of collisions between the two files in a pair
        // Print list of collision calculations to the console
        
        Queue<CollisionObject> heap = new PriorityQueue<>(numberOfFiles*numberOfFiles, new CollisionComparator()); // heap to sort pairs of files
        
        //This will be used to keep track of the groups of suspicious documents
        ArrayList<Set<String>> suspicious = new ArrayList<Set<String>>();
        
        for (int row = 0; row < numberOfFiles; row++) {
        	for (int col = row + 1; col < numberOfFiles; col++) {
        		if (collisions[row][col] > limit) {
        			heap.add(new CollisionObject(collisions[row][col], filesToName.get(row), filesToName.get(col)));
        		}
        	}
        }
        
        while (!heap.isEmpty()) {
        	CollisionObject colObj = heap.remove(); // pair of files with next highest number of collisions
        	
        	//artificially set the lower found on suspicion at 200 matching n word phrases, but this can be adjusted
        	if(colObj.numCollisions > 200) {
        		boolean found = false;
        		Set<String> first = null;
        		Set<String> second = null;
        		
        		//checks to see if either file in the pair of similar files has already been seen in a group
        		for(Set<String> s : suspicious) {
        			
        			//if a file is in a group, note the set in which it appears and add both files to the set.
        			if(s.contains(colObj.file1)) {
        				first = s;
        				first.add(colObj.file1);
        				first.add(colObj.file2);
        				found = true;
        			}
        			if(s.contains(colObj.file2)) {
        				second = s;
        				second.add(colObj.file1);
        				second.add(colObj.file2);
        				found = true;
        			}
        		}
        		
        		if(found) {
        			//if here, we found that at least one of the files was already in a set
        			//if we only found one, simply adding the other to the set containing the first completes the task (already done above)
        			//if we found both of them, then we must check and see if we found them in the same set
        			//if the two files were found in the same set, everything is fine. Otherwise, we must merge the two sets and remove one from the suspicious groups ArrayList
        			if((first != null) && (second != null)) {
        				if(!(first == second)) {
        					first.addAll(second);
        					suspicious.remove(second);
        				}
        			}
        		} else {
        			//if we did not find either file in the suspicious groups, we create a new group
        			Set<String> newGroup = new HashSet<String>();
        			newGroup.add(colObj.file1);
        			newGroup.add(colObj.file2);
        			suspicious.add(newGroup);
        		}
        		
        		
        	}
        
        	System.out.println(colObj.numCollisions + ":\t" + colObj.file1 + ",\t" + colObj.file2);
        }
        
        System.out.println();
        
        
        System.out.println("The suspicious groups of documents are as follows:");
        int groupNum = 1;
        
        //This chunk of code prints out all of the suspicious groups to the console
        for(Set<String> s : suspicious) {
        	String printOut = "Group " + groupNum + ":\t";
        
        	for(String fileName : s) {
        		printOut += fileName + ", ";
        	}
        	printOut = printOut.substring(0, printOut.length()-2);
        	System.out.println(printOut);
        	groupNum++;
        }
        
     
    }
    
    
    
    
    /**
     * Capitalizes and removes punctuation from input String
     * @param input is the String
     * @return capitalized and punctuation-free String
     */
    private static String makeCaps(String input) {
        String result = ""; // String to return
        char[] str = input.toCharArray(); // transform String to char[] for faster char access
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
