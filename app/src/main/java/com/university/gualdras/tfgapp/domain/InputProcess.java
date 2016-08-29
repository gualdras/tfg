package com.university.gualdras.tfgapp.domain;

import com.university.gualdras.tfgapp.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * Created by gualdras on 15/08/16.
 */
public class InputProcess {

    static IDictionary dict;

    public static String cleanInput(String input){
        StringBuilder output = new StringBuilder();
        if(input.length() > 0) {
            String[] sInput = input.split(" ");
            output = new StringBuilder();
            getDict();

            for (String w : sInput) {
                List<String> s;

                WordnetStemmer ws = new WordnetStemmer(dict);
                if ((s = ws.findStems(w, null)).size() > 0) {
                    output.append(s.get(0) + " ");
                }
            }
        }
        return output.toString().trim();
    }

    public static ArrayList<String> getSynonyms(String search) {
        String[] input = search.split(" ");
        ArrayList<String> synList = new ArrayList<>();
        getDict();

        for (String w : input) {
            for (POS pos : POS.values()) {
                IIndexWord idxWord = dict.getIndexWord(w, pos);
                if (idxWord != null) {
                    IWordID wordID = idxWord.getWordIDs().get(0);
                    IWord word = dict.getWord(wordID);

                    ISynset synSet = word.getSynset();
                    if(synSet.getWords().size() > 0) {
                        for (IWord syn : synSet.getWords()) {
                            synList.add(syn.getLemma());
                        }
                        break;
                    }
                }
            }
        }
        return synList;
    }
    
    public static ArrayList<String> getHypernyms(String search){
        String[] input = search.split(" ");
        ArrayList<String> hyperList = new ArrayList<>();
        getDict();

        for (String w : input) {
            for (POS pos : POS.values()) {
                IIndexWord idxWord = dict.getIndexWord(w, pos);
                if (idxWord != null) {
                    IWordID wordID = idxWord.getWordIDs().get(0);
                    IWord word = dict.getWord(wordID);

                    ISynset synSet = word.getSynset();
                    List<ISynsetID> hypernyms = synSet.getRelatedSynsets(Pointer.HYPERNYM);
                    List < IWord > words ;
                    for (ISynsetID sid : hypernyms) {
                        words = dict.getSynset(sid).getWords();
                        for(IWord hyper: words){
                            hyperList.add(hyper.getLemma());
                        }
                    }
                }
            }
        }
        return hyperList;
    }

    private static void getDict(){
        URL url = null;
        try {
            url = new URL("file", null, Constants.PATH_TO_DICT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        dict = new Dictionary(url) ;

        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
