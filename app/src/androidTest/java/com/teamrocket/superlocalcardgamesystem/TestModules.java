package com.teamrocket.superlocalcardgamesystem;

import android.test.AndroidTestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tj on 6/11/15.
 */
public class TestModules extends AndroidTestCase{
    public void testGameStatusShuffle(){
        GameStatus gs = new GameStatus();

        gs.shuffleDeck();
        List<Playingcard> deck = gs.cardDeck;
        Set<String> set = new HashSet<String>();
        for(Playingcard p : deck){
            if(set.contains(p.value) ){
                assertTrue(false);
            }
            set.add(p.value);
        }

        assertTrue(true);
    }
}
