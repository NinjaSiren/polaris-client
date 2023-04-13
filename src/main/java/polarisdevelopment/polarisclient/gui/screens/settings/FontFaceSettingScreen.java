/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.gui.screens.settings;

import polarisdevelopment.polarisclient.gui.GuiTheme;
import polarisdevelopment.polarisclient.gui.WindowScreen;
import polarisdevelopment.polarisclient.gui.utils.Cell;
import polarisdevelopment.polarisclient.gui.widgets.WLabel;
import polarisdevelopment.polarisclient.gui.widgets.WWidget;
import polarisdevelopment.polarisclient.gui.widgets.containers.WTable;
import polarisdevelopment.polarisclient.gui.widgets.containers.WView;
import polarisdevelopment.polarisclient.gui.widgets.input.WDropdown;
import polarisdevelopment.polarisclient.gui.widgets.input.WTextBox;
import polarisdevelopment.polarisclient.gui.widgets.pressable.WButton;
import polarisdevelopment.polarisclient.renderer.Fonts;
import polarisdevelopment.polarisclient.renderer.text.FontFamily;
import polarisdevelopment.polarisclient.renderer.text.FontInfo;
import polarisdevelopment.polarisclient.settings.FontFaceSetting;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FontFaceSettingScreen extends WindowScreen {
    private final FontFaceSetting setting;

    private WTable table;

    private WTextBox filter;
    private String filterText = "";

    public FontFaceSettingScreen(GuiTheme theme, FontFaceSetting setting) {
        super(theme, "Select Font");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        filter = add(theme.textBox("")).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initTable();
        };

        window.view.hasScrollBar = false;

        enterAction = () -> {
            List<Cell<?>> row = table.getRow(0);
            if (row == null) return;

            WWidget widget = row.get(2).widget();
            if (widget instanceof WButton button) {
                button.action.run();
            }
        };

        WView view = add(theme.view()).expandX().widget();
        view.scrollOnlyWhenMouseOver = false;
        table = view.add(theme.table()).expandX().widget();

        initTable();
    }

    private void initTable() {
        for (FontFamily fontFamily : Fonts.FONT_FAMILIES) {
            String name = fontFamily.getName();

            WLabel item = theme.label(name);
            if (!filterText.isEmpty() && !StringUtils.containsIgnoreCase(name, filterText)) continue;
            table.add(item);

            WDropdown<FontInfo.Type> dropdown = table.add(theme.dropdown(FontInfo.Type.Regular)).right().widget();

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(fontFamily.get(dropdown.get()));
                close();
            };

            table.row();
        }
    }
}
