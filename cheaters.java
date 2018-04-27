package assignment7;
/*
 * EE422C Project 4 submission by
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
	
	public static class CollisionObject {
		int numCollisions;
		String file1;
		String file2;
		
		public CollisionObject(int numCol, String f1, String f2) {
			numCollisions = numCol;
			file1 = f1;
			file2 = f2;
		}
	}
	
	public static class CollisionComparator implements Comparator<CollisionObject> {
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
        if (args.length != 2 && args.length != 3) {
            System.out.println("Wrong number of input variables");
            System.exit(0);
        }

        final File directory = new File(args[0]);
        int n = Integer.parseInt(args[1]);
        int limit = 0;
        if (args.length == 3) {
        	limit = Integer.parseInt(args[2]);
        }

        LinkedHashMap<String, LinkedList<Integer>> similarities = new LinkedHashMap<>(1500000, .75f, false);

        HashMap<Integer, String> filesToName = new HashMap<>(1500, .75f);

        int numberOfFiles = 0;

        for (final File f : directory.listFiles()) {
            String name = f.getName();

            filesToName.put(numberOfFiles, name);
            Scanner s;

            try {
                s = new Scanner(f);

                Queue<String> buffer = new LinkedList<String>();

                while (s.hasNext() && buffer.size() < n) {
                    String inputWord = s.next();
                    
                    String formattedWord = makeCaps(inputWord);
                    buffer.add(formattedWord);
                }

                while (buffer.size() == n) {
                    String phrase = "";
                    for(String word : buffer) {
                        phrase += word;
                    }
                    
                    if (similarities.containsKey(phrase)) {
                        similarities.get(phrase).add(numberOfFiles);
                    } else {
                        LinkedList<Integer> newPhrase = new LinkedList<Integer>();
                        newPhrase.add(numberOfFiles);
                        similarities.put(phrase, newPhrase);
                    }

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
        
        int[][] collisions = new int[numberOfFiles][numberOfFiles];
        
        for (String key : similarities.keySet()) {
        	LinkedList<Integer> list = similarities.get(key);
        	while (list.size() > 1) {
        		int firstElem = list.remove();
        		Iterator<Integer> it = list.iterator();
        		HashSet<Integer> alreadyChecked = new HashSet<>();
        		while (it.hasNext()) {
        			int otherElem = it.next();
        			if (otherElem != firstElem && !alreadyChecked.contains(otherElem)) {
        				alreadyChecked.add(otherElem);
        				int minElem;
        				int maxElem;
        				if (firstElem < otherElem) {
        					minElem = firstElem;
        					maxElem = otherElem;
        				} else {
        					minElem = otherElem;
        					maxElem = firstElem;
        				}
        				collisions[minElem][maxElem]++;
        			}
        		}
        	}
        }
        
        Queue<CollisionObject> heap = new PriorityQueue<>(numberOfFiles*numberOfFiles, new CollisionComparator());
        for (int row = 0; row < numberOfFiles; row++) {
        	for (int col = row + 1; col < numberOfFiles; col++) {
        		if (collisions[row][col] > limit) {
        			heap.add(new CollisionObject(collisions[row][col], filesToName.get(row), filesToName.get(col)));
        		}
        	}
        }
        
        while (!heap.isEmpty()) {
        	CollisionObject colObj = heap.remove();
        	System.out.println(colObj.numCollisions + ":\t" + colObj.file1 + ",\t" + colObj.file2);
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
