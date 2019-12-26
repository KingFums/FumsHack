package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import java.util.Random;

@Module.Info(name = "PopbobS3xDupe", description = "Automatically does the popbob sex dupe!", category = Module.Category.PLAYER)
public class PopbobS3xDupe extends Module {


    private static long startTime = 0;

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }

        else {
            if (startTime == 0) startTime = System.currentTimeMillis();
            if (startTime + 16000 <= System.currentTimeMillis()) {
                Minecraft.getMinecraft().playerController.connection.sendPacket(new CPacketChatMessage("I just completed the popbob sex dupe and got " + new Random().nextInt(200) + " new shulkers!"));
                startTime = System.currentTimeMillis();
            }
        }
    }
}