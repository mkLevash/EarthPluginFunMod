package earthrp.commands;

import io.papermc.paper.command.brigadier.Commands;

public interface PaperCommand {
    void register(Commands registrar);
}
