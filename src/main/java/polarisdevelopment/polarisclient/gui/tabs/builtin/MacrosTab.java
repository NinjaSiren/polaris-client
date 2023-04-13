/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.gui.tabs.builtin;

import polarisdevelopment.polarisclient.gui.GuiTheme;
import polarisdevelopment.polarisclient.gui.renderer.GuiRenderer;
import polarisdevelopment.polarisclient.gui.screens.EditSystemScreen;
import polarisdevelopment.polarisclient.gui.tabs.Tab;
import polarisdevelopment.polarisclient.gui.tabs.TabScreen;
import polarisdevelopment.polarisclient.gui.tabs.WindowTabScreen;
import polarisdevelopment.polarisclient.gui.widgets.containers.WTable;
import polarisdevelopment.polarisclient.gui.widgets.pressable.WButton;
import polarisdevelopment.polarisclient.gui.widgets.pressable.WMinus;
import polarisdevelopment.polarisclient.settings.Settings;
import polarisdevelopment.polarisclient.systems.macros.Macro;
import polarisdevelopment.polarisclient.systems.macros.Macros;
import polarisdevelopment.polarisclient.utils.misc.NbtUtils;
import net.minecraft.client.gui.screen.Screen;
import polarisdevelopment.polarisclient.MeteorClient;

public class MacrosTab extends Tab {
    public MacrosTab() {
        super("Macros");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new MacrosScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof MacrosScreen;
    }

    private static class MacrosScreen extends WindowTabScreen {
        public MacrosScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            add(theme.horizontalSeparator()).expandX();

            WButton create = add(theme.button("Create")).expandX().widget();
            create.action = () -> MeteorClient.mc.setScreen(new EditMacroScreen(theme, null, this::reload));
        }

        private void initTable(WTable table) {
            table.clear();
            if (Macros.get().isEmpty()) return;

            for (Macro macro : Macros.get()) {
                table.add(theme.label(macro.name.get() + " (" + macro.keybind.get() + ")"));

                WButton edit = table.add(theme.button(GuiRenderer.EDIT)).expandCellX().right().widget();
                edit.action = () -> MeteorClient.mc.setScreen(new EditMacroScreen(theme, macro, this::reload));

                WMinus remove = table.add(theme.minus()).widget();
                remove.action = () -> {
                    Macros.get().remove(macro);
                    reload();
                };

                table.row();
            }
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(Macros.get());
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(Macros.get());
        }
    }

    private static class EditMacroScreen extends EditSystemScreen<Macro> {
        public EditMacroScreen(GuiTheme theme, Macro value, Runnable reload) {
            super(theme, value, reload);
        }

        @Override
        public Macro create() {
            return new Macro();
        }

        @Override
        public boolean save() {
            if (value.name.get().isBlank()
                || value.messages.get().isEmpty()
                || !value.keybind.get().isSet()
            ) return false;

            if (isNew) {
                for (Macro m : Macros.get()) {
                    if (value.equals(m)) return false;
                }
            }

            if (isNew) Macros.get().add(value);
            else Macros.get().save();

            return true;
        }

        @Override
        public Settings getSettings() {
            return value.settings;
        }
    }
}
