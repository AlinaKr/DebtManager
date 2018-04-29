import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DebtCalculator {

	public static void main(String filePath) {
		
		HashMap<String, Double> expenses = new HashMap<String, Double>();
        Set<String> names = new HashSet<String>();
        
        StdIn.setStdInToFile(filePath);
        
        //As long as there is input remaining we..
        while (!(StdIn.isEmpty())) {
            //read each individual line and store it in a String object
            String line = StdIn.readLine();
            //Splits this String around matches of the given regular expression and holds the result in a String array. 
            //The first and third element represent the name(String) second the individual expense(Double)
            String[] data = line.split(";");
               
            try{ 
                //if an expense of this person has already been stored, we add the new expense to the person-key
                if (names.contains(data[0])) {
                    Double toadd = Double.parseDouble(data[2]);
                    Double currentexpenses = expenses.get(data[0]) + toadd;
                    expenses.put(data[0], currentexpenses);
                }
                //Otherwise we add the new name to the set and register it including his/her expense in hashmap expenses
                else {
                    names.add(data[0]);
                    expenses.put(data[0], Double.parseDouble(data[2]));
                }   
            }
               
            //If that fails, catch NumberFormatException
            catch (NumberFormatException e) {
                System.out.println("NumberFormatException");
            }
            
        }
        //Resets output stream to standard output
        StdIn.resetStdIn();
        
        int numberofpersons = names.size();
        Double sum;
        //Creating a new hashmap to store what each person owes individually to another person
        HashMap<String[], Double> debtspersontoperson = new HashMap<String[], Double>();
        
        //Going through all expenses
        for (Map.Entry<String, Double> entry : expenses.entrySet()) {
            sum = entry.getValue();  
            for (String s : names) {
                
                if (s.equals(entry.getKey())) {
                    //do nothing
                }
                //if we have a pair of two different persons
                else {
                    //Calculating the amount Person s owes to the current Person in the hashmap
                    //by splitting the expense of the latter person through the number of contributing persons in total
                    double expensetoadd = sum / numberofpersons;
                    //This debt from one individual person to another is stored by adding an string array
                    String[] twopersons = new String[]{s, entry.getKey()};
                    //to the new hashmap in the format Key:(Personx,Persony) Key: doublevalue
                    debtspersontoperson.put(twopersons, expensetoadd);
                }
            }
        }
        
        //Using the help of an HashSet with twosized String array, so we only access a personpair (e.g. x,y + y,x) once
        Set<String[]> usedpairs = new HashSet<>();
        
        for (String[] keyarray1 : debtspersontoperson.keySet()) {
            for (String[] keyarray2 : debtspersontoperson.keySet()) {
                //Only if we haven't accessed the current stringpair yet and the two string pairs are reverse
                //versions of each other, we can adapt the debts and calculate which person of the pair is left with a debt
                //and which person owes nothing to the other (in most cases, except both are evenly balanced)
                if (!usedpairs.contains(keyarray1) 
                && keyarray1[0].equals(keyarray2[1]) 
                && keyarray1[1].equals(keyarray2[0])) {
                    //Getting the debt from personx to persony, and the debt from persony to personx
                    Double value1 = debtspersontoperson.get(keyarray1);
                    Double value2 = debtspersontoperson.get(keyarray2);
                    //if the 1st value(from the outer loop) is bigger.. Personx is left with a debt that
                    //consitutes itself out of his/her debt to the other person minus what the other person owes him. Persony had no debts left.
                    if (value1 > value2) {
                        value1 = value1 - value2;
                        value2 = 0.0;
                    }
                    //And the other way around. The second value(from the inner loop is bigger) or both double values are even
                    else {
                        value2 = value2 - value1;
                        value1 = 0.0;
                    }
                    //The debts of the personpair (one direction and the other) are updated.
                    debtspersontoperson.put(keyarray1, value1);
                    debtspersontoperson.put(keyarray2, value2);
                    //We also put the person pair (in both orders) in a hashset to not accidentally edit it twice which would falsify results
                    usedpairs.add(keyarray1);
                    usedpairs.add(keyarray2);
                }
            }
        }
        
        //Going through every person to person debt relation
        for (String[] keyarray : debtspersontoperson.keySet()) {
            Double debt = debtspersontoperson.get(keyarray);
                //Only printing an element, when we are dealing with an actual (positive) debt
                if (debt > 0.0) {
                    System.out.println(keyarray[0] + " schuldet " + keyarray[1] + " " + debt + " Euro.");
                }
            
        }
	}

}
