package earthrp.events;

import earthrp.customObjects.Army;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArmyMoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    @Getter
    private final Army movedArmy;

    @Getter
    private final Chunk toChunk;

    @Getter
    private final Chunk fromChunk;
    public ArmyMoveEvent(Army movedArmy, Chunk toChunk, Chunk fromChunk){
        this.movedArmy = movedArmy;
        this.toChunk = toChunk;
        this.fromChunk = fromChunk;
    }




    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;

    }
}
