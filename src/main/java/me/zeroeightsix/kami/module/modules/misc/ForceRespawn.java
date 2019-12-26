package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.*;
import net.minecraft.client.gui.*;

@Module.Info(name = "ForceRespawn", category = Module.Category.MISC)
public class ForceRespawn extends Module
{
    @Override
    public void onUpdate() {
        if (this.isEnabled() && ForceRespawn.mc.currentScreen instanceof GuiGameOver) {
            ForceRespawn.mc.player.respawnPlayer();
            ForceRespawn.mc.displayGuiScreen((GuiScreen)null);
        }
    }
}
