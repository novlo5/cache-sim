import java.io.*; // BufferedReader
import java.util.*;

public class ac_test
{

    public static void main(String[] args) throws Exception {
        BufferedReader infile = new BufferedReader(new FileReader("dictionary.txt"));
		final int ARRAY_LEN = 5;
        Scanner kbd = new Scanner(System.in);
		File filewrite = new File("user_history.txt");
		filewrite.createNewFile();
		BufferedReader infile2 = new BufferedReader(new FileReader("user_history.txt"));
        FileWriter persist = new FileWriter(filewrite, true);
		int length = 0, count = 0, total_time = 0;
	    final double Nano2Seconds = 1000000000; // convert nano to S
		
        DLB dictionary = new DLB();
		DLB user_history = new DLB(); 
        while (infile.ready()){
            String a = infile.readLine();
            dictionary.addWord(a);
        }
		infile.close();
		while(infile2.ready())
		{
			String A = infile2.readLine();
			user_history.addWord(A);
		}
        infile2.close();
        System.out.print("Enter your first character: ");
        String prefix = kbd.nextLine();
		System.out.println();
        String [] predictions = new String[ARRAY_LEN];
        if (containsExitChar(prefix)) {
            kbd.close();
            System.exit(0);
        }
        do {
		double start = System.nanoTime();
		if(!user_history.empty())
		{
           length = user_history.keysWithPrefix(prefix, predictions, length);
		}
		if (length < 5){
			   length = dictionary.keysWithPrefix(prefix, predictions, length);
		}
		double endtime = (System.nanoTime());
		double end = (endtime - start) / Nano2Seconds ;
		total_time += end;
		count++;
		System.out.println("( " + end + " s " + ")");
		System.out.println("Predictions: ");
		System.out.println();
		
		    for (int j = 0; j < length; j++) {
            System.out.print(" (" + (j + 1) + ") " + predictions[j]);
			}
			System.out.println();
            System.out.print("Enter your next character: ");
            prefix += kbd.nextLine();
			if(containsNum(prefix, predictions, persist, user_history)){
				prefix = ""; //reset
				System.out.println("Enter the first character of your next word: ");
				prefix += kbd.nextLine();
			}
			length = 0;
			System.out.println();
        }while (!containsExitChar(prefix));
		persist.close();
        kbd.close();
		System.out.println("Average time: " + (total_time / (((double)count)))); // I have no clue why this doesnt work
        System.out.println("Bye!");
		
    }
	public static boolean containsExitChar(String s) { // helper for ending do while
        return s.charAt(s.length() - 1) == ('!');
    }
	public static boolean containsNum(String s,String[] predictions, FileWriter save, DLB user_history) throws IOException// check to add into user_history.txt
	{
		if(s.charAt(s.length() - 1) == '$')
		{
			String newS = s.substring(0, s.length() - 1);
			save.append( newS + "\n");
			user_history.addWord(newS);
			System.out.println("Word Completed: " + newS);
			return true;
		}
		if(s.charAt(s.length() - 1) == '1')
		{
			String newS = s.substring(0, s.length() - 1);
			save.append( predictions[0] + "\n");
			user_history.addWord(predictions[0]);
			System.out.println("Word Completed: " + predictions[0]);
			return true;
		}
		if(s.charAt(s.length() - 1) == '2')
		{
			save.append( predictions[1] + "\n");
			user_history.addWord(predictions[1]);
			System.out.println("Word Completed: " + predictions[1]);
			return true;
		}
		if(s.charAt(s.length() - 1) == '3')
		{
			save.append( predictions[2] + "\n");
			user_history.addWord(predictions[2]);
			System.out.println("Word Completed: " + predictions[2]);
			return true;
		}
		if(s.charAt(s.length() - 1) == '4')
		{
			save.append( predictions[3] + "\n");
			user_history.addWord(predictions[3]);
			System.out.println("Word Completed: " + predictions[3]);
			return true;
		}
		if(s.charAt(s.length() - 1) == '5')
		{
			save.append(predictions[4] + "\n");
			user_history.addWord(predictions[4]);
			System.out.println("Word Completed: " + predictions[4]);
			return true;
		}
		return false;
	}
}

  
	
