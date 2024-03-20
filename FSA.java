import java.util.*;

public class FSA
{
    public int numStates;
    public String[] alphabet;
    public Transition[] delta;
    public int[] finalStates;

    public FSA(int n, String[] a, Transition[] d, int[] f)
    {
        numStates = n;
        alphabet = a;
        finalStates = f;
        delta = d;
    }

    // Convert the FSA into a 5-tuple string representation
    public String toString()
    {
        String s = "("+toStringAsSet(alphabet)+", {";
        for(int i=0; i<numStates; i++)
        {
            if(i!=0) s += ", ";
            s += "q"+i;
        }
        s += "}, "+toStringAsSet(delta)+", q0, {";
        for(int i=0; i<finalStates.length; i++)
        {
            if(i!=0) s += (", ");
            s += "q"+finalStates[i];
        }
        return s+"})";
    }


    // check if FSA A accepts word w -- DFS implementation
    public Boolean isAccepted(Word w) {
        return isAcceptedRec(w,0,0);
    }

    private Boolean isAcceptedRec(Word w, int q, int i) {
        if(i==w.length) {
            for(int qF : finalStates)
                if(q==qF) return true;
            return false;
        }
        for(Transition t : delta)
            if(t.from==q && t.label.equals(w.get(i)))
                if(isAcceptedRec(w,t.to,i+1)) return true;
        return false;
    }

    // Perform all kinds of checks on an FSA
    public String check()
    {
        // Check the alphabet is valid
        if(alphabet==null) return "Bad alphabet (null)";
        // Check the number of states is valid
        if(numStates<=0) return "Bad number of states ("+numStates+")";
        // Check that the transition relation is valid
        String checkTR = checkTRelation(delta,"transition relation");
        if (!checkTR.equals("")) return checkTR;
        // Check that final states are valid
        if(finalStates==null) return "Bad (null) final states";
        for(int s : finalStates) {
            String checkS = checkState(s);
            if(!checkS.equals("")) return ("Bad final states element: "+checkS);
        }
        return "";
    }

    private String checkState(int s)
    {
        if (s<0 || s>=this.numStates) return ("incorrect state number ("+s+")");
        return "";
    }

    private String checkTRelation(Transition[] a,String name)
    {
        if(a==null) return ("Bad (null) "+name);
        for(Transition t : a) {
            String checkT = checkTransition(t);
            if (!checkT.equals("")) return ("Bad "+name+" element "+t+": "+checkT);
        }
        return "";
    }

    private String checkLabel(String l)
    {
        for(String s : alphabet) if(s.equals(l)) return "";
        if(!l.equals("")) return ("label not in alphabet ("+l+")"); return "";
    }

    private String checkTransition(Transition t)
    {
        String checkC = checkState(t.from);
        if(!checkC.equals("")) return checkC;
        checkC = checkState(t.to);
        if(!checkC.equals("")) return checkC;
        return checkLabel(t.label);
    }

    // Convert an array that represents a set into a string
    private String toStringAsSet(Object[] x)
    {
        String t = Arrays.toString(x);
        return "{"+t.substring(1,t.length()-1)+"}";
    }

    // Some FSAs for testing
    public static FSA generateDFAs(int i) {
        if (i==0) {
            String[] alphabet = new String[]{ "0", "1", "2" };
            Transition[] delta = new Transition[] {
                    new Transition(0,"0",1),
                    new Transition(1,"1",2),
                    new Transition(2,"2",3),
                    new Transition(3,"1",1),
                    new Transition(1,"0",4)
            };
            int[] finals = new int[] { 4 };
            return new FSA(5,alphabet,delta,finals);
        }
        if (i==1) {
            String[] alphabet = new String[]{ "0", "1"};
            Transition[] delta = new Transition[] {
                    new Transition(0,"0",0),
                    new Transition(0,"1",1)
            };
            int[] finals = new int[] { 1 };
            return new FSA(2,alphabet,delta,finals);
        }
        if (i==2){
            String[] alphabet = new String[]{ "0", "1"};
            Transition[] delta = new Transition[] {
                    new Transition(0,"1",1),
                    new Transition(1,"0",1)
            };
            int[] finals = new int[] { 1 };
            return new FSA(2,alphabet,delta,finals);
        }
        if (i == 3){
            String[] alphabet = new String[]{"a", "b", "c"};
            Transition[] delta = new Transition[]{
                    new Transition(0, "a", 1),
                    new Transition(1, "b", 1),
                    new Transition(1, "c", 0),
            };
            int[] finals = new int[] {0};
            return new FSA(2,alphabet, delta, finals);
        }
        if (i == 4){
            String[] alphabet = new String[]{"a", "b"};
            Transition[] delta = new Transition[]{
                    new Transition(0, "a", 1),
                    new Transition(1, "b", 0),
            };
            int[] finals = new int[] {1};
            return new FSA(2,alphabet, delta, finals);
        }
        if (i == 5){
            String[] alphabet = new String[]{"0"};
            Transition[] delta = new Transition[]{
                    new Transition(0, "0", 0),
            };
            int[] finals = new int[] {0};
            return new FSA(1,alphabet, delta, finals);
        }
        return null;
    }




    // test the Code.detectLoop method
    public static void testDetectLoop(){
        System.out.println("Testing detectLoop!");
        Object[][] allTests = new Object[][]{
                new Object[]{generateDFAs(0), new Word(new String[]{"0","1","2","1","0"}), true},
                new Object[]{generateDFAs(0), new Word(new String[]{"0","0"}), false},
                new Object[]{generateDFAs(1), new Word(new String[]{"0","1"}), true},
                new Object[]{generateDFAs(1), new Word(new String[]{"1"}), false},
                new Object[]{generateDFAs(2), new Word(new String[]{"1","0"}), true},
                new Object[]{generateDFAs(2), new Word(new String[]{"1"}), false},
                new Object[]{generateDFAs(3), new Word(new String[]{"a", "b", "c"}), true},
                new Object[]{generateDFAs(3), new Word(new String[]{"a", "c"}), true},
                new Object[]{generateDFAs(4), new Word(new String[]{"a", "b", "a"}), true},
                new Object[]{generateDFAs(4), new Word(new String[]{"a"}), false},
                new Object[]{generateDFAs(5), new Word(new String[]{"0"}), true},
                new Object[]{generateDFAs(5), new Word(new String[]{}), false},
        };
        int passed = 0;
        for(int testNum = 0; testNum < allTests.length; testNum ++){
            Object[] o = allTests[testNum];
            if(testNum % 2 == 0) System.out.println("\nTest FSA: " + o[0].toString() + "\n");
            Boolean result = Code.detectLoop((FSA) o[0], (Word) o[1]);
            String message;
            if(result == o[2]){
                message = "Pass";
                passed ++;
            }
            else{
                message = "Fail";
            }
            System.out.println("Test " + (testNum + 1) + " Expected Output: " + o[2] + ", Your Output: " + result + ", " + message);
        }
        System.out.println("\nTotal Passed: " + passed + "/" + allTests.length);
    }


    // test the Code.Pump method
    public static void testCodePump(){
        System.out.println("Testing Pump!");
        Object[][] allTests = new Object[][]{
                new Object[]{generateDFAs(0), new Word(new String[]{"0","1","2","1","0"}), 2, "01211210"},
                new Object[]{generateDFAs(0), new Word(new String[]{"0","1","2","1","0"}), 0, "00"},
                new Object[]{generateDFAs(0), new Word(new String[]{"0","0"}), 2, "No Pump!"},
                new Object[]{generateDFAs(1), new Word(new String[]{"0","1"}), 2, "001"},
                new Object[]{generateDFAs(1), new Word(new String[]{"0","1"}), 0, "1"},
                new Object[]{generateDFAs(1), new Word(new String[]{"1"}), 2, "No Pump!"},
                new Object[]{generateDFAs(2), new Word(new String[]{"1","0"}), 2, "100"},
                new Object[]{generateDFAs(2), new Word(new String[]{"1","0"}), 0, "1"},
                new Object[]{generateDFAs(2), new Word(new String[]{"1"}), 2, "No Pump!"},
                new Object[]{generateDFAs(3), new Word(new String[]{"a", "b", "c"}), 5, "abbbbbc"},
                new Object[]{generateDFAs(3), new Word(new String[]{"a", "b", "c"}), 0, "ac"},
                new Object[]{generateDFAs(3), new Word(new String[]{"a", "c"}), 2, "acac"},
                new Object[]{generateDFAs(4), new Word(new String[]{"a", "b", "a"}), 3, "abababa"},
                new Object[]{generateDFAs(4), new Word(new String[]{"a", "b", "a"}), 0, "a"},
                new Object[]{generateDFAs(4), new Word(new String[]{"a"}), 0, "No Pump!"},
                new Object[]{generateDFAs(5), new Word(new String[]{"0"}), 0, "epsilon"},
                new Object[]{generateDFAs(5), new Word(new String[]{"0"}), 6, "000000"},
                new Object[]{generateDFAs(5), new Word(new String[]{}), 2, "No Pump!"},
        };
        int passed = 0;
        for(int testNum = 0; testNum < allTests.length; testNum ++){
            Object[] o = allTests[testNum];
            if(testNum % 3 == 0) System.out.println("\nTest FSA: " + o[0].toString()+"\n");
            String result = Code.Pump((FSA) o[0], (Word) o[1], (Integer) o[2]).toString();
            String message;
            if(result.equals(o[3].toString())){
                message = "Pass";
                passed ++;
            }
            else{
                message = "Fail";
            }
            System.out.println("Test " + (testNum + 1) + " Expected Output: " + o[3] + ", Your Output: " + result + ", " + message);
        }
        System.out.println("\nTotal Passed: " + passed + "/" + allTests.length);
    }
}
