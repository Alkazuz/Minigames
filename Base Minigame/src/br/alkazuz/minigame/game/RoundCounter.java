package br.alkazuz.minigame.game;

public class RoundCounter
{
    public int timer;
    public boolean starting;
    public boolean canStart;
    public Round round;
    
    public RoundCounter(Round round) {
        this.starting = false;
        this.canStart = false;
        this.timer = MinigameConfig.START_TIME;
        this.round = round;
    }
    
    public String getMessageAndUpdate(int players) {
        if (this.round.state == RoundState.IN_PROGRESS) {
           // String txt = MinigameConfig.BOSSBAR.replace("&", "§").replace("{0}", String.valueOf(this.round.rodada.get())).replace("{1}", Methods.formatHHMMSS(this.timer));
            //return txt;
        }
        if (this.round.state != RoundState.AVAILABLE) {
            return "";
        }
        if (players == 0) {
            this.timer = MinigameConfig.START_TIME;
            return "";
        }
        if (this.timer <= 0 && players < MinigameConfig.MIN_PLAYERS) {
            this.starting = false;
            this.canStart = false;
            this.timer = 60;
        }
        if(timer <= 0 && starting) {
        	canStart = true;
        	starting = false;
        }
        if (players >= MinigameConfig.MIN_PLAYERS) {
        	if(!starting && players >= MinigameConfig.MIN_PLAYERS * 3) {
        		timer = 5;
        		this.starting = true;
            	
                return MinigameConfig.STARTING_WITHOUT_PLAYERS.replace("{1}", String.valueOf(this.timer)).replace("{0}", String.valueOf(MinigameConfig.MIN_PLAYERS - players));
        	}
        	if(!starting) {
        		timer = 60;
        	}
        	this.starting = true;
        	
            return MinigameConfig.STARTING_WITH_PLAYERS.replace("{0}", String.valueOf(this.timer));
        }
        if (players < MinigameConfig.MIN_PLAYERS) {
        	
            this.starting = false;
            this.canStart = false;
            return MinigameConfig.STARTING_WITHOUT_PLAYERS.replace("{1}", String.valueOf(this.timer)).replace("{0}", String.valueOf(MinigameConfig.MIN_PLAYERS - players));
        }
        this.starting = true;
        if (this.timer <= 0) {
            this.canStart = true;
            this.timer = 0;
        }
        if (this.timer == 60) {
        	round.broadcast(String.format("§eA partida começa em §f%s §esegundos.", String.valueOf(timer)));
        }
        if (this.timer == 30) {
        	round.broadcast(String.format("§eA partida começa em §f%s §esegundos.", String.valueOf(timer)));
        }
        if (this.timer == 20) {
        	round.broadcast(String.format("§eA partida começa em §f%s §esegundos.", String.valueOf(timer)));
        }
        if (this.timer == 10) {
        	round.broadcast(String.format("§eA partida começa em §f%s §esegundos.", String.valueOf(timer)));
        }
        if (this.timer <= 5 && this.timer > 0) {
        	round.broadcast(String.format("§eA partida começa em §f%s §esegundos.", String.valueOf(timer)));
        }
        return MinigameConfig.STARTING_WITH_PLAYERS.replace("{0}", String.valueOf(this.timer));
    }
}
