package io.github.xeonpowder.fabric.rpg.block;

import com.google.common.base.CaseFormat;

import io.github.xeonpowder.fabric.rpg.FabricRPG;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.PlantBlock;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * FabricRPGPlantBlock
 */
public class FabricRPGPlantBlock extends PlantBlock {
    protected boolean isTransparent;
    protected Identifier id;

    public FabricRPGPlantBlock(Settings blockSettings, boolean isTransparent) {
        super(blockSettings);
        this.isTransparent = isTransparent;
    }

    public void registerBlock() {
        Registry.register(Registry.BLOCK, this.getIdentifier(), this);
        Registry.register(Registry.ITEM, this.getIdentifier(),
                new FabricRPGBlockItem<FabricRPGPlantBlock>(this, new Item.Settings().group(FabricRPG.ITEM_GROUP)));
    }

    @Environment(EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return this.isTransparent ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID;
    }

    public String getTranslationKey() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getClass().getSimpleName());
    }

    public static String getTranslationKey(Class<? extends FabricRPGPlantBlock> plantBlockExtended) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, plantBlockExtended.getClass().getSimpleName());
    }

    public Identifier getIdentifier() {
        return this.id;
    }
}