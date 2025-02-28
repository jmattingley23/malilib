package fi.dy.masa.malilib.util.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.util.game.wrap.ItemWrap;

/**
 * A wrapper around ItemStack, that implements hashCode() and equals().
 * Whether or not the NBT data and damage of damageable items are considered by those methods,
 * depends on the ignoreDamage and checkNbt arguments to the constructor.
 */
public class ItemType
{
    protected final ItemStack stack;
    protected final boolean checkNbt;
    protected final boolean ignoreDamage;
    protected final int hashCode;

    public ItemType(ItemStack stack)
    {
        this(stack, true, false, true);
    }

    public ItemType(ItemStack stack, boolean copy, boolean checkNbt)
    {
        this(stack, copy, false, checkNbt);
    }

    public ItemType(ItemStack stack, boolean copy, boolean ignoreDamage, boolean checkNbt)
    {
        this.stack = ItemWrap.isEmpty(stack) ? ItemStack.EMPTY : (copy ? stack.copy() : stack);
        this.ignoreDamage = ignoreDamage;
        this.checkNbt = checkNbt;
        this.hashCode = this.calculateHashCode();
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public boolean getIgnoreDamage()
    {
        return this.ignoreDamage;
    }

    public boolean checkNbt()
    {
        return this.checkNbt;
    }

    @Override
    public int hashCode()
    {
        return this.hashCode;
    }

    protected int calculateHashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.stack.getItem().hashCode();

        if (this.ignoreDamage == false || this.stack.isItemStackDamageable() == false)
        {
            result = prime * result + this.stack.getMetadata();
        }

        if (this.checkNbt())
        {
            NBTTagCompound tag = ItemWrap.getTag(this.stack);
            result = prime * result + (tag != null ? tag.hashCode() : 0);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        ItemType other = (ItemType) obj;

        if (ItemWrap.isEmpty(this.stack) || ItemWrap.isEmpty(other.stack))
        {
            return ItemWrap.isEmpty(this.stack) == ItemWrap.isEmpty(other.stack);
        }
        else
        {
            if (this.stack.getItem() != other.stack.getItem())
            {
                return false;
            }

            if ((this.ignoreDamage == false || this.stack.isItemStackDamageable() == false) &&
                this.stack.getMetadata() != other.stack.getMetadata())
            {
                return false;
            }

            return this.checkNbt() == false || ItemStack.areItemStackTagsEqual(this.stack, other.stack);
        }
    }

    @Override
    public String toString()
    {
        if (this.checkNbt())
        {
            ResourceLocation rl = Item.REGISTRY.getNameForObject(this.stack.getItem());
            return rl.toString() + "@" + this.stack.getMetadata() + ItemWrap.getTag(this.stack);
        }
        else
        {
            ResourceLocation rl = Item.REGISTRY.getNameForObject(this.stack.getItem());
            return rl.toString() + "@" + this.stack.getMetadata();
        }
    }
}
