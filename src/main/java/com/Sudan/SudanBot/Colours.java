package com.Sudan.SudanBot;

public enum Colours {
    SUCCESS(0x3ff27b),
    ERROR(0xf5433d),
    INFO(0x4287f5);

    public int colour;

    Colours(int colour) {
        this.colour = colour;
    }
}
