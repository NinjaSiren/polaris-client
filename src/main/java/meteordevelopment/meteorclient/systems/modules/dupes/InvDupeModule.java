/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

/*
 * From Trouser Streak addon by etianl
 */

package meteordevelopment.meteorclient.systems.modules.dupes;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;

public class InvDupeModule extends Module {
    public InvDupeModule() {
        super(Categories.Dupes, "1.17 Inventory Dupe", "InventoryDupe only works on servers with the version 1.17. (Not any version after or before.)");
    }
}
