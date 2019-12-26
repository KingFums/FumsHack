package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;

@Module.Info(name = "ChinkKick", description = "Chinks don't like 1989.", category = Module.Category.MISC)
public class ChinkKick extends Module {

    private Setting<Boolean> debug = register(Settings.b("Debug", true));

    private static long startTime = 0;

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }
        else {
            if (startTime == 0) startTime = System.currentTimeMillis();
            if (startTime + 10000 <= System.currentTimeMillis()) {
                if (debug.getValue()) {
                    Command.sendChatMessage("&4The chinks have been Thanos snapped!");
                }
                Minecraft.getMinecraft().playerController.connection.sendPacket(new CPacketChatMessage("> 1989 Tiananmen Square [Chink Kick Module]"));
                startTime = System.currentTimeMillis();
            }
        }
    }
}