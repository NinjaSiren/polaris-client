/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import polarisdevelopment.polarisclient.renderer.Fonts;
import polarisdevelopment.polarisclient.systems.Systems;
import polarisdevelopment.polarisclient.systems.commands.Command;
import polarisdevelopment.polarisclient.systems.friends.Friend;
import polarisdevelopment.polarisclient.systems.friends.Friends;
import polarisdevelopment.polarisclient.utils.network.Capes;
import polarisdevelopment.polarisclient.utils.network.MeteorExecutor;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reloads many systems.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            warning("Reloading systems, this may take a while.");

            Systems.load();
            Capes.init();
            Fonts.refresh();
            MeteorExecutor.execute(() -> Friends.get().forEach(Friend::updateInfo));

            return SINGLE_SUCCESS;
        });
    }
}
