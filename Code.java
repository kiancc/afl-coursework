import java.nio.file.Path;
import java.util.ArrayList;

public class Code
{
    public static void main(String[] args)
    {
        // DEMO code, using automaton of Figure 1
        FSA A0=generateFSA0();

        // Check and print the automaton A0
        checkPrintFSA(A0,"A0");

        // Check if A0 accepts some words
        Word w = new Word(new String[]{"0","0","1"});
        System.out.println("accepts "+w+": "+A0.isAccepted(w));
        w = new Word(new String[]{"1","1","2"});
        System.out.println("accepts "+w+": "+A0.isAccepted(w));
        w = new Word(new String[]{"2","0","1"});
        System.out.println("accepts "+w+": "+A0.isAccepted(w));
        w = new Word(new String[]{});
        System.out.println("accepts "+w+": "+A0.isAccepted(w));

        // Check if A0 is least-1 -- the method is not implemented
        System.out.println("A0 Has Loop on '011': "+detectLoop(A0, new Word(new String[]{"0", "1", "1"}))+", Desired output: true");
        // Check if A0 is most-1 -- the method is not implemented
        System.out.println("A0 Pumps twice on '011': "+Pump(A0, new Word(new String[]{"0", "1", "1"}), 2)+", Desired output: 0111");


        // ---------
        // MAIN CODE
        // ---------
        // The code below tests the methods that you need to implement and prints
        // out related messages. Do not change this; instead, change the bodies
        // of methods generateFSA1, etc. as specified in the coursework questions.

        FSA[] As = new FSA[]{generateFSA1(),generateFSA2()};
        String[] names = new String[]{"A1","A2"};

        // Questions 13-14
        System.out.println("\nPrintout (Q13-14)");
        checkPrintFSAs(As,names);

        // Question 15
        System.out.println("\nPrintout (Q15)");
        runFSAs(As,names);

        // Question 15 (Detect Loop)
        System.out.println("\nPrintout (Q16)");
        testLoop();

        // Question 16 (Pump)
        System.out.println("\nPrintout (Q17)");
        testPump();
    }

    // Demo FSA, do not change this
    public static FSA generateFSA0() {
        String[] alphabet = new String[]{ "0", "1", "2" };
        Transition[] delta = new Transition[] {
                new Transition(0,"0",0),
                new Transition(0,"0",1),
                new Transition(0,"2",2),
                new Transition(1,"1",2),
                new Transition(2,"0",2),
                new Transition(2,"1",2)
        };
        int[] finals = new int[] { 2 };
        FSA A = new FSA(3,alphabet,delta,finals);
        return A;
    }

    // -----------------------------------
    // TODO CODE (REQUIRES IMPLEMENTATION)
    // -----------------------------------

    // TODO construct FSA of Question 13 and return it
    public static FSA generateFSA1() {

        int numStates = 6;

        String[] alphabet = new String[] {"0", "1", "2", "3"};

        Transition[] delta = new Transition[] {

            new Transition(0, "0", 0),

            new Transition(1, "1", 0),

            new Transition(1, "2", 2),

            new Transition(2, "3", 3),

            new Transition(3, "2", 2),

            new Transition(3, "1", 1),

            new Transition(4, "1", 3),

            new Transition(4, "1", 2),

            new Transition(4, "1", 5),

            new Transition(5, "2", 2)          

        };

        int[] finalStates = new int[] {3, 2};
        FSA fsa1 = new FSA(numStates, alphabet, delta, finalStates);
        return fsa1; 
    }

    // TODO construct FSA of Question 14 and return it
    public static FSA generateFSA2() {
        int numStates = 4;

        String[] alphabet = new String[] {"1", "2", "3", "5"};

        Transition[] delta = new Transition[] {

            new Transition(0, "3", 0),

            new Transition(0, "5", 1),

            new Transition(0, "1", 1),

            new Transition(1, "1", 0),

            new Transition(1, "1", 2),

            new Transition(1, "1", 3),

            new Transition(1, "2", 3),

            new Transition(2, "2", 2),

            new Transition(3, "3", 1)

        };

        int[] finalStates = new int[] {0, 2, 3};

        FSA fsa2 = new FSA(numStates, alphabet, delta, finalStates);

        return fsa2; 
    }


    // TODO implement this for Question 15
    public static void runFSA(FSA A, String name, Word input) {
        if (A.isAccepted(input)) {
            System.out.println(name + " accepts: " + input + " [yes]");
        }
        else {
            System.out.println(name + " accepts: " + input + " [no]");
        }
    }

    public static Boolean detectLoop(FSA A, Word w) {
        //TODO return true if a loop is encountered while parsing the word w
        // This method assumes that A is deterministic.
        int q = 0;
        String seen = "";
        for (int i = 0; i < w.length; i++) {
            for (Transition t : A.delta) {
                if (t.from == q && t.label.equals(w.get(i))) {
                    // make next start state the final state we just transitioned to
                    q = t.to;
                    // add states we have seen to a String
                    seen += t.from;

                    if (i == w.length - 1) {
                        seen += t.to;
                    }
                    break;
                }
            }            
        }

        String unique = "";
        
        for (int i = 0; i < seen.length(); i++) {
            if (unique.indexOf(seen.charAt(i)) == -1) {
                unique += seen.charAt(i);
            }
        }

        if (seen.length() > unique.length()) {
            return true;
        }

        return false;
    }

    public static Word Pump(FSA A, Word w, int j) {
        //TODO return "No Pump" if no loop is encountered while parsing the word w OR w is not accepted.
        // if w is accepted and there is a loop, return the same word with the loop pumped j times.
        // This method assumes that A is deterministic.
        // You can return the result of the function "badResult()" if there is no pump
        // to ensure test cases are accurate.
        int q = 0; // initial state
        String res = "";
        ArrayList<Integer> visited = new ArrayList<Integer>();
        visited.add(0); // adds first state as we always start from q0
        ArrayList<String> label = new ArrayList<String>();
        System.out.println("Word: " + w + " j: "+ j + " --------------------");
        if (!A.isAccepted(w) || !detectLoop(A, w)) {
            System.out.println("Word is not accepted!");
            return badResult();
        }
        else {
            for (int i = 0; i < w.length; i++) {
                for (Transition t : A.delta) {
                    if (t.from == q && t.label.equals(w.get(i))) {
                        System.out.println(t); // this prints the states visited
                        // checks if a state we are going to has already been visited, which means there is a loop
                        if (visited.contains(t.to)) {
                            System.out.println("Loop!!");
                            String pumpedString = "";
                            // pump the jam
                            int p_index = visited.indexOf(t.to);
                            visited.add(t.to);
                            label.add(t.label);
                            
                            // add transition labels up until the beginning of the loop
                            for (int k = 0; k < p_index; k++) {
                                res += label.get(k);
                            }

                            // pumps string between start and end of loop
                            while (j > 0) {
                                for (int p = p_index; p < visited.size() - 1; p++) {
                                    pumpedString += label.get(p);
                                }
                                j--;
                            }
                            res += pumpedString;
                            System.out.println(visited);
                            System.out.println(label);
                            System.out.println(pumpedString);
                        }
                        else {
                            visited.add(t.to);
                            label.add(t.label);
                        }
                        
                        // make next state is the final state we just transitioned to
                        q = t.to;
                        // add from state to states visited    
                        break;
                    }
                }            
            }
        }
        
        System.out.println(visited);
        System.out.println(label);

        System.out.println("Res: "+res);

        return new Word(res);
    }


    // --------------------------------
    // DO NOT CHANGE THE REMAINING CODE
    // --------------------------------

    public static Word badResult(){
        return new Word("No Pump!");
    }

    public static void testPump(){
        FSA.testCodePump();
    }

    public static void testLoop(){
        FSA.testDetectLoop();
    }

    // print FSAs A, with given names, after checking they are valid
    public static void checkPrintFSAs(FSA[] As, String[] names) {
        for (int i=0; i<As.length; i++)
            checkPrintFSA(As[i],names[i]);
    }

    // print FSA A, with given name, after checking it is valid
    public static void checkPrintFSA(FSA A, String name) {
        if(A==null) return;
        String s = A.check();
        if(s!="") System.out.println("Error found in "+name+":\n"+s);
        else System.out.println(name+" = "+A);
    }

    // run FSAs, using runFSA method (Q15)
    public static void runFSAs(FSA[] As, String[] names) {
        Word[] inputs = new Word[]{
                new Word(new String[]{}),
                new Word(new String[]{"2"}),
                new Word(new String[]{"3"}),
                new Word(new String[]{"0","2","2"}),
                new Word(new String[]{"0","2","3"}),
                new Word(new String[]{"2","3","1"}),
                new Word(new String[]{"3","2","1","1","2"}),
                new Word(new String[]{"3","5","5","1","3"}),
                new Word(new String[]{"1","2","3","1","2"}),
                new Word(new String[]{"1","1","5","1","2"})
        };
        for(int i=0; i<As.length; i++)
            for(Word input : inputs)
                runFSA(As[i],names[i],input);
    }

}
