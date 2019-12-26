package me.zeroeightsix.kami.module.modules.player;

import net.minecraft.item.ItemFlintAndSteel;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "FastFlint&Steel", category = Module.Category.PLAYER)
public class FastFlint extends Module
{
    @Override
    public void onUpdate() {
        if (FastFlint.mc.player.getHeldItemMainhand().getItem() instanceof ItemFlintAndSteel) {
            FastFlint.mc.rightClickDelayTimer = 0;
        }
    }
}
