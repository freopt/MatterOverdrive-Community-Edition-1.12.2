
package matteroverdrive.machines.analyzer;

import matteroverdrive.api.inventory.UpgradeTypes;
import matteroverdrive.api.matter_network.IMatterNetworkClient;
import matteroverdrive.api.matter_network.IMatterNetworkConnection;
import matteroverdrive.api.network.IMatterNetworkDispatcher;
import matteroverdrive.api.transport.IGridNode;
import matteroverdrive.data.Inventory;
import matteroverdrive.data.inventory.MatterSlot;
import matteroverdrive.data.transport.MatterNetwork;
import matteroverdrive.init.MatterOverdriveSounds;
import matteroverdrive.machines.components.ComponentMatterNetworkConfigs;
import matteroverdrive.machines.events.MachineEvent;
import matteroverdrive.matter_network.MatterNetworkTaskQueue;
import matteroverdrive.matter_network.components.MatterNetworkComponentClient;
import matteroverdrive.tile.MOTileEntityMachineEnergy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class TileEntityMachineMatterAnalyzer extends MOTileEntityMachineEnergy implements ISidedInventory, IMatterNetworkClient, IMatterNetworkConnection, IMatterNetworkDispatcher {
    public static final int ENERGY_CAPACITY = 512000;
    private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerUsage, UpgradeTypes.PowerStorage, UpgradeTypes.Speed, UpgradeTypes.PowerStorage, UpgradeTypes.Muffler);
    public int input_slot = 0;
    private ComponentMatterNetworkAnalyzer networkComponent;
    private ComponentMatterNetworkConfigs componentMatterNetworkConfigs;
    private ComponentTaskProcessingAnalyzer taskProcessingComponent;

    public TileEntityMachineMatterAnalyzer() {
        super(4);
        this.energyStorage.setCapacity(ENERGY_CAPACITY);
        this.energyStorage.setMaxExtract(ENERGY_CAPACITY);
        this.energyStorage.setMaxReceive(ENERGY_CAPACITY);
        playerSlotsHotbar = true;
        playerSlotsMain = true;
    }

    @Override
    public BlockPos getPosition() {
        return getPos();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
    	return (oldState.getBlock() != newState.getBlock());
    }

    @Override
    public void RegisterSlots(Inventory inventory) {
        input_slot = inventory.AddSlot(new MatterSlot(true));
        super.RegisterSlots(inventory);
    }

    @Override
    protected void registerComponents() {
        super.registerComponents();
        componentMatterNetworkConfigs = new ComponentMatterNetworkConfigs(this);
        networkComponent = new ComponentMatterNetworkAnalyzer(this);
        taskProcessingComponent = new ComponentTaskProcessingAnalyzer("Tasks", this, 1, 0);
        addComponent(componentMatterNetworkConfigs);
        addComponent(networkComponent);
        addComponent(taskProcessingComponent);
    }

    @Override
    public boolean isAffectedByUpgrade(UpgradeTypes type) {
        return upgradeTypes.contains(type);
    }


    @Override
    public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP) {
            return new int[]{input_slot};
        } else {
            return new int[]{input_slot};
        }
    }


    @Override
    public boolean canConnectFromSide(IBlockState blockState, EnumFacing side) {
        // Allow ANY connection to connect.
        return true;

        // Only allow rear connections to connect.
//        EnumFacing facing = blockState.getValue(MOBlock.PROPERTY_DIRECTION);
//        return facing.getOpposite() == side;
    }

    @Override
    public BlockPos getNodePos() {
        return getPos();
    }

    @Override
    public boolean establishConnectionFromSide(IBlockState blockState, EnumFacing side) {
        return canConnectFromSide(blockState, side);
    }

    @Override
    public void breakConnection(IBlockState blockState, EnumFacing side) {

    }

    @Override
    public MatterNetwork getNetwork() {
        return networkComponent.getNetwork();
    }

    @Override
    public void setNetwork(MatterNetwork network) {
        networkComponent.setNetwork(network);
    }

    @Override
    public World getNodeWorld() {
        return getWorld();
    }

    @Override
    public boolean canConnectToNetworkNode(IBlockState blockState, IGridNode toNode, EnumFacing direction) {
        return networkComponent.canConnectToNetworkNode(blockState, toNode, direction);
    }


    @Override
    protected void onMachineEvent(MachineEvent event) {
        if (event instanceof MachineEvent.ActiveChange) {
            forceSync();
        }
    }


    @Override
    public boolean getServerActive() {
        return taskProcessingComponent.isAnalyzing();
    }

    @Override
    public SoundEvent getSound() {
        return MatterOverdriveSounds.analyzer;
    }

    @Override
    public boolean hasSound() {
        return true;
    }

    @Override
    public float soundVolume() {
        ItemStack stack = this.getStackInSlot(input_slot);

        if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
            return 0.0f;
        }

        return 0.3f;
    }

    public float getProgress() {
        return taskProcessingComponent.getProgress();
    }

    public int getEnergyDrainPerTick() {
        return taskProcessingComponent.getEnergyDrainPerTick();
    }

    public int getEnergyDrainMax() {
        return taskProcessingComponent.getEnergyDrainMax();
    }

    @Override
    public MatterNetworkComponentClient getMatterNetworkComponent() {
        return networkComponent;
    }

    @Override
    public MatterNetworkTaskQueue getTaskQueue(int queueID) {
        return taskProcessingComponent.getTaskQueue();
    }

    @Override
    public int getTaskQueueCount() {
        return 1;
    }
}
