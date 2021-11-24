package com.futurteam.wordexalt;

import static org.junit.Assert.assertNotNull;

import com.futurteam.wordexalt.logic.planners.GraphPlanner;
import com.futurteam.wordexalt.logic.planners.Planner;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GraphPlannerUnitTest {
    @Test
    public void check2x2_isCorrect() {
        Planner planner = new GraphPlanner(new char[][]
                {
                        {'с', 'т'},
                        {'р', 'о'},
                });

        planner.prepare();

        assertNotNull(planner.check("рост"));
        assertNotNull(planner.check("сорт"));
        assertNotNull(planner.check("тор"));
        assertNotNull(planner.check("рот"));
        assertNotNull(planner.check("орт"));
        assertNotNull(planner.check("ор"));
        assertNotNull(planner.check("ро"));
    }

    @Test
    public void check3x3_isCorrect() {
        Planner planner = new GraphPlanner(new char[][]
                {
                        {'с', 'т', 'к'},
                        {'р', 'о', 'н'},
                        {'в', 'у', 'а'},
                });

        planner.prepare();

        assertNotNull(planner.check("урон"));
        assertNotNull(planner.check("врун"));
        assertNotNull(planner.check("руна"));
        assertNotNull(planner.check("сток"));
        assertNotNull(planner.check("рост"));
        assertNotNull(planner.check("сорт"));
        assertNotNull(planner.check("тон"));
        assertNotNull(planner.check("тор"));
        assertNotNull(planner.check("рот"));
        assertNotNull(planner.check("орт"));
        assertNotNull(planner.check("ор"));
        assertNotNull(planner.check("ро"));
    }
}