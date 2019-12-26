package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import me.zeroeightsix.kami.setting.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import me.zeroeightsix.kami.command.*;
import java.util.*;

@Module.Info(name = "32kDetect", category = Module.Category.COMBAT, description = "Detects when spawnfags have 32ks")
public class Sharp32kDetect extends Module
{
    private Setting<Boolean> watermark;
    private Setting<Boolean> color;
    private Set<EntityPlayer> sword;
    public static final Minecraft mc;
    
    public Sharp32kDetect() {
        this.watermark = this.register(Settings.b("Watermark", true));
        this.color = this.register(Settings.b("Color", false));
        this.sword = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
    }
    
    private boolean is32k(final EntityPlayer player, final ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            final NBTTagList enchants = stack.getEnchantmentTagList();
            if (enchants != null) {
                for (int i = 0; i < enchants.tagCount(); ++i) {
                    if (enchants.getCompoundTagAt(i).getShort("lvl") >= 32767) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        for (final EntityPlayer player : Sharp32kDetect.mc.world.playerEntities) {
            if (player.equals((Object)Sharp32kDetect.mc.player)) {
                continue;
            }
            if (this.is32k(player, player.itemStackMainHand) && !this.sword.contains(player)) {
                if (this.watermark.getValue()) {
                    if (this.color.getValue()) {
                        Command.sendChatMessage("&4" + player.getDisplayNameString() + " is holding a 32k");
                    }
                    else {
                        Command.sendChatMessage(player.getDisplayNameString() + " is holding a 32k");
                    }
                }
                else if (this.color.getValue()) {
                    Command.sendRawChatMessage("&4" + player.getDisplayNameString() + " is holding a 32k");
                }
                else {
                    Command.sendRawChatMessage(player.getDisplayNameString() + " is holding a 32k");
                }
                this.sword.add(player);
            }
            if (!this.sword.contains(player)) {
                continue;
            }
            if (this.is32k(player, player.itemStackMainHand)) {
                continue;
            }
            if (this.watermark.getValue()) {
                if (this.color.getValue()) {
                    Command.sendChatMessage("&2" + player.getDisplayNameString() + " is no longer holding a 32k");
                }
                else {
                    Command.sendChatMessage(player.getDisplayNameString() + " is no longer holding a 32k");
                }
            }
            else if (this.color.getValue()) {
                Command.sendRawChatMessage("&2" + player.getDisplayNameString() + " is no longer holding a 32k");
            }
            else {
                Command.sendRawChatMessage(player.getDisplayNameString() + " is no longer holding a 32k");
            }
            this.sword.remove(player);
        }
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
