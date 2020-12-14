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
        this.round = round;
        this.timer = MinigameConfig.START_TIME;
    }
    
    public String getMessageAndUpdate(int players) {
        if (players == 0) {
            this.timer = MinigameConfig.START_TIME;
            return "";
        }
        --this.timer;
        if (this.timer <= 0 && players < MinigameConfig.MIN_PLAYERS) {
            this.starting = false;
            this.canStart = false;
            this.timer = 60;
        }
        if (players < MinigameConfig.MIN_PLAYERS) {
            this.starting = false;
            this.canStart = false;
            return MinigameConfig.STARTING_WITHOUT_PLAYERS.replace("{1}", Integer.toString(this.timer)).replace("{0}", Integer.toString(MinigameConfig.MIN_PLAYERS - players));
        }
        this.starting = true;
        if (this.timer <= 0) {
            this.canStart = true;
            this.timer = 0;
        }
        if (this.timer == 60) {
        	round.broadcast(String.format("§eA partida come§a em §f%s §esegundos.", timer));
        }
        if (this.timer == 30) {
        	round.broadcast(String.format("§eA partida come§a em §f%s §esegundos.", timer));
        }
        if (this.timer == 20) {
        	round.broadcast(String.format("§eA partida come§a em §f%s §esegundos.", timer));
        }
        if (this.timer == 10) {
        	round.broadcast(String.format("§eA partida come§a em §f%s §esegundos.", timer));
        }
        if (this.timer <= 5 && this.timer > 0) {
        	round.broadcast(String.format("§eA partida come§a em §f%s §esegundos.", timer));
        }
        return MinigameConfig.STARTING_WITH_PLAYERS.replace("{0}", this.timer + "");
    }
}
