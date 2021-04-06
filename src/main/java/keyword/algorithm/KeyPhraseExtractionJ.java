package keyword.algorithm;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

public class KeyPhraseExtractionJ {

        static final float d = 0.85f;           //damping factor, default 0.85
        static final int max_iter = 200;        //max iteration times
        static final float min_diff = 0.0001f;  //condition to judge whether recurse or not
        private static  int nKeyword= 50;         //number of keywords to extract,default 5
        private static  int coOccuranceWindow=3; //size of the co-occurance window, default 3

        // change default parameters
        public static void setKeywordNumber(int sysKeywordNum)
        {
            nKeyword = sysKeywordNum;
        }

        public static void setWindowSize(int window)
        {
            coOccuranceWindow = window;
        }

        //    public static List<String> getKeyword(String title, String content)
//    {
        public static List<String> getKeyword(String content){
            String title = "";

            Map<String, Float> score = KeyPhraseExtractionJ.getWordScore(title, content);

            //rank keywords in terms of their score
            List<Map.Entry<String, Float>> entryList = new ArrayList<>(score.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>()
                    {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
                        {
                            return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
                        }
                    }
            );

            System.out.println("After sorting: "+entryList);

            List<String> sysKeywordList=new ArrayList<String>();

            //List<String>  unmergedList=new ArrayList<String>();
            for (int i = 0; i < nKeyword; ++i){
                try{
                    //unmergedList.add(entryList.get(i).getKey());
                    sysKeywordList.add(entryList.get(i).getKey());
                }catch(IndexOutOfBoundsException e){
                    continue;
                }
            }

            System.out.print("window:"+coOccuranceWindow+"\nkeywordNum: "+nKeyword);
            return sysKeywordList;
        }

        public static Map<String,Float> getWordScore(String title, String content)
        {

            //segment text into words
            List<Term> termList = HanLP.segment(title + content);

            int count=1;  //position of each word
            Map<String,Integer> wordPosition = new HashMap<String,Integer>();

            List<String> wordList=new ArrayList<String>();

            //filter stop words
            for (Term t : termList)
            {
                if (shouldInclude(t))
                {
                    wordList.add(t.word);
                    if(!wordPosition.containsKey(t.word))
                    {
                        wordPosition.put(t.word,count);
                        count++;
                    }
                }
            }
            //System.out.println("Keyword candidates:"+wordList);

            //generate word-graph in terms of size of co-occur window
            Map<String, Set<String>> words = new HashMap<String, Set<String>>();
            Queue<String> que = new LinkedList<String>();
            for (String w : wordList)
            {
                if (!words.containsKey(w))
                {
                    words.put(w, new HashSet<String>());
                }
                que.offer(w);    // insert into the end of the queue
                if (que.size() > coOccuranceWindow)
                {
                    que.poll();  // pop from the queue
                }

                for (String w1 : que)
                {
                    for (String w2 : que)
                    {
                        if (w1.equals(w2))
                        {
                            continue;
                        }

                        words.get(w1).add(w2);
                        words.get(w2).add(w1);
                    }
                }
            }
            //System.out.println("word-graph:"+words); //each k,v represents all the words in v point to k

            // iterate till recurse
            Map<String, Float> score = new HashMap<String, Float>();
            for (int i = 0; i < max_iter; ++i)
            {
                Map<String, Float> m = new HashMap<String, Float>();
                float max_diff = 0;
                for (Map.Entry<String, Set<String>> entry : words.entrySet())
                {
                    String key = entry.getKey();
                    Set<String> value = entry.getValue();
                    m.put(key, 1 - d);
                    for (String other : value)
                    {
                        int size = words.get(other).size();
                        if (key.equals(other) || size == 0) continue;
                        m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                    }

                    max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 1 : score.get(key))));
                }
                score = m;

                //exit once recurse
                if (max_diff <= min_diff)
                    break;
            }
            return score;
        }

        /**
         * judge whether a word belongs to stop words
         * @param term(Term): word needed to be judged
         * @return(boolean):  if the word is a stop word,return false;otherwise return true
         */
        public static boolean shouldInclude(Term term)
        {
            return CoreStopWordDictionary.shouldInclude(term);
        }




    public static void main(String[] args) {

        String content= "Keyword extraction (also known as keyword detection or keyword analysis) is a text analysis technique that automatically extracts the most used and most important words and expressions from a text. It helps summarize the content of texts and recognize the main topics discussed.\n" +
                "\n" +
                "Keyword extraction uses machine learning artificial intelligence (AI) with natural language processing (NLP) to break down human language so that it can be understood and analyzed by machines. It’s used to find keywords from all manner of text: regular documents and business reports, social media comments, online forums and reviews, news reports, and more.\n" +
                "\n" +
                "Imagine you want to analyze thousands of online reviews about your product. Keyword extraction helps you sift through the whole set of data and obtain the words that best describe each review in just seconds. That way, you can easily and automatically see what your customers are mentioning most often, saving your teams hours upon hours of manual processing.";
        String regex = "\\b(a|able|it|about|across|after|all|almost|also|am|among|an|and|any|are|as|at|be|because|been|but|by|can|cannot|could|dear|did|do|does|either|else|ever|every|for|from|get|got|had|has|have|he|her|hers|him|his|how|however|i|if|in|into|is|it|its|just|least|let|like|likely|may|me|might|most|must|my|neither|no|nor|not|of|off|often|on|only|or|other|our|own|rather|said|say|says|she|should|since|so|some|than|that|the|their|them|then|there|these|they|this|tis|to|too|twas|us|wants|was|we|were|what|when|where|which|while|who|whom|why|will|with|would|yet|you|your)\\b\\s?";
        String con1 = content.toLowerCase().replaceAll(regex ,"");
        System.out.println(KeyPhraseExtractionJ.getKeyword(con1));




    }
    }

