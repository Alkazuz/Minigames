package br.alkazuz.lobby.api;

public class CommandExecutor
{
    public static void run(String string) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec(string);
        }
        catch (Exception exception) {
            throw new RuntimeException(new StringBuilder().insert(0, "Could not run command '").append(string).append("'").toString(), exception);
        }
    }
}
