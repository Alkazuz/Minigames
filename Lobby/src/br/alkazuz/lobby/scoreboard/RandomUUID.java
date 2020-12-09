package br.alkazuz.lobby.scoreboard;

import java.util.*;

class RandomUUID
{
    public String getUUID() {
        return String.valueOf(String.valueOf(UUID.randomUUID().toString().substring(0, 6))) + UUID.randomUUID().toString().substring(0, 6) + (int)Math.round(Math.random() * 100.0);
    }
}
