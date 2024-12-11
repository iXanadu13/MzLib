package mz.mzlib.example;

import mz.mzlib.util.compound.Compound;
import mz.mzlib.util.compound.CompoundOverride;
import mz.mzlib.util.compound.Delegator;
import mz.mzlib.util.wrapper.*;

public class ExampleCompound
{
    public static class Foo
    {
        int var;
        public Foo(int var)
        {
            this.var=var;
        }
        public void f()
        {
            System.out.println("Hello World");
        }
        public void g()
        {
            f();
            System.out.println(this.var);
        }
    }

    @WrapClass(Foo.class)
    public interface WrapperFoo extends WrapperObject
    {
        @WrapperCreator
        static WrapperFoo create(Object wrapped)
        {
            return WrapperObject.create(WrapperFoo.class, wrapped);
        }

        @WrapMethod("f")
        void f();

        @WrapMethod("g")
        void g();
    }

    @Compound
    public interface CompoundFoo extends WrapperFoo, Delegator
    {
        @WrapperCreator
        static CompoundFoo create(Object wrapped)
        {
            return WrapperObject.create(CompoundFoo.class, wrapped);
        }

        static CompoundFoo newInstance(Foo delegate)
        {
            return Delegator.newInstance(CompoundFoo::create,delegate);
        }

        @CompoundOverride("f")
        default void f()
        {
            System.out.println("Fuck you World");
        }
    }

    public static void main(String[] args)
    {
        CompoundFoo.newInstance(new Foo(114)).g();
    }
}