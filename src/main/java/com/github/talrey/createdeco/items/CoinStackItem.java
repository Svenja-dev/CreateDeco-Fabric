package com.github.talrey.createdeco.items;

import com.github.talrey.createdeco.BlockRegistry;
import com.github.talrey.createdeco.blocks.CoinStackBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public class CoinStackItem extends Item {
  public final String material;

  public CoinStackItem (Settings props) {
    this(props, "iron");
  }

  public CoinStackItem (Settings props, String material) {
    super(props);
    this.material = material;
  }

  protected boolean placeBlock(ItemUsageContext ctx) {
    ItemPlacementContext bictx = new ItemPlacementContext (ctx);
    BlockState target = ctx.getWorld().getBlockState(ctx.getBlockPos());
    if (target.getBlock() instanceof CoinStackBlock && this.material.equals(((CoinStackBlock)target.getBlock()).material) && target.get(Properties.LAYERS) < 8) {
      int height = target.get(Properties.LAYERS);
      if (!ctx.getWorld().isClient()) ctx.getWorld().setBlockState(ctx.getBlockPos(), BlockRegistry.COIN_BLOCKS.get(this.material).getDefaultState()
        .with(Properties.LAYERS, height+1)
        .with(Properties.WATERLOGGED, ctx.getWorld().isWater(ctx.getBlockPos()) && height+1 != 8)
      );
      return true;
    }
    else if (target.canReplace(bictx)) {
      if (!CoinStackBlock.canSurvive(ctx.getWorld(), ctx.getBlockPos())) return false;
      if (!ctx.getWorld().isClient()) ctx.getWorld().setBlockState(ctx.getBlockPos(), BlockRegistry.COIN_BLOCKS.get(this.material).getDefaultState());
      return true;
    }
    else {
      BlockPos offset = ctx.getBlockPos().offset(ctx.getSide());
      target = ctx.getWorld().getBlockState(offset);
      if (target.canReplace(bictx)) {
        if (!CoinStackBlock.canSurvive(ctx.getWorld(), offset)) return false;
        if (!ctx.getWorld().isClient()) ctx.getWorld().setBlockState(
          offset, BlockRegistry.COIN_BLOCKS.get(this.material).getDefaultState()
        );
        return true;
      }
    }
    return false;
  }


  @Override
  public ActionResult useOnBlock (ItemUsageContext ctx) {
    if (placeBlock (ctx)) {
      ctx.getStack().decrement(1);
      ctx.getWorld().playSound(
        null, ctx.getBlockPos(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 1f, 1f
      );
      return ActionResult.SUCCESS;
    }
    return super.useOnBlock(ctx);
  }
}
