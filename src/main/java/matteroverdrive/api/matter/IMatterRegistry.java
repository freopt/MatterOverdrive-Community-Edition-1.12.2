
package matteroverdrive.api.matter;

import matteroverdrive.data.matter.IMatterEntryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Simeon on 7/20/2015.
 * This is the Matter Registry, where all matter values for Items are stored.
 * The main implementation of this is {@link matteroverdrive.handler.MatterRegistry}.
 */
public interface IMatterRegistry {
    /**
     * Used to get matter for a specific Item stack.
     *
     * @param itemStack the item stack.
     * @return the amount of matter the item stack contains.
     * Returns 0 if the stack does not contain matter.
     */
    int getMatter(ItemStack itemStack);

    /**
     * Used to get matter for specified ore.
     *
     * @param ore the ore dictionary entry
     * @return the matter amount.
     */
    int getMatterOre(String ore);

    /**
     * Used to register handlers for a given Item
     *
     * @param item    the item key.
     * @param handler the handler.
     * @return the Matter Entry associated with that item. It holds all the Handlers.
     */
    IMatterEntry<?, ?> register(Item item, IMatterEntryHandler<?> handler);

    /**
     * Acts the same way as {@link #register(Item, IMatterEntryHandler)} but for ores.
     *
     * @param ore     the ore dictionary entry.
     * @param handler the handler.
     * @return the Matter Entry associated with that ore. It holds all the Handlers.
     */
    IMatterEntry<?, ?> registerOre(String ore, IMatterEntryHandler<?> handler);

    /**
     * Calculates the matter contained in an item based on it's recipe.
     * This is where the blacklist comes.
     * When an item in the recipe is blacklisted or does not contain any matter.
     * The calculation will stop and return 0.
     *
     * @param item the item being calculated
     * @return the matter contained in the calculated item based on its recipe.
     */
    int getMatterFromRecipe(ItemStack item);

    /**
     * This add a whole mod the the blacklist.
     * All items from that mod are treated as blacklisted.
     *
     * @param modID the ID of the blacklisted mod.
     */
    void addModToBlacklist(String modID);
}
