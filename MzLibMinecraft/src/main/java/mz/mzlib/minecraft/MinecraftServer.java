package mz.mzlib.minecraft;

import mz.mzlib.minecraft.command.CommandManager;
import mz.mzlib.minecraft.command.CommandOutput;
import mz.mzlib.minecraft.datafixer.DataFixerV1400;
import mz.mzlib.minecraft.datafixer.DataUpdaterV_1400;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.incomprehensible.registry.RegistryManagerV1602;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.Instance;
import mz.mzlib.util.Pair;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.StrongRef;
import mz.mzlib.util.async.AsyncFunctionRunner;
import mz.mzlib.util.async.BasicAwait;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperCreator;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.BooleanSupplier;

@WrapMinecraftClass(@VersionName(name="net.minecraft.server.MinecraftServer"))
public interface MinecraftServer extends WrapperObject, CommandOutput, Instance, AsyncFunctionRunner
{
    @WrapperCreator
    static MinecraftServer create(Object wrapped)
    {
        return WrapperObject.create(MinecraftServer.class, wrapped);
    }
    
    MinecraftServer instance = RuntimeUtil.nul();
    
    @WrapMinecraftFieldAccessor(@VersionName(name="serverThread"))
    Thread getThread();
    
    default List<EntityPlayer> getPlayers()
    {
        return this.getPlayerManager().getPlayers();
    }
    
    @WrapMinecraftMethod(@VersionName(name="getPlayerManager"))
    PlayerManager getPlayerManager();
    
    @WrapMinecraftMethod(@VersionName(name="getCommandManager"))
    CommandManager getCommandManager();
    
    @WrapMinecraftMethod(@VersionName(name="tick", end=1400))
    void tickV_1400();
    
    @WrapMinecraftMethod(@VersionName(name="tick", begin=1400))
    void tickV1400(BooleanSupplier booleanSupplier);
    
    Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    
    @Override
    default void schedule(Runnable task)
    {
        tasks.add(task);
    }
    
    StrongRef<Long> tickNumber = new StrongRef<>(0L);
    Queue<Pair<Long, Runnable>> waitingTasks = new PriorityBlockingQueue<>(11, Collections.reverseOrder(Pair.comparingByFirst()));
    
    @Override
    default void schedule(Runnable task, BasicAwait await)
    {
        if(await instanceof SleepTicks)
        {
            if(((SleepTicks)await).ticks==0)
                this.schedule(task);
            else
                waitingTasks.add(new Pair<>(tickNumber.get()+((SleepTicks)await).ticks, task));
        }
        else
            throw new UnsupportedOperationException();
    }
    
    @WrapMinecraftFieldAccessor(@VersionName(name="dataFixer", end=1400))
    DataUpdaterV_1400 getDataUpdaterV_1400();
    
    @WrapMinecraftFieldAccessor(@VersionName(name="dataFixer", begin=1400))
    DataFixerV1400 getDataUpdaterV1400();
    
    /**
     * 1.12.2: 1343
     * 1.13.2: 1631
     * 1.14: 1952
     * 1.19: 3105
     * 1.20.1: 3465
     * 1.20.5: 3837
     */
    int getDataVersion();
    
    @SpecificImpl("getDataVersion")
    @VersionRange(end=1400)
    default int getDataVersionV_1400()
    {
        return this.getDataUpdaterV_1400().getDataVersion();
    }
    
    @SpecificImpl("getDataVersion")
    @VersionRange(begin=1400, end=1800)
    default int getDataVersionV1400_1800()
    {
        return GlobalConstants.getMinecraftVersionV1400_1800().getDataVersion();
    }
    
    @SpecificImpl("getDataVersion")
    @VersionRange(begin=1800)
    default int getDataVersionV1800()
    {
        return GlobalConstants.getMinecraftVersionV1800().getDataVersion().getNumber();
    }
    
    @VersionRange(begin=1602)
    RegistryManagerV1602 getRegistriesV1602();
    
    @SpecificImpl("getRegistriesV1602")
    @WrapMinecraftMethod(@VersionName(name="getRegistryManager", begin=1602, end=1802))
    RegistryManagerV1602 getRegistriesV1602_1802();
    
    @SpecificImpl("getRegistriesV1602")
    @WrapMinecraftMethod(@VersionName(name="getRegistryManager", begin=1802))
    RegistryManagerV1602.Immutable getRegistriesV1802();
}
