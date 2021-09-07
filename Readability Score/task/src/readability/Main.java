package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        try {
            String text = readFileAsString(args[0]);

            String[] sentences = text.split("[.?!]");
            int sentenceNum = sentences.length;

            List<String> words = new ArrayList<>();
            for (String sentence : sentences) {
                words.addAll(List.of(sentence.strip().split("\\s")));
            }
            int wordNum = words.size();
            int characters = text.replace(" ", "").length();

            int syllables = 0;
            int polysyllables = 0;
            for (String word : words) {
                String fixed = word
                        .replaceAll("e\\b", "")
                        .replaceAll("you", "a")
                        .replaceAll("[aeiouy]{2}", "a")
                        .replaceAll(" th "," a ")
                        .replaceAll(",","")
                        .replaceAll(" w "," a ")
                        .replaceAll("[0-9]+", "a")
                        .replaceAll("[^aeiouy]", "");
                int wordSyllables = fixed.length();

                if (wordSyllables == 0) {
                    syllables++;
                    continue;
                } else if (wordSyllables > 2)
                    polysyllables++;

                syllables += wordSyllables;
            }

            System.out.printf("Words: %s\n", wordNum);
            System.out.printf("Sentences: %s\n", sentenceNum);
            System.out.printf("Characters: %s\n", characters);

            System.out.printf("Syllables: %s\n", syllables);
            System.out.printf("Polysyllables: %s\n", polysyllables);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String choice = s.next();

            double scoreARI = 4.71 * (characters * 1.0 / wordNum)
                    + 0.5 * (wordNum * 1.0 / sentenceNum) - 21.43;

            double scoreFK =  0.39 * (wordNum * 1.0 / sentenceNum)
                    + 11.8 * (syllables * 1.0 / wordNum) - 15.59;

            double scoreSMOG = 1.043 * Math.sqrt(polysyllables * 30.0 / sentenceNum) + 3.1291;

            double scoreCL = 0.0588 * (characters * 1.0 / wordNum * 100)
                    - 0.296 * (sentenceNum * 1.0 / wordNum * 100) - 15.8;

            if (choice.equals("ARI")) score(1, scoreARI);
            if (choice.equals("FK")) score(2, scoreFK);
            if (choice.equals("SMOG")) score(3, scoreSMOG);
            if (choice.equals("CL")) score(4, scoreCL);

            if (choice.equals("all")) {
                double ageAvg = score(1, scoreARI)
                        + score(2, scoreFK)
                        + score(3, scoreSMOG)
                        + score(4, scoreCL);
                System.out.printf("\n\nThis text should be understood in average by %.2f-year-olds.", ageAvg);
            }

        } catch (IOException ignored) {
        }
    }

    public static int toAgeIndex (int score) {
        int[] array = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24};
        return array[score - 1];
    }

    public static int score(int type, double score) {
        int intScore = (int) Math.ceil(score);
        if (intScore > 13) intScore = 13;
        int ageRange = toAgeIndex(intScore);

        switch (type) {
            case 1:
                System.out.printf("\nAutomated Readability Index: %.2f (about %s-year-olds)", score, ageRange);
                break;
            case 2:
                System.out.printf("\nFlesch–Kincaid readability tests: %.2f (about %s-year-olds)", score, ageRange);
                break;
            case 3:
                System.out.printf("\nSimple Measure of Gobbledygook: %.2f (about %s-year-olds)", score, ageRange);
                break;
            case 4:
                System.out.printf("\nColeman–Liau index: %.2f (about %s-year-olds)", score, ageRange);
                break;
        }
        return ageRange;
    }
}
