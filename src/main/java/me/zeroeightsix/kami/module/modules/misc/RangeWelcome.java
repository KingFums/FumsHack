package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.ArrayList;

@Module.Info(name = "RangeGreet", category = Module.Category.MISC, description = "Greets players when they entered your holy presence.")
public class RangeWelcome extends Module
{
    public ArrayList<String> names;
    public ArrayList<String> names2;
    public ArrayList<String> removal;

    public RangeWelcome() {
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
        Minecraft.getMinecraft().playerController.connection.sendPacket(new CPacketChatMessage("/w " + entityIn.getName() + " Hi! I just cummed near you."));
    }
    
    private enum RangeWelcomeMode
    {
        PRIVATE, 
        PUBLIC;
    }

}
