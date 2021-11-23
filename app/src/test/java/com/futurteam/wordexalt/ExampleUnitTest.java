package com.futurteam.wordexalt;

import org.junit.Test;

import static org.junit.Assert.*;

import com.futurteam.wordexalt.logic.planners.TreePlanner;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void check2x2_isCorrect() {
        TreePlanner planner = new TreePlanner(new char[][]
        {
            {'с', 'т'},
            {'р', 'о'},
        });

        planner.prepare();

        assertNotNull(planner.Check("рост"));
        assertNotNull(planner.Check("сорт"));
        assertNotNull(planner.Check("тор"));
        assertNotNull(planner.Check("рот"));
        assertNotNull(planner.Check("орт"));
        assertNotNull(planner.Check("ор"));
        assertNotNull(planner.Check("ро"));
    }
    @Test
    public void check3x3_isCorrect() {
        TreePlanner planner = new TreePlanner(new char[][]
        {
            {'с', 'т', 'к'},
            {'р', 'о', 'н'},
            {'в', 'у', 'а'},
        });

        planner.prepare();

        assertNotNull(planner.Check("урон"));
        assertNotNull(planner.Check("врун"));
        assertNotNull(planner.Check("руна"));
        assertNotNull(planner.Check("сток"));
        assertNotNull(planner.Check("рост"));
        assertNotNull(planner.Check("сорт"));
        assertNotNull(planner.Check("тон"));
        assertNotNull(planner.Check("тор"));
        assertNotNull(planner.Check("рот"));
        assertNotNull(planner.Check("орт"));
        assertNotNull(planner.Check("ор"));
        assertNotNull(planner.Check("ро"));
    }
}