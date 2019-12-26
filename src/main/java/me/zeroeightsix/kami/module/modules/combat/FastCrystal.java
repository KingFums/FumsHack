package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.*;
import me.zeroeightsix.kami.event.events.*;
import me.zero.alpine.listener.*;
import net.minecraft.init.*;
import java.util.function.*;

@Module.Info(name = "FastCrystal", category = Module.Category.COMBAT, description = "Fast Place But For Crystals")
public class FastCrystal extends Module
{
    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener;

    public FastCrystal() {
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (FastCrystal.mc.player != null && (FastCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || FastCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {
                FastCrystal.mc.rightClickDelayTimer = 0;
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }
}
