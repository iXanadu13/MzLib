package mz.mzlib.minecraft;

import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.Instance;
import mz.mzlib.util.Pair;
import mz.mzlib.util.RuntimeUtil;
import mz.mzlib.util.StrongRef;
import mz.mzlib.util.async.AsyncFunctionRunner;
import mz.mzlib.util.async.BasicAwait;
import mz.mzlib.util.wrapper.WrapperObject;
import mz.mzlib.util.wrapper.WrapperCreator;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.BooleanSupplier;

@WrapMinecraftClass(@VersionName(name = "net.minecraft.server.MinecraftServer"))
public interface MinecraftServer extends WrapperObject, Instance, AsyncFunctionRunner
{
    @WrapperCreator
    static MinecraftServer create(Object wrapped)
    {
        return WrapperObject.create(MinecraftServer.class, wrapped);
    }

    MinecraftServer instance = RuntimeUtil.nul();

    default List<EntityPlayer> getPlayers()
    {
        return this.getPlayerManager().getPlayers();
    }

    @WrapMinecraftMethod(@VersionName(name = "getPlayerManager"))
    PlayerManager getPlayerManager();

    @WrapMinecraftMethod(@VersionName(name="tick", end=1400))
    void tickV_1400();
    @WrapMinecraftMethod(@VersionName(name="tick", begin=1400))
    void tickV1400(BooleanSupplier booleanSupplier);

    Queue<Runnable> tasks=new ConcurrentLinkedQueue<>();
    @Override
    default void schedule(Runnable function)
    {
        tasks.add(function);
    }

    StrongRef<Long> tickNumber =new StrongRef<>(0L);
    Queue<Pair<Long, Runnable>> watingTasks=new PriorityBlockingQueue<>(11, Collections.reverseOrder(Pair.comparingByFirst()));
    @Override
    default void schedule(Runnable function, BasicAwait await)
    {
        if(await instanceof SleepTicks)
        {
            if(((SleepTicks) await).ticks==0)
                this.schedule(function);
            else
                watingTasks.add(new Pair<>(tickNumber.get()+((SleepTicks) await).ticks, function));
        }
        else
            throw new UnsupportedOperationException();
    }
}