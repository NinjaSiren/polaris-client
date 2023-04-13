/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import polarisdevelopment.polarisclient.gui.GuiThemes;
import polarisdevelopment.polarisclient.settings.Setting;
import polarisdevelopment.polarisclient.systems.Systems;
import polarisdevelopment.polarisclient.systems.commands.Command;
import polarisdevelopment.polarisclient.systems.commands.arguments.ModuleArgumentType;
import polarisdevelopment.polarisclient.systems.hud.Hud;
import polarisdevelopment.polarisclient.systems.modules.Module;
import polarisdevelopment.polarisclient.systems.modules.Modules;
import polarisdevelopment.polarisclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ResetCommand extends Command {

    public ResetCommand() {
        super("reset", "Resets specified settings.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("settings")
                .then(argument("module", ModuleArgumentType.create()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);
                    module.settings.forEach(group -> group.forEach(Setting::reset));
                    module.info("Reset all settings.");
                    return SINGLE_SUCCESS;
                }))
                .then(literal("all").executes(context -> {
                    Modules.get().getAll().forEach(module -> module.settings.forEach(group -> group.forEach(Setting::reset)));
                    ChatUtils.infoPrefix("Modules", "Reset all module settings");
                    return SINGLE_SUCCESS;
                }))
        ).then(literal("gui").executes(context -> {
            GuiThemes.get().clearWindowConfigs();
            ChatUtils.info("Reset GUI positioning.");
            return SINGLE_SUCCESS;
        })).then(literal("bind")
                .then(argument("module", ModuleArgumentType.create()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);

                    module.keybind.set(true, -1);
                    module.info("Reset bind.");

                    return SINGLE_SUCCESS;
                }))
                .then(literal("all").executes(context -> {
                    Modules.get().getAll().forEach(module -> module.keybind.set(true, -1));
                    ChatUtils.infoPrefix("Modules", "Reset all binds.");
                    return SINGLE_SUCCESS;
                }))
        ).then(literal("hud").executes(context -> {
            Systems.get(Hud.class).resetToDefaultElements();
            ChatUtils.infoPrefix("HUD", "Reset all elements.");
            return SINGLE_SUCCESS;
        }));
    }
}
