package mz.mzlib.util.delegator.basic;

import mz.mzlib.util.delegator.Delegator;
import mz.mzlib.util.delegator.DelegatorClass;

@DelegatorClass(boolean.class)
public interface BooleanDelegator extends Delegator
{
	@Override
	Boolean getDelegate();
}