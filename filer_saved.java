import java.io.*;
import java.lang.*;
import java.util.*;

class filter {

    int num_ham = 0;
    int num_spam = 0;
    double class_h = 0;
    double class_s = 0;
    Set<String> dist_words = new HashSet<String>(); // stores distinct words
    ArrayList<String> ham_words = new ArrayList<String>(); // stores all ham words counting duplicates
    ArrayList<String> spam_words = new ArrayList<String>(); // stores all spam words counting duplicates
    Map<String,Double> ham_map = new HashMap<String,Double>(); // stores distinct words and probablities of ham
    Map<String,Double> spam_map = new HashMap<String,Double>(); // stores distinct words and probabilities of spam

    void read_files(String directory) throws IOException {

        File dir = new File(directory);
        File[] emails = dir.listFiles();
        int num_files = emails.length;
        
        for (int i = 0; i < emails.length; i ++) {

            String filename = emails[i].getName();
            Scanner count = new Scanner(emails[i]);

            // if ham email
            if (filename.startsWith("ham")) {
                num_ham += 1;
                while (count.hasNext()) {
                    String word = count.next();
                    dist_words.add(word);
                    ham_words.add(word);
                }
            }

            // if spam email
            else if (filename.startsWith("spam")) {
                num_spam += 1;
                while (count.hasNext()) {
                    String word = count.next();
                    dist_words.add(word);
                    spam_words.add(word);
                }
            }
        }
    }


    void train_likelihood() {

        double h_product = 0;
        double s_product = 0;
        double num_docs = num_ham + num_spam;
        class_h = num_ham / num_docs;
        class_s = num_spam / num_docs;

        Iterator<String> it = dist_words.iterator();
        while (it.hasNext()) {
            String word = it.next();
            int ham_frequency = Collections.frequency(ham_words, word);
            int spam_frequency = Collections.frequency(spam_words, word);
            
            // calculate ham probability
            double h_numerator = ham_frequency + 1;
            double h_denomenator = (ham_words.size() + dist_words.size());
            double ham_prob = h_numerator/h_denomenator;
            ham_map.put(word,ham_prob);

            // calculate spam probability
            double s_numerator = spam_frequency + 1;
            double s_denomenator = (spam_words.size() + dist_words.size());
            double spam_prob = s_numerator/s_denomenator;
            spam_map.put(word,spam_prob);
        }
    }


    void classify(String test_file) throws IOException {

        double h_sum = 0;
        double s_sum = 0;

        File test = new File(test_file);
        Scanner test_words = new Scanner(test);
        while (test_words.hasNext()) {
            String word = test_words.next();
            if (ham_map.containsKey(word)) h_sum += Math.log((double)ham_map.get(word));
            if (spam_map.containsKey(word)) s_sum += Math.log((double)spam_map.get(word));
        }

        double ham_prob = Math.log(class_h) + h_sum;
        double spam_prob = Math.log(class_s) + s_sum;
        if (ham_prob > spam_prob) System.out.println("ham");
        else System.out.println("spam");

    }


    public static void main(String[] args) throws IOException {

        filter new_filter = new filter();

        new_filter.read_files(args[0]);
        new_filter.train_likelihood();
        new_filter.classify(args[1]);

    }

}

