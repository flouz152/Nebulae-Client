package net.minecraft.util;

import events.EventManager;
import events.impl.player.EventInput;
import net.minecraft.client.GameSettings;

public class MovementInputFromOptions extends MovementInput {
// leaked by itskekoff; discord.gg/sk3d 7Q8l7Giu
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void tickMovement(boolean isSneak) {
        this.forwardKeyDown = this.gameSettings.keyBindForward.isKeyDown();
        this.backKeyDown = this.gameSettings.keyBindBack.isKeyDown();
        this.leftKeyDown = this.gameSettings.keyBindLeft.isKeyDown();
        this.rightKeyDown = this.gameSettings.keyBindRight.isKeyDown();
        this.moveForward = this.forwardKeyDown == this.backKeyDown ? 0.0F : (this.forwardKeyDown ? 1.0F : -1.0F);
        this.moveStrafe = this.leftKeyDown == this.rightKeyDown ? 0.0F : (this.leftKeyDown ? 1.0F : -1.0F);
        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneaking = this.gameSettings.keyBindSneak.isKeyDown();
       final EventInput event = new EventInput(moveForward, moveStrafe, jump, sneaking, 0.3D);
        EventManager.call(event);


        final double sneakMultiplier = event.getSneakSlow();
        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();
        this.jump = event.isJump();
        this.sneaking = event.isSneak();

        if (isSneak) {
            this.moveStrafe = (float)((double)this.moveStrafe * sneakMultiplier);
            this.moveForward = (float)((double)this.moveForward * sneakMultiplier);
        }
    }
}
