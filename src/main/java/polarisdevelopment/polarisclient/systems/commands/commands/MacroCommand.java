/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import polarisdevelopment.polarisclient.systems.commands.Command;
import polarisdevelopment.polarisclient.systems.commands.arguments.MacroArgumentType;
import polarisdevelopment.polarisclient.systems.macros.Macro;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class MacroCommand extends Command {
    public MacroCommand() {
        super("macro", "Allows you to execute macros.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
            argument("macro", MacroArgumentType.create())
                .executes(context -> {
                    Macro macro = MacroArgumentType.get(context);
                    macro.onAction();
                    return SINGLE_SUCCESS;
                }));
    }
}
