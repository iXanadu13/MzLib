package mz.mzlib.example;

import mz.mzlib.MzLib;
import mz.mzlib.util.delegator.*;

public class ExampleDelegator
{
	public static class Test
	{
		private final double var=114.514;
		private final Test thiz=this;
		private Test()
		{
		}
		private void m()
		{
			System.out.println("HelloWorld");
		}
		private static void m1()
		{
			System.out.println("HelloWorld1");
		}
	}
	
	@DelegatorClass(Test.class)
	public interface TestDelegator extends Delegator
	{
		@DelegatorConstructor
		TestDelegator staticConstruct();
		static TestDelegator construct()
		{
			return Delegator.createStatic(TestDelegator.class).staticConstruct();
		}
		
		@DelegatorFieldAccessor("var")
		double getVar();
		@DelegatorFieldAccessor("var")
		void setVar(double var);
		
		@DelegatorFieldAccessor("thiz")
		TestDelegator getThiz();
		@DelegatorFieldAccessor("@0")
		TestDelegator ditto();
		
		@DelegatorMethod("m")
		void m();
		@DelegatorMethod("m1")
		void staticM1();
		static void m1()
		{
			Delegator.createStatic(TestDelegator.class).staticM1();
		}
	}
	
	public static void main(String[] args)
	{
		MzLib.instance.load();
		
		TestDelegator t=TestDelegator.construct().getThiz();
		System.out.println(t.getVar());
		t.setVar(191981.0);
		System.out.println(t.getVar());
		t.m();
		TestDelegator.m1();
	}
}