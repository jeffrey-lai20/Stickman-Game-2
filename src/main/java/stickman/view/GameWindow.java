package stickman.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import stickman.model.Entity.Entity;
import stickman.model.GameEngineImpl;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Draws the game's window as well as its entities. Continously
 * updates the frame and draws newly position entities.
 */
public class GameWindow {
    private final int width;
    private Scene scene;
    private Pane pane;
    private GameEngineImpl model;
    private List<EntityView> entityViews;
    private BackgroundDrawer backgroundDrawer;
    private double elapsedTime = 0;

    private double xViewportOffset = 0.0;
    private static final double VIEWPORT_MARGIN = 280.0;

    Text time;
    Text lives;

    /**
     * Constructor for GameWindow. Sets the initial values of its
     * attributes and initialises drawing and intake of keyboard input.
     * @param model
     * @param width
     * @param height
     */
    public GameWindow(GameEngineImpl model, int width, int height) {
        this.model = model;
        this.pane = new Pane();
        this.width = width;
        this.scene = new Scene(pane, width, height);

        this.entityViews = new ArrayList<>();

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        this.backgroundDrawer = new ParallaxBackground();
        backgroundDrawer.draw(model, pane);
        time = new Text();
        lives = new Text();
        pane.getChildren().add(time);
        pane.getChildren().add(lives);
    }

    /** Returns the scene. */
    public Scene getScene() {
        return this.scene;
    }

    /** Runs the game window at a refresh rate of every 17ms*/
    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Draws and updates the game window and all its entities. Also
     * continuously updates the time and number of lives on the the
     * game window, and displays end-game messages.
     */
    private void draw() {
        model.tick();

        List<Entity> entities = model.getCurrentLevel().getEntities();

        for (EntityView entityView: entityViews) {
            entityView.markForDelete();
        }

        double heroXPos = model.getCurrentLevel().getHeroX();
        heroXPos -= xViewportOffset;

        if (heroXPos < VIEWPORT_MARGIN) {
            if (xViewportOffset >= 0) {
                xViewportOffset -= VIEWPORT_MARGIN - heroXPos;
                if (xViewportOffset < 0) {
                    xViewportOffset = 0;
                }
            }
        } else if (heroXPos > width - VIEWPORT_MARGIN) {
            xViewportOffset += heroXPos - (width - VIEWPORT_MARGIN);
        }

        backgroundDrawer.update(xViewportOffset);

        for (Entity entity: entities) {
            boolean notFound = true;
            for (EntityView view: entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView: entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);

        if (model.heroDead()) {
            try {
                java.util.concurrent.TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.restartLevel();
        }
        if (model.finish()) {
            Text finished = new Text("F I N I S H E D !");
            finished.setFont(new Font(50));
            finished.setX(model.getCurrentLevel().getWidth()/2-150);
            finished.setY(model.getCurrentLevel().getHeight()/2);
            pane.getChildren().add(finished);
        } else if (model.gameOver()) {
            Text finished = new Text("G A M E  O V E R !");
            finished.setFont(new Font(50));
            finished.setX(model.getCurrentLevel().getWidth()/2-200);
            finished.setY(model.getCurrentLevel().getHeight()/2);
            pane.getChildren().add(finished);

            lives.setText("Lives: " + model.getLives());
            lives.setFont(new Font(20));
            lives.setX(width-100);
            lives.setY(30);

        } else {
            elapsedTime = (new Date()).getTime() - model.getStartTime();
            elapsedTime = elapsedTime/1000;
            time.setText("Time: " + elapsedTime);
            time.setFont(new Font(20));
            time.setX(30);
            time.setY(30);
            lives.setText("Lives: " + model.getLives());
            lives.setFont(new Font(20));
            lives.setX(width-100);
            lives.setY(30);
        }





    }
}
