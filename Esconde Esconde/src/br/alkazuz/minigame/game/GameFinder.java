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
	int delay = 0;
	public void update() {
		if(main.rounds.size() == 0 && delay < 5) {
			if(delay < 5) {
				main.createRound(null);
			}
			
			delay++;
			return;
		}
		if(round != null) {
			if (round.state == RoundState.AVAILABLE && round.players.size() <= 40 && System.currentTimeMillis() - round.timeLoaded >= 3000L) {
				return;
			}
		}
		if (main.rounds.size() <= 0 || System.currentTimeMillis() - MinigameConfig.STARTED <= 10000) {
			round = null;
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
            if (round != null && round.state == RoundState.AVAILABLE && System.currentTimeMillis() - round.timeLoaded >= 3000L) {
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
            if (round != null && round.state == RoundState.AVAILABLE && System.currentTimeMillis() - round.timeLoaded >= 3000L) {
                best = round;
            }
        }
        round = best;
	}
	
	public static Round getRound() {
		return round;
	}

}
