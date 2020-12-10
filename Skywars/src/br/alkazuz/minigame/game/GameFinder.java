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
			if (round.state == RoundState.AVAILABLE && round.players.size() < round.maxPlayers()) {
				return;
			}
		}
		if (round != null && main.rounds.stream().filter(r -> r.state == RoundState.AVAILABLE && r.players.size() < round.maxPlayers() ).count() == 0 ) {
			round = null;
			main.createRound(null);
			//System.out.println("geradno outro");
			return;
        }
		if(main.rounds.size() == 0)return;
        int index = 0;
        Round best = null;
        List<Round> all = main.rounds;
        while (round == null && index < 10 && all.size() >0) {
            ++index;
            round = all.get(new Random().nextInt(all.size()));
            if (round != null && round.state == RoundState.AVAILABLE) {
                best = round;
            }
        }
        for (index = 0; best == null && index < 10; best = round) {
            ++index;
            round = all.get(new Random().nextInt(all.size()));
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
