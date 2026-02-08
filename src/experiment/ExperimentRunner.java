package experiment;

import java.util.*;
import java.util.concurrent.*;

public class ExperimentRunner {

    private final GameRunner runner;

    public ExperimentRunner(GameRunner runner) {
        this.runner = runner;
    }

    public ExperimentResult run(ExperimentConfig config) {
        ExperimentResult result = new ExperimentResult();

        // Mode Mono-thread (Debug / 1 partie)
        if (config.getNbGames() == 1) {
            GameResult game = runner.runGame(config);
            result.record(game);
            return result;
        }

        // Mode Multi-thread
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<GameResult>> futures = new ArrayList<>();

        for (int i = 0; i < config.getNbGames(); i++) {
            futures.add(pool.submit(() -> runner.runGame(config)));
        }

        for (Future<GameResult> f : futures) {
            try {
                result.record(f.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pool.shutdown();
        return result;
    }
}



















