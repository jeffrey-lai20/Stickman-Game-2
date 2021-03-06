import org.junit.Test;
import stickman.model.LevelImpl;

import static org.junit.Assert.*;

/**
 * Set of tests for LevelImpl.
 */
public class LevelTest {
    LevelImpl level = new LevelImpl(20, 3.2, "normal", 1, 2,
            new double[] {500.0, 250.0, 1000.0, 225.0}, 2, new double [] {700.0, 400.0}, 1280);

    /**
     * Test for basic functionality.
     */
    @Test
    public void basicTest() {
        assertEquals(300, level.getHeight(), 0.01);
        assertEquals(640, level.getWidth(), 0.01);
        assertNotNull(level.getEntities());
        assertEquals(300, level.getFloorHeight(), 0.01);
        assertEquals(20, level.getHeroX(), 0.01);

    }

    /**
     * Tests functionality of hero movement.
     */
    @Test
    public void moveTest() {
        assertTrue(level.jump());
        assertTrue(level.moveLeft());
        assertTrue(level.moveRight());
        level.tick();
        assertTrue(level.stopMoving());
    }

    /**
     * Tests whether or not end-game has been reached.
     */
    @Test
    public void endTest() {
        assertFalse(level.finish());
        assertFalse(level.heroDead());
    }

    /**
     * Tests the occurrence of finishing the game.
     */
    @Test
    public void finishTest() {
        LevelImpl level2 = new LevelImpl(1280, 3.2, "normal", 1, 2,
                new double[] {500.0, 250.0, 1000.0, 225.0}, 2, new double [] {700.0, 400.0}, 1280);
        level2.tick();
        assertTrue(level2.finish());
    }

    /**
     * Tests the occurrence of hero's death in the game.
     */
    @Test
    public void diedTest() {
        LevelImpl level2 = new LevelImpl(400, 3.2, "normal", 1, 2,
                new double[] {500.0, 250.0, 1000.0, 225.0}, 2, new double [] {700.0, 400.0}, 1280);
        level2.tick();
        assertTrue(level2.heroDead());
    }

}
