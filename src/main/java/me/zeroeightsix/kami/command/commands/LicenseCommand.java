package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.Command;

/**
 * Created by S-B99 on 01/12/2019.
 */

public class LicenseCommand extends Command {

    public LicenseCommand() {
        super("license", null);
        setDescription("Prints KAMI Blue's license");
    }

    @Override
    public void call(String[] args) {
        Command.sendChatMessage("It's backdoored against people who should have it by checking UUID's. Have fun!");
    }
}
