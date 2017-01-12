package com.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class WordServiceTest {

    private WordService words = new WordService();

    @Test
    public void providesStaticWordLua() {
        // GIVEN

        // WHEN
        Word word = words.getWord();

        // THEN
        assertThat(word.getWordId(), equalTo(1L));
        assertThat(word.getWordString(), equalTo("Lua"));
    }

}
