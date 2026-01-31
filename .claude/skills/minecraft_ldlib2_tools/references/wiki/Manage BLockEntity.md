Manage BlockEntity
Since 2.1.0

By implementing ISyncPersistRPCBlockEntity, you are able to delegate all synchronization and persistence code to LDLib2. You don't need any additional code, just setup syncStorage and annotations.


public class MyBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {
    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    @UpdateListener(methodName = "onIntValueChanged")
    private int intValue = 10;
    @Persisted
    @DescSynced
    @DropSaved
    @RequireRerender
    private ItemStack itemStack = ItemStack.EMPTY;

    public MyBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(...);
    }

    private void onIntValueChanged(int oldValue, int newValue) {
        LDLib2.LOGGER.info("Int value changed from {} to {}", oldValue, newValue);
    }

    @RPCMethod
    public void rpcMsg(String msg) {
    if (level.isClient) { // receive 
        LDLib2.LOGGER.info("Received RPC from server: {}", message);
    } else { // send
        rpcToTracking("rpcMsg", msg)
    }
}
Note

Actually, ISyncPersistRPCBlockEntity is consisted of ISyncBlockEntity, IRPCBlockEntity, IPersistManagedHolder, IBlockEntityManaged. You can choose partial of them if nonly few features are necessary or you want to fine-grained control.

Warning

If you have useAsyncThread() enabled (return ture by default). You have to be careful of thread safe issue. For example, notifyPersistence() will be triggered in a thread. In general, you don't need to worry it, LDLib2 will handle all cases.