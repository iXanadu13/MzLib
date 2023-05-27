package mz.mzlib.util;

import java.io.InputStream;

public class IOUtil
{
	private IOUtil() {}
	
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static byte[] readAll(InputStream stream)
	{
		try
		{
			byte[] result=new byte[stream.available()];
			if(stream.read(result)<result.length||RuntimeUtil.runAndCatch(stream::read)==null)
				throw new AssertionError();
			return result;
		}
		catch(Throwable e)
		{
			throw RuntimeUtil.forceThrow(e);
		}
	}
}