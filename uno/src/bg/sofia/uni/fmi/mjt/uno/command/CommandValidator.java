package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;

public class CommandValidator {

    public static void validateArgsLength(String[] args, int expectedLength, String usage) {
        if (args.length != expectedLength) {
            throw new CommandExecutionException("Invalid arguments. Usage: " + usage);
        }
    }

    public static String extractArgument(String[] args, String prefix, String usage) {
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        throw new CommandExecutionException("Missing required argument. Usage: " + usage);
    }
}
