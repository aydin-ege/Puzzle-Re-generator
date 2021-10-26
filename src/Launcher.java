import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * @author Denizhan This class is the Launcher of the program. We first fetch
 *         the site by running the program Ege and Ali written in Python. I use
 *         command prompt as a bridge. Then, aiFood.txt will be created. I
 *         decode the datas and process them. Then, I provide them to Onur's
 *         GUI.
 * 
 *         Project Members: Denizhan Soydas Onur Kirmizi Sina Sahan Ege Aydin
 *         Ali Ozer
 * 
 */
public class Launcher {

	public static void main(String[] args) {

//		try {
//			// We are executing crossword fetch program, written in Python.
//			Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"py scraper_new.py && py webScraper.py && exit\"");
//		} catch (Exception e) {
//			System.out.println("OooOOoOps! Doing Something Wrong! ");
//			e.printStackTrace();
//		}

		// we are getting data from the file that fetch program created. (Puzzle fetch
		// step)
		File file = new File("aiFood.txt");
		while (!file.exists()) {
			System.out.println("Error Loading the site...");

		}

//		try {
//			//we are getting new clues from the file that is generated. (New clues fetch step)
//			System.out.println("**********CHECKPOINT1");
//			Runtime.getRuntime().exec("cmd /c start cmd.exe /K  \" echo hello && py webScraper.py && echo hello && exit\"");
//			System.out.println("**********CHECKPOINT2");
//		} catch (Exception e) {
//			System.out.println("**********CHECKPOINT3");
//			System.out.println("OooOOoOps! Doing Something Wrong! ");
//			e.printStackTrace();
//			System.out.println("**********CHECKPOINT4");
//		}

		ArrayList<String> answers = new ArrayList<String>();
		ArrayList<String> oldClues = new ArrayList<String>();
		// ArrayList<String> newClues = new ArrayList<String>();
		ArrayList<Integer> order = new ArrayList<Integer>();
		int downWardStartIndex = -1;
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

		Scanner scPuzzle = null;
		try {
			scPuzzle = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("file i/o error on Puzzle");
		}

		if (scPuzzle != null) {
			String nextWord = " ";

			// Fetching the order
			while (nextWord.charAt(nextWord.length() - 1) != ']') {
				nextWord = scPuzzle.next();
				String order_not_processed = nextWord;
				if (order_not_processed.charAt(0) == '[')
					order_not_processed = (String) order_not_processed.subSequence(1, order_not_processed.length());
				order.add(new Integer((int) (order_not_processed.charAt(0) - 48)));
			}

			for (int i = 0; i < order.size(); i++) {
				System.out.println(order.get(i));
			}
			for (int i = 0; i <= order.size() - 2; i++) {
				if (order.get(i) > order.get(i + 1)) {
					downWardStartIndex = i + 1;
					break;
				}
			}
			System.out.println("DownwardStartIndex is: " + downWardStartIndex);
			nextWord = " ";
			// Fetching the answers
			while (nextWord.charAt(nextWord.length() - 1) != ']') {

				nextWord = scPuzzle.next();
				answers.add(nextWord);

			}

			// Rendering the answers.
			answers.set(0, (String) answers.get(0).subSequence(1, answers.get(1).length() + 1));
			System.out.println("Size: " + answers.size());
			answers.set(answers.size() - 1,
					(String) answers.get(answers.size() - 1).subSequence(0, answers.get(1).length()));
			for (int i = 0; i < answers.size(); i++) {
				answers.set(i, (String) answers.get(i).subSequence(1, answers.get(i).length() - 2));
			}

			// printing the answers
			for (int i = 0; i < answers.size(); i++) {
				System.out.println(answers.get(i));
			}

			nextWord = " ";
			// Fetching the old clues
			nextWord = scPuzzle.nextLine();
			nextWord = scPuzzle.nextLine();
			String[] clues = nextWord.split("',|\",");
			for (String x : clues) {
				oldClues.add(x);
			}
			for (String x : oldClues) {
				System.out.println(x);
			}
			// now rendering the clues.
			oldClues.set(0, (String) oldClues.get(0).subSequence(2, oldClues.get(0).length()));
			oldClues.set(oldClues.size() - 1, (String) oldClues.get(oldClues.size() - 1).subSequence(0,
					oldClues.get(oldClues.size() - 1).length() - 2));
			for (int i = 1; i <= oldClues.size() - 1; i++) {
				if (oldClues.get(i).charAt(0) == '\"' && oldClues.get(i).charAt(oldClues.get(i).length()) == '\"') {
					oldClues.set(i, (String) oldClues.get(i).subSequence(1, oldClues.get(i).length() - 1));
				} else {
					oldClues.set(i, (String) oldClues.get(i).subSequence(2, oldClues.get(i).length()));
				}
			}
			for (String x : oldClues) {
				System.out.println(x);
			}

			nextWord = " ";
			// Fetching the coordinates
			nextWord = scPuzzle.nextLine();
			// System.out.println(nextWord);
			String[] coordinates_not_rendered = nextWord.split(";");
			for (String x : coordinates_not_rendered) {
				System.out.println(x);
			}
			for (int i = 0; i <= coordinates_not_rendered.length - 1; i++) {
				coordinates_not_rendered[i] = (String) coordinates_not_rendered[i].subSequence(1,
						coordinates_not_rendered[i].length());
			}
			coordinates_not_rendered[coordinates_not_rendered.length
					- 1] = (String) coordinates_not_rendered[coordinates_not_rendered.length - 1].subSequence(0,
							coordinates_not_rendered[coordinates_not_rendered.length - 1].length() - 1);

			for (String x : coordinates_not_rendered) {
				System.out.println(x);
			}
			for (String x : coordinates_not_rendered) {
				Coordinate tempCoordinate = new Coordinate((int) x.charAt(3) - 48, (int) x.charAt(5) - 48);
				coordinates.add(tempCoordinate); // WARNING INDEX STARTS AT 0 BUT, PUZZLE STARTS AT 1st KEY!
			}
			for (Coordinate x : coordinates) {
				System.out.println(x);
			}

			Date date;
			try {
				date = new SimpleDateFormat("dd/MM/yyyy").parse(args[0]);
			} catch (ParseException e1) {
				date = new Date();
			}
			String dateTrimmed = ((String) date.toString().subSequence(0, 10) + ","
					+ (String) date.toString().subSequence(24, date.toString().length()));

			System.out.println(date);
			System.out.println(dateTrimmed);
			System.out.println(order);

			String preSpace = "       ";
			String postSpace = "    ";
			int maxSize = 30;

			// Creating Area Strings.
			String acrossAreaOld = "";
			String downAreaOld = "";
			for (int i = 0; i < downWardStartIndex; i++) {
				// acrossAreaOld = acrossAreaOld + "\n" + order.get(i) + " " + oldClues.get(i) +
				// " ";
				acrossAreaOld = acrossAreaOld + preSpace + order.get(i) + postSpace;
				int size = 0;
				String[] words = oldClues.get(i).split(" ");
				for (int j = 0; j < words.length; j++) {
					size += words[j].length();
					acrossAreaOld = acrossAreaOld + words[j] + " ";
					if (size > maxSize && words.length > j + 1) {
						acrossAreaOld = acrossAreaOld + "\n" + postSpace + "  " + preSpace;
						size = 0;
					}
				}
				if (acrossAreaOld.charAt(acrossAreaOld.length() - 1) == '\n') {
					acrossAreaOld = acrossAreaOld.substring(0, acrossAreaOld.length() - 1);
				}

				acrossAreaOld = acrossAreaOld + "\n";

			}
			for (int i = downWardStartIndex; i < order.size(); i++) {
				// downAreaOld = downAreaOld + "\n" + order.get(i) + " " + oldClues.get(i) + "
				// ";
				downAreaOld = downAreaOld + preSpace + order.get(i) + postSpace;
				String[] words = oldClues.get(i).split(" ");
				int size = 0;
				for (int j = 0; j < words.length; j++) {
					size += words[j].length();
					downAreaOld = downAreaOld + words[j] + " ";
					if (size > maxSize && words.length > j + 1) {
						downAreaOld = downAreaOld + "\n" + postSpace + "  " + preSpace;
						size = 0;
					}
				}
				if (downAreaOld.charAt(downAreaOld.length() - 1) == '\n') {
					downAreaOld = downAreaOld.substring(0, downAreaOld.length() - 1);
				}

				downAreaOld = downAreaOld + "\n";

			}

			System.out.println(acrossAreaOld);
			System.out.println(downAreaOld);
//			String letters = "";
//			for(String x :answers) {
//				letters = letters + x;
//			}

			String[] letters = new String[downWardStartIndex];
			for (int i = 0; i <= downWardStartIndex - 1; i++) {
				letters[i] = answers.get(i);
			}
			System.out.println("CHECKPOINT ***");
			for (int i = 0; i <= letters.length - 1; i++) {
				System.out.println(letters[i]);
			}
			acrossAreaOld = acrossAreaOld.replace("\\\'", "\'");
			downAreaOld = downAreaOld.replace("\\\'", "\'");

			System.out.println("We fetched the whole puzzle");
			System.out.println("Now its time to generate new clues...");

			// we are getting new clues from the file that is generated
			File fileNew = new File("generatedClues.txt");
			while (!fileNew.exists()) {
				System.out.println("Error generating Clues...");
			}

			Scanner scClues = null;
			try {
				scClues = new Scanner(fileNew);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("file i/o error on Clues");
			}

			String allNewClues = scClues.nextLine();
			String[] cluesNew = allNewClues.split("#####"); // REGEX !
			ArrayList<String> cluesNewAL = new ArrayList<String>();
			for (String x : cluesNew) {
				cluesNewAL.add(x.substring(0, 1).toUpperCase() + x.substring(1));
			}

			String acrossAreaNew = "";
			String downAreaNew = "";
			for (int i = 0; i < downWardStartIndex; i++) {
				// acrossAreaNew = acrossAreaNew + "\n" + order.get(i) + " " + cluesNewAL.get(i)
				// + " ";
				acrossAreaNew = acrossAreaNew + preSpace + order.get(i) + postSpace;
				String[] words = cluesNewAL.get(i).split(" ");
				int size = 0;
				for (int j = 0; j < words.length; j++) {
					size += words[j].length();
					acrossAreaNew = acrossAreaNew + words[j] + " ";
					if (size > maxSize && words.length > j + 1) {
						acrossAreaNew = acrossAreaNew + "\n" + postSpace + "  " + preSpace;
						size = 0;
					}
				}
				if (acrossAreaNew.charAt(acrossAreaNew.length() - 1) == '\n') {
					acrossAreaNew = acrossAreaNew.substring(0, acrossAreaNew.length() - 1);
				}
				acrossAreaNew = acrossAreaNew + "\n";
				acrossAreaNew = acrossAreaNew.substring(0, 1).toUpperCase() + acrossAreaNew.substring(1);
			}
			for (int i = downWardStartIndex; i < order.size(); i++) {
				// downAreaNew = downAreaNew + "\n" + order.get(i) + " " + cluesNewAL.get(i) + "
				// ";
				downAreaNew = downAreaNew + preSpace + order.get(i) + postSpace;
				String[] words = cluesNewAL.get(i).split(" ");
				int size = 0;
				for (int j = 0; j < words.length; j++) {
					size += words[j].length();
					downAreaNew = downAreaNew + words[j] + " ";
					if (size > maxSize && words.length > j + 1) {
						downAreaNew = downAreaNew + "\n" + postSpace + "  " + preSpace;
						size = 0;
					}
				}
				if (downAreaNew.charAt(downAreaNew.length() - 1) == '\n') {
					downAreaNew = downAreaNew.substring(0, downAreaNew.length() - 1);
				}

				downAreaNew = downAreaNew + "\n";
				downAreaNew = downAreaNew.substring(0, 1).toUpperCase() + downAreaNew.substring(1);

			}
			acrossAreaOld = acrossAreaOld.replace("&#128514;", ":)");// WARNING
			downAreaOld = downAreaOld.replace("&#128514;", ":)");// WARNING
			GUICreator gc = new GUICreator(acrossAreaOld, downAreaOld, acrossAreaNew, downAreaNew, dateTrimmed, letters,
					coordinates);

			// Saying Goodbye.
			scPuzzle.close();
			scClues.close();
//			if (file.delete())
//				System.out.println("LOG:Temporary Files Successfully Deleted.");
//			else
//				System.out.println("LOG:Temporary Files Could not be Deleted. Remove them manually.");
		}

	}

}
