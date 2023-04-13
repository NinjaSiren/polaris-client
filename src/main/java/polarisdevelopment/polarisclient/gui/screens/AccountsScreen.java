/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.gui.screens;

import polarisdevelopment.polarisclient.gui.GuiTheme;
import polarisdevelopment.polarisclient.gui.WindowScreen;
import polarisdevelopment.polarisclient.gui.widgets.WAccount;
import polarisdevelopment.polarisclient.gui.widgets.containers.WContainer;
import polarisdevelopment.polarisclient.gui.widgets.containers.WHorizontalList;
import polarisdevelopment.polarisclient.gui.widgets.pressable.WButton;
import polarisdevelopment.polarisclient.systems.accounts.Account;
import polarisdevelopment.polarisclient.systems.accounts.Accounts;
import polarisdevelopment.polarisclient.systems.accounts.MicrosoftLogin;
import polarisdevelopment.polarisclient.systems.accounts.types.MicrosoftAccount;
import polarisdevelopment.polarisclient.utils.misc.NbtUtils;
import polarisdevelopment.polarisclient.utils.network.MeteorExecutor;
import org.jetbrains.annotations.Nullable;
import polarisdevelopment.polarisclient.MeteorClient;

public class AccountsScreen extends WindowScreen {
    public AccountsScreen(GuiTheme theme) {
        super(theme, "Accounts");
    }

    @Override
    public void initWidgets() {
        // Accounts
        for (Account<?> account : Accounts.get()) {
            WAccount wAccount = add(theme.account(this, account)).expandX().widget();
            wAccount.refreshScreenAction = this::reload;
        }

        // Add account
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        addButton(l, "Cracked", () -> MeteorClient.mc.setScreen(new AddCrackedAccountScreen(theme, this)));
        addButton(l, "Altening", () -> MeteorClient.mc.setScreen(new AddAlteningAccountScreen(theme, this)));
        addButton(l, "Microsoft", () -> {
            locked = true;

            MicrosoftLogin.getRefreshToken(refreshToken -> {
                locked = false;

                if (refreshToken != null) {
                    MicrosoftAccount account = new MicrosoftAccount(refreshToken);
                    addAccount(null, this, account);
                }
            });
        });
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(@Nullable AddAccountScreen screen, AccountsScreen parent, Account<?> account) {
        if (screen != null) screen.locked = true;

        MeteorExecutor.execute(() -> {
            if (account.fetchInfo()) {
                account.getCache().loadHead();

                Accounts.get().add(account);
                if (account.login()) Accounts.get().save();

                if (screen != null) {
                    screen.locked = false;
                    screen.close();
                }

                parent.reload();

                return;
            }

            if (screen != null) screen.locked = false;
        });
    }

    @Override
    public boolean toClipboard() {
        return NbtUtils.toClipboard(Accounts.get());
    }

    @Override
    public boolean fromClipboard() {
        return NbtUtils.fromClipboard(Accounts.get());
    }
}
