package com.wildfire.render.armor;

import com.wildfire.api.IGenderArmor;

public class EmptyGenderArmor implements IGenderArmor {

    public static final EmptyGenderArmor INSTANCE = new EmptyGenderArmor();

    private EmptyGenderArmor() {
    }

    @Override
    public boolean coversBreasts() {
        return false;
    }
}