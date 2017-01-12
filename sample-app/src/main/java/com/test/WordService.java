package com.test;

import org.springframework.stereotype.Service;

/**
 * {@link WordService} represents a simple service providing {@link Word}s.
 *
 * @author Christian Fehmer
 *
 */
@Service
public class WordService {

    /**
     * provides a static {@link Word}
     *
     * @return the word "Lua" with the id 1
     */
    public Word getWord() {
        Word word = new Word();
        word.setWordId(1);
        word.setWordString("Lua");
        return word;
    }

}
