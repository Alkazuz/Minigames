package br.alkazuz.minigame.game;

public enum RoundState
{
    LOADING("LOADING", 0), 
    AVAILABLE("AVAILABLE", 1), 
    IN_PROGRESS("IN_PROGRESS", 2), 
    FINISHED("FINISHED", 3);
    
    private RoundState(String s, int n) {
    }
}
