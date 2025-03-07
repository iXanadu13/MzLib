package mz.mzlib.minecraft.command.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.util.wrapper.WrapClassForName;
import mz.mzlib.util.wrapper.WrapFieldAccessor;
import mz.mzlib.util.wrapper.WrapperCreator;
import mz.mzlib.util.wrapper.WrapperObject;

@VersionRange(begin=1300)
@WrapClassForName("com.mojang.brigadier.CommandDispatcher")
public interface CommandDispatcherV1300 extends WrapperObject
{
    @WrapperCreator
    static CommandDispatcherV1300 create(Object wrapped)
    {
        return WrapperObject.create(CommandDispatcherV1300.class, wrapped);
    }
    
    @Override
    CommandDispatcher<?> getWrapped();
    
    @WrapFieldAccessor("root")
    CommandNodeV1300 getRoot();
}
