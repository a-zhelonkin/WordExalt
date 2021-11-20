package com.futurteam.wordexalt;

import org.junit.Test;

import static org.junit.Assert.*;

import com.futurteam.wordexalt.logic.Planner;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void check2x2_isCorrect() {
        Planner planner = new Planner(new char[][]
        {
            {'с', 'т'},
            {'р', 'о'},
        });

        planner.Prepare();

        assertTrue(planner.Check("рост"));
        assertTrue(planner.Check("сорт"));
        assertTrue(planner.Check("тор"));
        assertTrue(planner.Check("рот"));
        assertTrue(planner.Check("орт"));
        assertTrue(planner.Check("ор"));
        assertTrue(planner.Check("ро"));
    }
    @Test
    public void check3x3_isCorrect() {
        Planner planner = new Planner(new char[][]
        {
            {'с', 'т', 'к'},
            {'р', 'о', 'н'},
            {'в', 'у', 'а'},
        });

        planner.Prepare();

        assertTrue(planner.Check("урон"));
        assertTrue(planner.Check("врун"));
        assertTrue(planner.Check("руна"));
        assertTrue(planner.Check("сток"));
        assertTrue(planner.Check("рост"));
        assertTrue(planner.Check("сорт"));
        assertTrue(planner.Check("тон"));
        assertTrue(planner.Check("тор"));
        assertTrue(planner.Check("рот"));
        assertTrue(planner.Check("орт"));
        assertTrue(planner.Check("ор"));
        assertTrue(planner.Check("ро"));
    }
}