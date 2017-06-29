package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.tileentity.Jukebox;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class RecordInteractionBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Location<World>> optLocation = context.get(Parameters.INTERACTION_LOCATION);
        if (optLocation.isPresent()) {
            final Location<World> location = optLocation.get();
            final Optional<TileEntity> optTile = location.getTileEntity();
            if (optTile.isPresent()) {
                final TileEntity tile = optTile.get();
                if (tile instanceof Jukebox) {
                    final ItemStack itemStack = context.tryGet(Parameters.USED_ITEM_STACK).copy();
                    final ItemStackSnapshot oldSnapshot = itemStack.createSnapshot();
                    itemStack.setQuantity(itemStack.getQuantity() - 1);
                    final ItemStackSnapshot newSnapshot = itemStack.createSnapshot();
                    context.get(Parameters.PLAYER).ifPresent(player -> {
                        if (!player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                            context.get(Parameters.USED_SLOT).ifPresent(slot -> context.addSlotChange(
                                    new SlotTransaction(slot, oldSnapshot, newSnapshot)));
                        }
                    });
                    itemStack.setQuantity(1);
                    ((Jukebox) tile).insertRecord(itemStack);
                    return BehaviorResult.SUCCESS;
                }
            }
        }
        return BehaviorResult.PASS;
    }
}
