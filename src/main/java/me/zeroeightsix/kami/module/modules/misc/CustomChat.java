package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

@Module.Info(name = "FumsChat", category = Module.Category.MISC, description = "Adds a watermark suffix to your messages in chat.")
public class CustomChat extends Module {

    private Setting<Boolean> greentext = register(Settings.b("Greentext", false));


    private final String KAMI_SUFFIX = " \u2B43 \u0493\u222A\u03FB\uA1D9 \u2C67\u2A5C\u1455\u2C69";

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/"))
                return;
            else if (s.startsWith(","))
                return;
            else if (s.startsWith("."))
                return;
            else if (s.startsWith("-"))
                return;
            else if (s.startsWith("="))
                return;
            else if (s.startsWith("#"))
                return;
            else if (s.startsWith("-"))
                return;
            else if (s.startsWith(";"))
                return;
            else if (s.startsWith("'"))
                return;

            else if (greentext.getValue())
                s = "> " + s + KAMI_SUFFIX;
            else if (!greentext.getValue())
                s += KAMI_SUFFIX;

            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

}
