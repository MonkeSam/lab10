package it.unibo.mvc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private static final String CONFIG_PATH = "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "config.yml";

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *              the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        final int min;
        final int max;
        final int attempts;
        try {
            loadConfig();
        } catch (IOException e) {
            for (DrawNumberView drawNumberView : views) {
                drawNumberView.displayError(e.getMessage());
            }
        }
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view : views) {
            view.setObserver(this);
            view.start();
        }
        this.model = new DrawNumberImpl(min, max, attempts);
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view : views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view : views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);// NOPDM: on porpouse
    }

    /**
     * Loads from config.yml attempts, minimum and maximum generated number.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void loadConfig() throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(CONFIG_PATH), StandardCharsets.UTF_8))) {
            this.min = Integer.parseInt(reader.readLine().split(" ")[1]);
            this.max = Integer.parseInt(reader.readLine().split(" ")[1]);
            this.attempts = Integer.parseInt(reader.readLine().split(" ")[1]);
        }
    }

    /**
     * @param args
     *             ignored
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl());
        new DrawNumberApp(new DrawNumberViewImpl());
    }

}
