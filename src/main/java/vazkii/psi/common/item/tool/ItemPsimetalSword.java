/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [06/02/2016, 21:14:38 (GMT)]
 */
package vazkii.psi.common.item.tool;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.RegistryHelper;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.item.ItemCAD;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPsimetalSword extends SwordItem implements IPsimetalTool {

    public ItemPsimetalSword(String name, Item.Properties properties) {
        super(PsiAPI.PSIMETAL_TOOL_MATERIAL, 3, -2.4F, properties);
		RegistryHelper.registerItem(this, name);
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, LivingEntity target, @Nonnull LivingEntity attacker) {
        super.hitEntity(itemstack, target, attacker);

        if (isEnabled(itemstack) && attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;

            PlayerData data = PlayerDataHandler.get(player);
			ItemStack playerCad = PsiAPI.getPlayerCAD(player);

			if(!playerCad.isEmpty()) {
				ItemStack bullet = getBulletInSocket(itemstack, getSelectedSlot(itemstack));
				ItemCAD.cast(player.getEntityWorld(), player, data, bullet, playerCad, 5, 10, 0.05F,
						(SpellContext context) -> {
					context.attackedEntity = target;
					context.tool = itemstack;
						});
			}
		}

		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		if (!isEnabled(stack))
			modifiers.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
		return modifiers;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
        if (damage > stack.getMaxDamage())
            damage = stack.getDamage();
		super.setDamage(stack, damage);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (!isEnabled(stack))
			return 1;
		return super.getDestroySpeed(stack, state);
	}

	@Nonnull
	@Override
	public String getTranslationKey(ItemStack stack) {
		String name = super.getTranslationKey(stack);
        if (!isEnabled(stack))
            name += ".broken";
        return name;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        IPsimetalTool.regen(stack, entityIn, isSelected);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        ITextComponent componentName = ISocketable.getSocketedItemName(stack, "psimisc.none");
	    tooltip.add(new TranslationTextComponent("psimisc.spellSelected", componentName));
    }

    @Override
    public boolean getIsRepairable(ItemStack thisStack, @Nonnull ItemStack material) {
        return IPsimetalTool.isRepairableBy(material) || super.getIsRepairable(thisStack, material);
    }
	
	@Override
	public boolean requiresSneakForSpellSet(ItemStack stack) {
		return false;
	}

}
