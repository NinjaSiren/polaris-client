/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.utils.misc;

import polarisdevelopment.polarisclient.MeteorClient;
import net.minecraft.util.Identifier;

public class MeteorIdentifier extends Identifier {
    public MeteorIdentifier(String path) {
        super(MeteorClient.MOD_ID, path);
    }
}
