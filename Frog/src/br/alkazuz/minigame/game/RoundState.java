package br.alkazuz.minigame.game;

public enum RoundState
{
    LOADING("LOADING", 0), 
    REMOVING("REMOVING", 1), 
    AVAILABLE("AVAILABLE", 2), 
    IN_PROGRESS("IN_PROGRESS", 3),
    SNOW("SNOW", 3), 
    LOCK("LOCK", 4), 
    FINAL("FINAL", 5), 
    STOP("STOP", 6),
    FINISHED("FINISHED", 7),
    WOOL_WAIT("WOOL_WAIT", 8);
    
    private RoundState(String s, int n) {
    }
}
