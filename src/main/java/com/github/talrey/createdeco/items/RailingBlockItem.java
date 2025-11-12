package com.github.talrey.createdeco.items;

import com.github.talrey.createdeco.BlockRegistry;
import com.github.talrey.createdeco.blocks.CatwalkRailingBlock;
import com.github.talrey.createdeco.blocks.CatwalkStairBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.function.Predicate;

public class RailingBlockItem extends BlockItem {
  private final int placementHelperID;

  public RailingBlockItem (CatwalkRailingBlock block, Properties props) {
    super(block, props);
    placementHelperID = PlacementHelpers.register(new RailingHelper());
  }

  @Override
  public ActionResult useOnBlock (ItemUsageContext ctx) {
    BlockPos pos    = ctx.getBlockPos();
    Direction face  = ctx.getSide();
    World level     = ctx.getWorld();
    PlayerEntity player   = ctx.getPlayer();
    ItemStack stack = ctx.getStack();

    BlockState state        = level.getBlockState(pos);
    IPlacementHelper helper = PlacementHelpers.get(placementHelperID);
    BlockHitResult ray = new BlockHitResult(ctx.getHitPos(), face, pos, true);

    if (player == null) return ActionResult.PASS;

    if (state.getBlock() instanceof CatwalkStairBlock catstair
     && this.getBlock().equals(BlockRegistry.CATWALK_RAILINGS.get(catstair.metal))
    ) {
      // ADD `if ([stack != state]) return;` CHECK
      var dir = state.get(Properties.HORIZONTAL_FACING);
      var xPos = ctx.getHitPos().x - (double) pos.getX() - 0.5;
      var zPos = ctx.getHitPos().z - (double) pos.getZ() - 0.5;
      boolean left = false;

      if (dir == Direction.NORTH) left = xPos > 0;
      if (dir == Direction.SOUTH) left = xPos < 0;
      if (dir == Direction.EAST) left = zPos > 0;
      if (dir == Direction.WEST) left = zPos < 0;


      if (state.get(left ? CatwalkStairBlock.RAILING_LEFT : CatwalkStairBlock.RAILING_RIGHT)) return ActionResult.PASS;
      var soundType = state.getSoundType();
      level.setBlockState(pos, state.with(left ? CatwalkStairBlock.RAILING_LEFT : CatwalkStairBlock.RAILING_RIGHT, true), 3);
      level.playSound(player, pos, this.getPlaceSound(state), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
      level.emitGameEvent(player, GameEvent.BLOCK_PLACE, pos);
      if (!player.getAbilities().creativeMode) {
        stack.decrement(1);
      }
      return ActionResult.SUCCESS;
    }

    PlacementOffset offset = null;
    if (helper.matchesState(state)) {
      offset = helper.getOffset(player, level, state, pos, ray);
      //return offset.placeInWorld(world, this, player, ctx.getHand(), ray);
    }

    if (offset != null && offset.isSuccessful() && !player.isSneaking() //&& railMatchTest
    ) {
      state = offset.getGhostState(); //level.getBlockState(offset.getBlockPos());
      var offsetPos = offset.getBlockPos();
      var soundType = state.getSoundType();

      level.setBlockState(offsetPos, offset.getTransform().apply(state), 3);
      level.playSound(player, offsetPos, this.getPlaceSound(state), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
      level.emitGameEvent(player, GameEvent.BLOCK_PLACE, offsetPos);
      if (!player.getAbilities().creativeMode) {
        stack.decrement(1);
      }
      return ActionResult.SUCCESS;
    }

    return super.useOnBlock(ctx);
  }

  @MethodsReturnNonnullByDefault
  public static class RailingHelper implements IPlacementHelper {
    @Override
    public Predicate<ItemStack> getItemPredicate () {
      return CatwalkRailingBlock::isRailing;
    }

    @Override
    public Predicate<BlockState> getStatePredicate () {
      return state -> true; //CatwalkRailingBlock.isRailing(state.getBlock());
    }

    @Override
    public PlacementOffset getOffset (
      PlayerEntity player, World world, BlockState state, BlockPos pos, BlockHitResult ray
    ) {
      Direction face = ray.getDirection();
      BlockState adjacent = world.getBlockState(pos.offset(face));
      if (CatwalkRailingBlock.isRailing(adjacent.getBlock())) {
        pos = pos.offset(face);
        state = adjacent;
      }

      boolean railMatchTest = player.isHolding(state.getBlock().asItem());

      if (!CatwalkRailingBlock.isRailing(state.getBlock()) ||
              (state.get(CatwalkRailingBlock.NORTH_FENCE)
              && state.get(CatwalkRailingBlock.SOUTH_FENCE)
              && state.get(CatwalkRailingBlock.EAST_FENCE)
              && state.get(CatwalkRailingBlock.WEST_FENCE)) || !railMatchTest) {
        return PlacementOffset.fail();
      }

      List<Direction> dirs = IPlacementHelper.orderedByDistanceExceptAxis(
         pos, ray.getLocation(), Direction.Axis.Y
      );
      for (Direction offset : dirs) {
        if (!state.get(CatwalkRailingBlock.fromDirection(offset))) {
          state = state.with(CatwalkRailingBlock.fromDirection(offset), true);
          break;
        }
      }

      BlockState finalState = state;
      return PlacementOffset.success(pos, newState -> finalState).withGhostState(finalState);
    }
  }
}
