package mz.mzlib.minecraft.entity;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.entity.data.EntityDataType;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.wrapper.WrapperCreator;
import mz.mzlib.util.wrapper.WrapperObject;

@WrapMinecraftClass(@VersionName(name="net.minecraft.entity.ItemEntity"))
public interface EntityItem extends WrapperObject, Entity
{
    @WrapperCreator
    static EntityItem create(Object wrapped)
    {
        return WrapperObject.create(EntityItem.class, wrapped);
    }
    
    EntityType ENTITY_TYPE = EntityType.fromId(Identifier.ofMinecraft("item"));
    
    /**
     * type: {@link mz.mzlib.minecraft.item.ItemStack}
     */
    static EntityDataType dataTypeItem()
    {
        return create(null).staticDataTypeItem();
    }
    @WrapMinecraftFieldAccessor(@VersionName(name="STACK"))
    EntityDataType staticDataTypeItem();
}
