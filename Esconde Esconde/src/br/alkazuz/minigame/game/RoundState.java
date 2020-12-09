package br.alkazuz.minigame.game;

public enum RoundState
{
    LOADING("LOADING", 0), 
    AVAILABLE("AVAILABLE", 1), 
    HIDDING("HIDDING", 3), 
    IN_PROGRESS("IN_PROGRESS", 4), 
    FINISHED("FINISHED", 5);
    
    private RoundState(String s, int n) {
    }
}
