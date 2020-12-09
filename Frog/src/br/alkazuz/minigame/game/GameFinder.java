package br.alkazuz.minigame.game;

import java.util.List;
import java.util.Random;

import br.alkazuz.minigame.main.Main;

public class GameFinder{

	public Main main;
	
	public GameFinder(Main main) {
		this.main = main;
	}
	
	private static Round round;
	public void update() {
		if(round != null) {
			if (round.state == RoundState.AVAILABLE && round.players.size() <= MinigameConfig.MAX_PLAYERS) {
				return;
			}
		}
		if (main.rounds.stream().filter(r -> r.state == RoundState.AVAILABLE && r.players.size() < MinigameConfig.MAX_PLAYERS).count() == 0 ) {
			round = null;
			main.createRound(null);
			return;
        }
        int index = 0;
        Round best = null;
        List<Round> all = main.rounds;
        while (round == null && index < 10) {
            ++index;
            round = all.get(new Random().nextInt(all.size()));
            if(!round.isValidWorld()) {
            	round.state = RoundState.FINISHED;
            	round.cleanUp();
            }
            if (round != null && round.state == RoundState.AVAILABLE) {
                best = round;
            }
        }
        for (index = 0; best == null && index < 10; best = round) {
            ++index;
            round = all.get(new Random().nextInt(all.size()));
            if(!round.isValidWorld()) {
            	round.state = RoundState.FINISHED;
            	round.cleanUp();
            }
            if (round != null && round.state == RoundState.AVAILABLE) {
                best = round;
            }
        }
        round = best;
	}
	
	public static Round getRound() {
		return round;
	}

}
