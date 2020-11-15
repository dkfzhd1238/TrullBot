package discord.bot.command;

import java.util.List;

public interface ICommand {
    void handle(CommandContext commandContext);

    String getHelp();

    String getName();

    default List<String> getAliases() {
        return List.of();
    }
}
