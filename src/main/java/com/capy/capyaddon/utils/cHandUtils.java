package com.capy.capyaddon.utils;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;

public class cHandUtils {
    private static boolean mainSwinging = false;
    private float mainProgress = 0;

    private boolean offSwinging = false;
    private float offProgress = 0;

    public void startSwing(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            if (!mainSwinging) {
                mainProgress = 0;
                mainSwinging = true;
            }
        } else {
            if (!offSwinging) {
                offProgress = 0;
                offSwinging = true;
            }
        }
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (mainSwinging) {
            if (mainProgress >= 1) {
                mainSwinging = false;
                mainProgress = 0;
            } else {
                mainProgress += (float) event.frameTime;
            }
        }

        if (offSwinging) {
            if (offProgress >= 1) {
                offSwinging = false;
                offProgress = 0;
            } else {
                offProgress += (float) (event.frameTime * 1);
            }
        }
    }

    public float getSwing(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return 0 + (1) * mainProgress;
        }
        return 0 + (1) * offProgress;
    }

    public float getY(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return (0 + (0) * mainProgress) / -10f;
        }
        return (0 + (0) * offProgress) / -10f;
    }
}
