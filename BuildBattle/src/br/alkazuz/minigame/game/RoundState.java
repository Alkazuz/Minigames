package br.alkazuz.minigame.game;

public enum RoundState
{
    LOADING("LOADING", 0), 
    AVAILABLE("AVAILABLE", 1), 
    CHOOSING("CHOOSING", 2), 
    IN_PROGRESS("BUILDING", 3), 
    VOTING("VOTING", 4), 
    VOTING_NEXT("VOTING_NEXT", 4), 
    ENDING("ENDING", 4), 
    END("END", 4), 
    FINISHED("FINISHED", 5);
    
    private RoundState(String s, int n) {
    }
}
