package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.*;
import java.util.*;
import me.zeroeightsix.kami.event.events.*;
import net.minecraft.client.*;
import me.zeroeightsix.kami.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.network.play.client.CPacketChatMessage;

@Module.Info(name = "RangeAnnouncer", category = Module.Category.MISC, description = "Announces in chat when a player gets in render distance.")
public class RangeAnnouncer extends Module
{
    public ArrayList<String> names;
    public ArrayList<String> names2;
    public ArrayList<String> removal;
    
    public RangeAnnouncer() {
        this.names = new ArrayList<String>();
        this.names2 = new ArrayList<String>();
        this.removal = new ArrayList<String>();
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        this.names2.clear();
        Minecraft.getMinecraft().world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter(entity -> !EntityUtil.isFakeLocalPlayer(entity)).filter(entity -> entity instanceof EntityPlayer).filter(entity -> !(entity instanceof EntityPlayerSP)).forEach(this::testName);
        this.testLeave();
    }
    
    private void testName(final Entity entityIn) {
        this.names2.add(entityIn.getName());
        if (!this.names.contains(entityIn.getName())) {
            this.sendMessage(entityIn);
            this.names.add(entityIn.getName());
        }
    }
    
    private void testLeave() {
        this.names.forEach(name -> {
            if (!this.names2.contains(name)) {
                this.removal.add(name);
            }
            return;
        });
        this.removal.forEach(name -> this.names.remove(name));
        this.removal.clear();
    }
    
    private void sendMessage(final Entity entityIn) {
        Minecraft.getMinecraft().playerController.connection.sendPacket(new CPacketChatMessage(entityIn.getName() + " came into my render distance!"));
    }
    
    private enum RangeAnnouncerMode
    {
        PRIVATE, 
        PUBLIC;
    }

}
