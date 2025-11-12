package com.github.talrey.createdeco.blocks;

import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SupportWedgeBlock extends Block implements IWrenchable, Waterloggable {
    public static final DirectionProperty FACING;
    public static final IntProperty ORIENTATION = IntProperty.of("orientation", 1, 4);
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape NORTH_1_AABB;
    protected static final VoxelShape SOUTH_1_AABB;
    protected static final VoxelShape EAST_1_AABB;
    protected static final VoxelShape WEST_1_AABB;
    protected static final VoxelShape NORTH_2_AABB;
    protected static final VoxelShape SOUTH_2_AABB;
    protected static final VoxelShape EAST_2_AABB;
    protected static final VoxelShape WEST_2_AABB;
    protected static final VoxelShape NORTH_3_AABB;
    protected static final VoxelShape SOUTH_3_AABB;
    protected static final VoxelShape EAST_3_AABB;
    protected static final VoxelShape WEST_3_AABB;
    protected static final VoxelShape NORTH_4_AABB;
    protected static final VoxelShape SOUTH_4_AABB;
    protected static final VoxelShape EAST_4_AABB;
    protected static final VoxelShape WEST_4_AABB;


    public SupportWedgeBlock(Settings properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(ORIENTATION, 1).with(WATERLOGGED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, ORIENTATION);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView level, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext context) {
        if (state.get(FACING) == Direction.NORTH) {
            return switch (state.get(ORIENTATION)) {
                default -> NORTH_1_AABB;
                case 2 -> NORTH_2_AABB;
                case 3 -> NORTH_3_AABB;
                case 4 -> NORTH_4_AABB;
            };
        }
        if (state.get(FACING) == Direction.SOUTH) {
            return switch (state.get(ORIENTATION)) {
                default -> SOUTH_1_AABB;
                case 2 -> SOUTH_2_AABB;
                case 3 -> SOUTH_3_AABB;
                case 4 -> SOUTH_4_AABB;
            };
        }
        if (state.get(FACING) == Direction.EAST) {
            return switch (state.get(ORIENTATION)) {
                default -> EAST_1_AABB;
                case 2 -> EAST_2_AABB;
                case 3 -> EAST_3_AABB;
                case 4 -> EAST_4_AABB;
            };
        }
        if (state.get(FACING) == Direction.WEST) {
            return switch (state.get(ORIENTATION)) {
                default -> WEST_1_AABB;
                case 2 -> WEST_2_AABB;
                case 3 -> WEST_3_AABB;
                case 4 -> WEST_4_AABB;
            };
        } else return NORTH_1_AABB;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockState;
        if (!context.canReplaceExisting()) {
            blockState = context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide().getOpposite()));
            if (blockState.isOf(this) && blockState.get(FACING) == context.getSide()) {
                return null;
            }
        }

        blockState = this.getDefaultState();
        WorldView levelReader = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        Direction[] var6 = context.getPlacementDirections();

        for (Direction direction : var6) {
            if (direction.getAxis().isHorizontal()) {


                var location = 1;
                var xPos = context.getHitPos().x - (double) blockPos.getX() - 0.5;
                var yPos = context.getHitPos().y - (double) blockPos.getY() - 0.5;
                var zPos = context.getHitPos().z - (double) blockPos.getZ() - 0.5;
                if (context.getSide() == Direction.NORTH || context.getSide() == Direction.SOUTH) {
                    boolean bottomleft = xPos < -yPos;
                    boolean topleft = xPos < yPos;
                    if (!bottomleft && topleft) location = 1;
                    if (!bottomleft && !topleft) location = 2;
                    if (bottomleft && !topleft) location = 3;
                    if (bottomleft && topleft) location = 4;
                }
                if (context.getSide() == Direction.EAST || context.getSide() == Direction.WEST) {
                    boolean bottomleft = zPos < -yPos;
                    boolean topleft = zPos < yPos;
                    if (!bottomleft && topleft) location = 1;
                    if (!bottomleft && !topleft) location = 2;
                    if (bottomleft && !topleft) location = 3;
                    if (bottomleft && topleft) location = 4;
                }
                if (context.getSide() == Direction.UP) {
                    location = 3;
                }

                blockState = blockState.with(FACING, direction.getOpposite()).with(ORIENTATION, location);
                if (blockState.canPlaceAt(levelReader, blockPos)) {
                    return blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            level.scheduleFluidTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(level));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, level, currentPos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        WATERLOGGED = Properties.WATERLOGGED;
        NORTH_1_AABB = VoxelShapes.union(Block.createCuboidShape(5.0D, 8.0D, 14.0D, 11.0D, 14.0D, 16.0D), Block.createCuboidShape(5.0D, 14.0D, 8.0D, 11.0D, 16.0D, 16.0D));
        NORTH_2_AABB = VoxelShapes.union(Block.createCuboidShape(8.0D, 5.0D, 14.0D, 14.0D, 11.0D, 16.0D), Block.createCuboidShape(14.0D, 5.0D, 8.0D, 16.0D, 11.0D, 16.0D));
        NORTH_3_AABB = VoxelShapes.union(Block.createCuboidShape(5.0D, 2.0D, 14.0D, 11.0D, 8.0D, 16.0D), Block.createCuboidShape(5.0D, 0.0D, 8.0D, 11.0D, 2.0D, 16.0D));
        NORTH_4_AABB = VoxelShapes.union(Block.createCuboidShape(2.0D, 5.0D, 14.0D, 8.0D, 11.0D, 16.0D), Block.createCuboidShape(0.0D, 5.0D, 8.0D, 2.0D, 11.0D, 16.0D));

        SOUTH_1_AABB = VoxelShapes.union(Block.createCuboidShape(5.0D, 8.0D, 0.0D, 11.0D, 14.0D, 2.0D), Block.createCuboidShape(5.0D, 14.0D, 0.0D, 11.0D, 16.0D, 8.0D));
        SOUTH_2_AABB = VoxelShapes.union(Block.createCuboidShape(8.0D, 5.0D, 0.0D, 14.0D, 11.0D, 2.0D), Block.createCuboidShape(14.0D, 5.0D, 0.0D, 16.0D, 11.0D, 8.0D));
        SOUTH_3_AABB = VoxelShapes.union(Block.createCuboidShape(5.0D, 2.0D, 0.0D, 11.0D, 8.0D, 2.0D), Block.createCuboidShape(5.0D, 0.0D, 0.0D, 11.0D, 2.0D, 8.0D));
        SOUTH_4_AABB = VoxelShapes.union(Block.createCuboidShape(2.0D, 5.0D, 0.0D, 8.0D, 11.0D, 2.0D), Block.createCuboidShape(0.0D, 5.0D, 0.0D, 2.0D, 11.0D, 8.0D));

        EAST_1_AABB = VoxelShapes.union(Block.createCuboidShape(0.0D, 8.0D, 5.0D, 2.0D, 14.0D, 11.0D), Block.createCuboidShape(0.0D, 14.0D, 5.0D, 8.0D, 16.0D, 11.0D));
        EAST_2_AABB = VoxelShapes.union(Block.createCuboidShape(0.0D, 5.0D, 8.0D, 2.0D, 11.0D, 14.0D), Block.createCuboidShape(0.0D, 5.0D, 14.0D, 8.0D, 11.0D, 16.0D));
        EAST_3_AABB = VoxelShapes.union(Block.createCuboidShape(0.0D, 2.0D, 5.0D, 2.0D, 8.0D, 11.0D), Block.createCuboidShape(0.0D, 0.0D, 5.0D, 8.0D, 2.0D, 11.0D));
        EAST_4_AABB = VoxelShapes.union(Block.createCuboidShape(0.0D, 5.0D, 2.0D, 2.0D, 11.0D, 8.0D), Block.createCuboidShape(0.0D, 5.0D, 0.0D, 8.0D, 11.0D, 2.0D));

        WEST_1_AABB = VoxelShapes.union(Block.createCuboidShape(14.0D, 8.0D, 5.0D, 16.0D, 14.0D, 11.0D), Block.createCuboidShape(8.0D, 14.0D, 5.0D, 16.0D, 16.0D, 11.0D));
        WEST_2_AABB = VoxelShapes.union(Block.createCuboidShape(14.0D, 5.0D, 8.0D, 16.0D, 11.0D, 14.0D), Block.createCuboidShape(8.0D, 5.0D, 14.0D, 16.0D, 11.0D, 16.0D));
        WEST_3_AABB = VoxelShapes.union(Block.createCuboidShape(14.0D, 2.0D, 5.0D, 16.0D, 8.0D, 11.0D), Block.createCuboidShape(8.0D, 0.0D, 5.0D, 16.0D, 2.0D, 11.0D));
        WEST_4_AABB = VoxelShapes.union(Block.createCuboidShape(14.0D, 5.0D, 2.0D, 16.0D, 11.0D, 8.0D), Block.createCuboidShape(8.0D, 5.0D, 0.0D, 16.0D, 11.0D, 2.0D));
    }
}
