package earthrp.listeners;

import earthrp.Earth;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import org.bukkit.Chunk;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Set;

public class HologramListener implements Listener {

    public HologramListener(){}

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        long chunkKey = chunk.getChunkKey();
        ServerDatabase db = Earth.getInstance().getDatabase();

        // Проверяем, есть ли задачи для загруженного чанка
        Set<ServerDatabase.PendingTask> tasks = db.getHoloTasks(chunkKey);
        if (tasks == null || tasks.isEmpty()) return;

        // Обрабатываем все отложенные вызовы для этого чанка
        for (ServerDatabase.PendingTask task : tasks) {
            TextDisplay display = Tools.findHologram(chunk,task.holoType());
            if(display == null) continue;
            display.text(Tools.deserialize(task.newValue()));
            if(task.taskType() == ServerDatabase.TaskType.DELETE){display.remove();}
        }
        tasks.clear();
        db.putHoloTasks(chunkKey,tasks);

    }
}
