/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

import net.minecraft.util.Identifier;

public class PolarisIdentifier extends Identifier {
    public PolarisIdentifier(String path) {
        super("meteor-client", path);
    }
}
