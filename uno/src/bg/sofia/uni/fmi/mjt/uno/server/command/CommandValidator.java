package bg.sofia.uni.fmi.mjt.uno.server.command;

import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;

public class CommandValidator {

    public static void validateArgsLength(String[] args, int expectedLength, String usage) {
        if (args == null || args.length != expectedLength) {
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

    public static int extractIntArgument(String[] args, String prefix, String usage) {
        String value = extractArgument(args, prefix, usage);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new CommandExecutionException("Invalid number format for argument: " + prefix + ". Usage: " + usage);
        }
    }

}
